package com.example.pm01e124270061;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class DetalleContactoActivity extends AppCompatActivity {

    private static final int CODIGO_PERMISO_LLAMADA = 101;

    private String nombre;
    private String telefono;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_contacto);

        ImageView imgDetalle =
                findViewById(R.id.imgDetalleContacto);

        TextView txtNombre =
                findViewById(R.id.txtDetalleNombre);

        TextView txtTelefono =
                findViewById(R.id.txtDetalleTelefono);

        Button btnLlamar =
                findViewById(R.id.btnLlamarDetalle);

        Button btnCancelar =
                findViewById(R.id.btnCancelarLlamada);

        nombre = getIntent().getStringExtra("nombre");
        telefono = getIntent().getStringExtra("telefono");
        String imagen = getIntent().getStringExtra("imagen");

        if (nombre == null) {
            nombre = "Contacto";
        }

        if (telefono == null) {
            telefono = "";
        }

        txtNombre.setText(nombre);
        txtTelefono.setText(telefono);

        if (imagen != null && !imagen.trim().isEmpty()) {
            try {
                imgDetalle.setImageURI(Uri.parse(imagen));
            } catch (Exception e) {
                imgDetalle.setImageResource(
                        android.R.drawable.ic_menu_camera
                );
            }
        }

        // Cancelar vuelve a la lista de contactos.
        btnCancelar.setOnClickListener(v -> finish());

        // Llamar muestra la ventana de confirmación.
        btnLlamar.setOnClickListener(v -> confirmarLlamada());
    }

    private void confirmarLlamada() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar llamada")
                .setMessage(
                        "¿Deseas llamar a " + nombre
                                + "?\nNúmero: " + telefono
                )
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setPositiveButton("Llamar", (dialog, which) -> {
                    if (telefono.trim().isEmpty()) {
                        Toast.makeText(
                                this,
                                "El contacto no tiene teléfono.",
                                Toast.LENGTH_SHORT
                        ).show();
                    } else {
                        comprobarPermiso();
                    }
                })
                .show();
    }

    private void comprobarPermiso() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED) {

            realizarLlamada();

        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.CALL_PHONE
                    },
                    CODIGO_PERMISO_LLAMADA
            );
        }
    }

    private void realizarLlamada() {
        Intent intent = new Intent(
                Intent.ACTION_CALL,
                Uri.parse("tel:" + telefono)
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
                requestCode,
                permissions,
                grantResults
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
}


