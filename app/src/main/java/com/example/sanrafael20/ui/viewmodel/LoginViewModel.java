package com.example.sanrafael20.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sanrafael20.data.local.AppDatabase;
import com.example.sanrafael20.data.local.entity.CentroMedicoEntity;
import com.example.sanrafael20.data.local.entity.EmpleadoEntity;
import com.example.sanrafael20.data.repository.EmpleadoRepository;
import com.example.sanrafael20.util.Resultado;

public class LoginViewModel extends AndroidViewModel {

    private final EmpleadoRepository repositorio;
    private final LiveData<CentroMedicoEntity> centro;
    private final MutableLiveData<Resultado<EmpleadoEntity>> resultado = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application app) {
        super(app);
        repositorio = new EmpleadoRepository(app);
        centro = AppDatabase.getInstance(app).centroMedicoDao().observarPrincipal();
    }

    public LiveData<CentroMedicoEntity> getCentro() { return centro; }
    public LiveData<Resultado<EmpleadoEntity>> getResultado() { return resultado; }

    /** Autentica con id/correo y clave, determinando el rol automáticamente según el registro del empleado. */
    public void ingresar(String identificador, String clave) {
        AppDatabase.EJECUTOR.execute(() -> {
            EmpleadoEntity empleado = repositorio.autenticar(identificador, clave);
            if (empleado == null) {
                resultado.postValue(Resultado.error("ID/correo o contraseña incorrectos"));
            } else if (empleado.getFechaSalida() != null) {
                resultado.postValue(Resultado.error("El empleado ya no está activo"));
            } else {
                resultado.postValue(Resultado.exito(empleado));
            }
        });
    }

    /**
     * Versión anterior que validaba el rol seleccionado manualmente desde la tarjeta.
     * Se deja comentada según requerimiento.
     */
    public void ingresar(String identificador, String clave, String rolElegido) {
        /*
        AppDatabase.EJECUTOR.execute(() -> {
            EmpleadoEntity empleado = repositorio.autenticar(identificador, clave);
            if (empleado == null) {
                resultado.postValue(Resultado.error("ID/correo o contraseña incorrectos"));
            } else if (empleado.getFechaSalida() != null) {
                resultado.postValue(Resultado.error("El empleado ya no está activo"));
            } else if (!rolElegido.equals(empleado.getRol())) {
                resultado.postValue(Resultado.error("Tu usuario no tiene el rol " + rolElegido));
            } else {
                resultado.postValue(Resultado.exito(empleado));
            }
        });
        */
        ingresar(identificador, clave);
    }
}
