package com.example.sanrafael20.util;

import android.content.Context;
import android.content.SharedPreferences;

/** Guarda quién inició sesión (id, nombre y rol) en SharedPreferences. */
public class SesionManager {

    private static final String ARCHIVO = "sesion_centro_medico";
    private static final String ID = "id_empleado";
    private static final String NOMBRE = "nombre";
    private static final String ROL = "rol";

    private final SharedPreferences prefs;

    public SesionManager(Context context) {
        prefs = context.getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE);
    }

    public void iniciar(long idEmpleado, String nombre, String rol) {
        prefs.edit()
                .putLong(ID, idEmpleado)
                .putString(NOMBRE, nombre)
                .putString(ROL, rol)
                .apply();
    }

    public boolean estaActiva() { return prefs.contains(ID) && getRol() != null; }
    public long getIdEmpleado() { return prefs.getLong(ID, -1); }
    public String getNombre() { return prefs.getString(NOMBRE, ""); }
    public String getRol() { return prefs.getString(ROL, null); }

    public void cerrar() { prefs.edit().clear().apply(); }
}
