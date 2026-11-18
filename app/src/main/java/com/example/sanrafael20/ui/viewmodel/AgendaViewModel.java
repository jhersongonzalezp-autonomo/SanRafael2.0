package com.example.sanrafael20.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.sanrafael20.data.local.dto.ConsultaConPaciente;
import com.example.sanrafael20.data.repository.ConsultaRepository;

import java.util.List;

public class AgendaViewModel extends AndroidViewModel {

    private final ConsultaRepository repositorio;
    private LiveData<List<ConsultaConPaciente>> agenda;

    public AgendaViewModel(@NonNull Application app) {
        super(app);
        repositorio = new ConsultaRepository(app);
    }

    /** La consulta se crea una sola vez; al girar la pantalla se reutiliza. */
    public LiveData<List<ConsultaConPaciente>> getAgenda(long idMedico) {
        if (agenda == null) {
            agenda = repositorio.observarAgenda(idMedico);
        }
        return agenda;
    }
}
