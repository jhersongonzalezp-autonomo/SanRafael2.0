package com.example.sanrafael20.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.sanrafael20.data.local.AppDatabase;
import com.example.sanrafael20.data.local.entity.CentroMedicoEntity;
import com.example.sanrafael20.data.local.entity.HistoriaClinicaEntity;
import com.example.sanrafael20.data.local.entity.PacienteEntity;
import com.example.sanrafael20.util.Fechas;

import java.util.List;

public class PacienteRepository {

    private final AppDatabase db;

    public PacienteRepository(Application app) {
        db = AppDatabase.getInstance(app);
    }

    public LiveData<List<PacienteEntity>> observarTodos() {
        return db.pacienteDao().observarTodos();
    }

    public LiveData<PacienteEntity> observarPorId(long idPaciente) {
        return db.pacienteDao().observarPorId(idPaciente);
    }

    /**
     * Guarda el paciente y abre su historia clínica en una sola transacción:
     * si algo falla, no queda ni el paciente ni la historia. Llamar en segundo plano.
     */
    public long registrar(PacienteEntity paciente, String numeroHistoria) {
        return db.runInTransaction(() -> {
            if (db.pacienteDao().buscarPorIdentidad(paciente.getIdentidad()) != null) {
                throw new IllegalArgumentException("Ya existe un paciente con ese documento");
            }
            if (db.historiaClinicaDao().contarPorNumero(numeroHistoria) > 0) {
                throw new IllegalArgumentException("Ese número de historia ya está en uso");
            }
            CentroMedicoEntity centro = db.centroMedicoDao().buscarPrincipal();
            paciente.setIdCentro(centro == null ? null : centro.getIdCentro());
            long idPaciente = db.pacienteDao().insertar(paciente);

            HistoriaClinicaEntity historia = new HistoriaClinicaEntity();
            historia.setIdPaciente(idPaciente);
            historia.setNumeroHistoria(numeroHistoria);
            historia.setFechaApertura(Fechas.hoy());
            db.historiaClinicaDao().insertar(historia);
            return idPaciente;
        });
    }
}
