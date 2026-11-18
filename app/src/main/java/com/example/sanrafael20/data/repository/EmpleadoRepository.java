package com.example.sanrafael20.data.repository;

import android.app.Application;

import com.example.sanrafael20.data.local.AppDatabase;
import com.example.sanrafael20.data.local.dao.EmpleadoDao;
import com.example.sanrafael20.data.local.entity.EmpleadoEntity;
import com.example.sanrafael20.util.PasswordUtils;

public class EmpleadoRepository {

    private final EmpleadoDao empleadoDao;

    public EmpleadoRepository(Application app) {
        empleadoDao = AppDatabase.getInstance(app).empleadoDao();
    }

    /**
     * Busca al empleado por correo o por ID y compara el hash de la contraseña.
     * Devuelve null si no existe o la contraseña no coincide. Llamar en segundo plano.
     */
    public EmpleadoEntity autenticar(String identificador, String clave) {
        EmpleadoEntity empleado;
        if (identificador.contains("@")) {
            empleado = empleadoDao.buscarPorEmail(identificador);
        } else {
            try {
                empleado = empleadoDao.buscarPorId(Long.parseLong(identificador));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        if (empleado == null || !PasswordUtils.coincide(clave, empleado.getHashPassword())) {
            return null;
        }
        return empleado;
    }
}
