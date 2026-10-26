
package com.example.pm01e124270061;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private EditText edtNombre, edtTelefono, edtNota;
    private Spinner spPais;
    private ImageView imgContacto;
    private DBHelper dbHelper;

    private String imagenSeleccionada = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);

        imgContacto = findViewById(R.id.imgContacto);
        spPais = findViewById(R.id.spPais);
        edtNombre = findViewById(R.id.edtNombre);
        edtTelefono = findViewById(R.id.edtTelefono);
        edtNota = findViewById(R.id.edtNota);

        Button btnGuardar = findViewById(R.id.btnGuardar);
        Button btnVerContactos = findViewById(R.id.btnVerContactos);

        String[] paises = {
                "Honduras",
                "Guatemala",
                "El Salvador",
                "Nicaragua",
                "Costa Rica",
                "Panamá",
                "México",
                "Estados Unidos",
                "España"
        };

        ArrayAdapter<String> adaptador = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                paises
        );

        adaptador.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spPais.setAdapter(adaptador);

        btnGuardar.setOnClickListener(v -> guardarContacto());

        btnVerContactos.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    ListaContactosActivity.class
            );
            startActivity(intent);
        });
    }

    private void guardarContacto() {
        String pais = spPais.getSelectedItem().toString();
        String nombre = edtNombre.getText().toString().trim();
        String telefono = edtTelefono.getText().toString().trim();
        String nota = edtNota.getText().toString().trim();

        if (nombre.isEmpty() || telefono.isEmpty() || nota.isEmpty()) {
            Toast.makeText(
                    this,
                    "Completa todos los campos.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (!Pattern.matches("[\\p{L} ]+", nombre)) {
            edtNombre.setError(
                    "Usa solo letras y espacios en el nombre."
            );
            return;
        }

        if (!Pattern.matches("[0-9]+", telefono)) {
            edtTelefono.setError(
                    "El teléfono debe contener solo números."
            );
            return;
        }

        Contactos contacto = new Contactos(
                pais,
                nombre,
                telefono,
                nota,
                imagenSeleccionada
        );

        long resultado = dbHelper.insertarContacto(contacto);

        if (resultado != -1) {
            Toast.makeText(
                    this,
                    "Contacto guardado correctamente.",
                    Toast.LENGTH_SHORT
            ).show();

            edtNombre.setText("");
            edtTelefono.setText("");
            edtNota.setText("");

            imagenSeleccionada = "";
            imgContacto.setImageResource(
                    android.R.drawable.ic_menu_camera
            );
        } else {
            Toast.makeText(
                    this,
                    "No se pudo guardar el contacto.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}
