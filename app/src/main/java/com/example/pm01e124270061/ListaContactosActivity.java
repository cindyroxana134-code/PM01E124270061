package com.example.pm01e124270061;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class ListaContactosActivity extends AppCompatActivity {

    private static final int CODIGO_PERMISO_LLAMADA = 100;

    private DBHelper dbHelper;
    private ListView listaContactos;
    private ArrayList<Contactos> contactos;
    private Contactos contactoSeleccionado;
    private String telefonoPendiente = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_contactos);

        listaContactos = findViewById(R.id.listaContactos);

        Button btnVolver = findViewById(R.id.btnVolver);
        Button btnLlamar = findViewById(R.id.btnLlamar);
        Button btnEliminar = findViewById(R.id.btnEliminar);
        Button btnCompartir = findViewById(R.id.btnCompartir);
        Button btnVerImagen = findViewById(R.id.btnVerImagen);
        Button btnActualizar = findViewById(R.id.btnActualizar);

        dbHelper = new DBHelper(this);

        btnVolver.setOnClickListener(v -> finish());

        listaContactos.setOnItemClickListener(
                (parent, view, position, id) -> {
                    contactoSeleccionado = contactos.get(position);

                    Toast.makeText(
                            this,
                            "Seleccionado: "
                                    + contactoSeleccionado.getNombre(),
                            Toast.LENGTH_SHORT
                    ).show();
                }
        );

        btnLlamar.setOnClickListener(v -> {
            if (hayContactoSeleccionado()) {
                Intent intent = new Intent(
                        ListaContactosActivity.this,
                        DetalleContactoActivity.class
                );

                intent.putExtra("nombre",
                        contactoSeleccionado.getNombre());
                intent.putExtra("telefono",
                        contactoSeleccionado.getTelefono());
                intent.putExtra("pais",
                        contactoSeleccionado.getPais());
                intent.putExtra("nota",
                        contactoSeleccionado.getNota());
                intent.putExtra("imagen",
                        contactoSeleccionado.getImagen());

                startActivity(intent);
            }
        });


        btnEliminar.setOnClickListener(v -> {
            if (hayContactoSeleccionado()) {
                confirmarEliminar();
            }
        });

        btnCompartir.setOnClickListener(v -> {
            if (hayContactoSeleccionado()) {
                compartirContacto();
            }
        });

        btnVerImagen.setOnClickListener(v -> {
            if (hayContactoSeleccionado()) {
                verImagen();
            }
        });

        btnActualizar.setOnClickListener(v -> {
            if (hayContactoSeleccionado()) {
                Toast.makeText(
                        this,
                        "La función Actualizar está pendiente.",
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarContactos();
    }

    private void cargarContactos() {
        contactos = dbHelper.obtenerContactos();

        ArrayList<String> nombres = new ArrayList<>();

        for (Contactos contacto : contactos) {
            nombres.add(
                    contacto.getNombre() + "\n"
                            + contacto.getPais() + " - "
                            + contacto.getTelefono()
            );
        }

        ArrayAdapter<String> adaptador = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                nombres
        );

        listaContactos.setAdapter(adaptador);
    }

    private boolean hayContactoSeleccionado() {
        if (contactoSeleccionado == null) {
            Toast.makeText(
                    this,
                    "Primero selecciona un contacto de la lista.",
                    Toast.LENGTH_SHORT
            ).show();
            return false;
        }

        return true;
    }

    private void confirmarLlamada(Contactos contacto) {
        new AlertDialog.Builder(this)
                .setTitle("Llamar a " + contacto.getNombre())
                .setMessage(
                        "¿Deseas llamar al número "
                                + contacto.getTelefono() + "?"
                )
                .setPositiveButton("Sí, llamar", (dialog, which) -> {
                    telefonoPendiente = contacto.getTelefono();
                    comprobarPermisoYLlamar();
                })
                .setNegativeButton("No", (dialog, which) ->
                        dialog.dismiss())
                .show();
    }

    private void comprobarPermisoYLlamar() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED) {
            realizarLlamada();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    CODIGO_PERMISO_LLAMADA
            );
        }
    }

    private void realizarLlamada() {
        if (telefonoPendiente == null
                || telefonoPendiente.trim().isEmpty()) {
            Toast.makeText(
                    this,
                    "El teléfono no es válido.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        Intent intent = new Intent(
                Intent.ACTION_CALL,
                Uri.parse("tel:" + telefonoPendiente)
        );

        try {
            startActivity(intent);
        } catch (SecurityException e) {
            Toast.makeText(
                    this,
                    "No se concedió el permiso para llamar.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults
    ) {
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults
        );

        if (requestCode == CODIGO_PERMISO_LLAMADA) {
            if (grantResults.length > 0
                    && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED) {
                realizarLlamada();
            } else {
                Toast.makeText(
                        this,
                        "Permiso de llamada denegado.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }

    private void confirmarEliminar() {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar contacto")
                .setMessage(
                        "¿Deseas eliminar a "
                                + contactoSeleccionado.getNombre() + "?"
                )
                .setPositiveButton("Sí", (dialog, which) -> {

                    int eliminado = dbHelper.eliminarContacto(
                            contactoSeleccionado.getId()
                    );

                    if (eliminado > 0) {
                        Toast.makeText(
                                this,
                                "Contacto eliminado.",
                                Toast.LENGTH_SHORT
                        ).show();

                        contactoSeleccionado = null;
                        cargarContactos();

                    } else {
                        Toast.makeText(
                                this,
                                "No se pudo eliminar el contacto.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                })
                .setNegativeButton("No", (dialog, which) ->
                        dialog.dismiss())
                .show();
    }

    private void compartirContacto() {
        String texto = "Nombre: "
                + contactoSeleccionado.getNombre()
                + "\nPaís: " + contactoSeleccionado.getPais()
                + "\nTeléfono: " + contactoSeleccionado.getTelefono()
                + "\nNota: " + contactoSeleccionado.getNota();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, texto);

        startActivity(Intent.createChooser(
                intent,
                "Compartir contacto"
        ));
    }

    private void verImagen() {
        String imagen = contactoSeleccionado.getImagen();

        if (imagen == null || imagen.trim().isEmpty()) {
            Toast.makeText(
                    this,
                    "Este contacto no tiene imagen guardada.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        try {
            Intent intent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(imagen)
            );
            startActivity(intent);

        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "No se pudo abrir la imagen.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}


