package com.example.sanrafael20.ui.viewmodel;

import android.app.Application;
import android.database.sqlite.SQLiteConstraintException;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sanrafael20.data.local.AppDatabase;
import com.example.sanrafael20.data.local.entity.PacienteEntity;
import com.example.sanrafael20.data.local.entity.SignosVitalesEntity;
import com.example.sanrafael20.data.repository.PacienteRepository;
import com.example.sanrafael20.data.repository.SignosVitalesRepository;
import com.example.sanrafael20.util.Resultado;

public class SignosVitalesViewModel extends AndroidViewModel {

    private final SignosVitalesRepository repositorio;
    private final PacienteRepository pacientes;
    private final MutableLiveData<Resultado<Long>> guardado = new MutableLiveData<>();

    public SignosVitalesViewModel(@NonNull Application app) {
        super(app);
        repositorio = new SignosVitalesRepository(app);
        pacientes = new PacienteRepository(app);
    }

    public LiveData<PacienteEntity> getPaciente(long idPaciente) { return pacientes.observarPorId(idPaciente); }
    public LiveData<Integer> getCantidadRegistros(long idEnfermero) { return repositorio.observarCantidadPorEmpleado(idEnfermero); }
    public LiveData<Resultado<Long>> getGuardado() { return guardado; }

    public void guardar(long idPaciente, long idEnfermero, SignosVitalesEntity signos) {
        AppDatabase.EJECUTOR.execute(() -> {
            try {
                guardado.postValue(Resultado.exito(repositorio.registrar(idPaciente, idEnfermero, signos)));
            } catch (IllegalArgumentException e) {
                guardado.postValue(Resultado.error(e.getMessage()));
            } catch (SQLiteConstraintException e) {
                guardado.postValue(Resultado.error("Algún valor está fuera del rango permitido"));
            }
        });
    }
}
