package com.example.sanrafael20.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.sanrafael20.data.local.AppDatabase;
import com.example.sanrafael20.data.local.dao.ConsultaDao;
import com.example.sanrafael20.data.local.dao.HistoriaClinicaDao;
import com.example.sanrafael20.data.local.dao.PacienteDao;
import com.example.sanrafael20.data.local.dao.SignosVitalesDao;
import com.example.sanrafael20.data.local.dto.ConsultaConPaciente;
import com.example.sanrafael20.data.local.entity.HistoriaClinicaEntity;
import com.example.sanrafael20.data.local.entity.PacienteEntity;
import com.example.sanrafael20.data.local.entity.SignosVitalesEntity;
import com.example.sanrafael20.util.Fechas;

import java.time.LocalDate;
import java.util.List;

public class ConsultaRepository {

    private final ConsultaDao consultaDao;
    private final PacienteDao pacienteDao;
    private final HistoriaClinicaDao historiaClinicaDao;
    private final SignosVitalesDao signosVitalesDao;

    public ConsultaRepository(Application app) {
        AppDatabase db = AppDatabase.getInstance(app);
        consultaDao = db.consultaDao();
        pacienteDao = db.pacienteDao();
        historiaClinicaDao = db.historiaClinicaDao();
        signosVitalesDao = db.signosVitalesDao();
    }

    public LiveData<List<ConsultaConPaciente>> observarAgenda(long idMedico) {
        return consultaDao.observarAgenda(idMedico);
    }

    public LiveData<List<PacienteEntity>> observarPacientesPorMedico(long idMedico) {
        return consultaDao.observarPacientesPorMedico(idMedico);
    }

    public LiveData<ConsultaConPaciente> observarConsulta(long idConsulta) {
        if (idConsulta <= 0) {
            return consultaDao.observarPrimera();
        }
        return consultaDao.observarPorId(idConsulta);
    }

    public LiveData<PacienteEntity> observarPaciente(long idPaciente) {
        return pacienteDao.observarPorId(idPaciente);
    }

    public LiveData<List<PacienteEntity>> observarTodosPacientes() {
        return pacienteDao.observarTodos();
    }

    public LiveData<HistoriaClinicaEntity> observarHistoriaClinica(long idPaciente) {
        return historiaClinicaDao.observarPorPaciente(idPaciente);
    }

    public LiveData<List<ConsultaConPaciente>> observarHistorialPaciente(long idPaciente) {
        return consultaDao.observarHistorialPaciente(idPaciente);
    }

    public LiveData<List<SignosVitalesEntity>> observarRegistrosPaciente(long idPaciente) {
        return signosVitalesDao.observarPorPaciente(idPaciente);
    }

    public void finalizarConsulta(long idConsulta, long idPaciente, long idMedico,
                                  String diagnostico, String tratamiento, String indicaciones) {
        AppDatabase.EJECUTOR.execute(() -> {
            if (idConsulta > 0) {
                consultaDao.actualizarEstado(idConsulta, "Completada");
            }
            HistoriaClinicaEntity historia = historiaClinicaDao.buscarPorPaciente(idPaciente);
            long idHistoria = historia != null ? historia.getIdHistoria() : 1;

            SignosVitalesEntity registro = new SignosVitalesEntity();
            registro.setIdHistoria(idHistoria);
            registro.setIdEmpleado(idMedico > 0 ? idMedico : 1);
            if (idConsulta > 0) {
                registro.setIdConsulta(idConsulta);
            }
            registro.setFechaHoraToma(Fechas.FECHA.format(LocalDate.now()));

            StringBuilder obs = new StringBuilder();
            if (diagnostico != null && !diagnostico.trim().isEmpty()) {
                obs.append("Diagnóstico: ").append(diagnostico.trim());
            }
            if (tratamiento != null && !tratamiento.trim().isEmpty()) {
                if (obs.length() > 0) obs.append("\n");
                obs.append("Tratamiento: ").append(tratamiento.trim());
            }
            if (indicaciones != null && !indicaciones.trim().isEmpty()) {
                if (obs.length() > 0) obs.append("\n");
                obs.append("Indicaciones: ").append(indicaciones.trim());
            }
            registro.setObservaciones(obs.length() > 0 ? obs.toString() : "Consulta realizada");
            signosVitalesDao.insertar(registro);
        });
    }
}
