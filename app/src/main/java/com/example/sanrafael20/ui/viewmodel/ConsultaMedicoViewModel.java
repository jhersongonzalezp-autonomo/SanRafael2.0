package com.example.sanrafael20.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.sanrafael20.data.local.dto.ConsultaConPaciente;
import com.example.sanrafael20.data.local.entity.HistoriaClinicaEntity;
import com.example.sanrafael20.data.local.entity.PacienteEntity;
import com.example.sanrafael20.data.local.entity.SignosVitalesEntity;
import com.example.sanrafael20.data.repository.ConsultaRepository;

import java.util.List;

public class ConsultaMedicoViewModel extends AndroidViewModel {

    private final ConsultaRepository repositorio;

    public ConsultaMedicoViewModel(@NonNull Application app) {
        super(app);
        repositorio = new ConsultaRepository(app);
    }

    public LiveData<ConsultaConPaciente> getConsulta(long idConsulta) {
        return repositorio.observarConsulta(idConsulta);
    }

    public LiveData<PacienteEntity> getPaciente(long idPaciente) {
        return repositorio.observarPaciente(idPaciente);
    }

    public LiveData<List<PacienteEntity>> getTodosPacientes() {
        return repositorio.observarTodosPacientes();
    }

    public LiveData<List<PacienteEntity>> getPacientesPorMedico(long idMedico) {
        return repositorio.observarPacientesPorMedico(idMedico);
    }

    public LiveData<HistoriaClinicaEntity> getHistoriaClinica(long idPaciente) {
        return repositorio.observarHistoriaClinica(idPaciente);
    }

    public LiveData<List<ConsultaConPaciente>> getHistorialPaciente(long idPaciente) {
        return repositorio.observarHistorialPaciente(idPaciente);
    }

    public LiveData<List<SignosVitalesEntity>> getRegistrosPaciente(long idPaciente) {
        return repositorio.observarRegistrosPaciente(idPaciente);
    }

    public void finalizarConsulta(long idConsulta, long idPaciente, long idMedico,
                                  String diagnostico, String tratamiento, String indicaciones) {
        repositorio.finalizarConsulta(idConsulta, idPaciente, idMedico, diagnostico, tratamiento, indicaciones);
    }
}
