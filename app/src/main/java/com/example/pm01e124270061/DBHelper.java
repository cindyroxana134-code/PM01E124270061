package com.example.pm01e124270061;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "contactos.db";
    private static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE contactos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "pais TEXT NOT NULL, " +
                "nombre TEXT NOT NULL, " +
                "telefono TEXT NOT NULL, " +
                "nota TEXT NOT NULL, " +
                "imagen TEXT)";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion, int newVersion) {
        // Aquí se agregarán las migraciones si cambia la base de datos.
    }

    public long insertarContacto(Contactos contacto) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put("pais", contacto.getPais());
        valores.put("nombre", contacto.getNombre());
        valores.put("telefono", contacto.getTelefono());
        valores.put("nota", contacto.getNota());
        valores.put("imagen", contacto.getImagen());

        long resultado = db.insert("contactos", null, valores);
        db.close();

        return resultado;
    }

    public ArrayList<Contactos> obtenerContactos() {
        ArrayList<Contactos> lista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM contactos ORDER BY nombre ASC",
                null
        );

        try {
            while (cursor.moveToNext()) {
                Contactos contacto = new Contactos(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("pais")),
                        cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                        cursor.getString(cursor.getColumnIndexOrThrow("telefono")),
                        cursor.getString(cursor.getColumnIndexOrThrow("nota")),
                        cursor.getString(cursor.getColumnIndexOrThrow("imagen"))
                );

                lista.add(contacto);
            }
        } finally {
            cursor.close();
            db.close();
        }

        return lista;
    }

    public int eliminarContacto(int id) {
        SQLiteDatabase db = getWritableDatabase();

        int resultado = db.delete(
                "contactos",
                "id = ?",
                new String[]{String.valueOf(id)}
        );

        db.close();
        return resultado;
    }
}
