package com.example.sanrafael20.ui.viewmodel;

import android.app.Application;
import android.database.sqlite.SQLiteConstraintException;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sanrafael20.data.local.AppDatabase;
import com.example.sanrafael20.data.local.entity.PacienteEntity;
import com.example.sanrafael20.data.repository.PacienteRepository;
import com.example.sanrafael20.util.Resultado;

import java.util.List;

public class PacienteViewModel extends AndroidViewModel {

    private final PacienteRepository repositorio;
    private final MutableLiveData<Resultado<Long>> registro = new MutableLiveData<>();

    public PacienteViewModel(@NonNull Application app) {
        super(app);
        repositorio = new PacienteRepository(app);
    }

    public LiveData<List<PacienteEntity>> getPacientes() { return repositorio.observarTodos(); }
    public LiveData<Resultado<Long>> getRegistro() { return registro; }

    public void registrar(PacienteEntity paciente, String numeroHistoria) {
        AppDatabase.EJECUTOR.execute(() -> {
            try {
                long id = repositorio.registrar(paciente, numeroHistoria);
                registro.postValue(Resultado.exito(id));
            } catch (IllegalArgumentException e) {
                registro.postValue(Resultado.error(e.getMessage()));
            } catch (SQLiteConstraintException e) {
                registro.postValue(Resultado.error("La base rechazó los datos: revisa edad y estatura"));
            }
        });
    }
}
