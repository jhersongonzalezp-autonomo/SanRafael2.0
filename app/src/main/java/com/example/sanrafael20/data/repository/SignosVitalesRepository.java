package com.example.sanrafael20.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.sanrafael20.data.local.AppDatabase;
import com.example.sanrafael20.data.local.entity.ConsultaEntity;
import com.example.sanrafael20.data.local.entity.HistoriaClinicaEntity;
import com.example.sanrafael20.data.local.entity.SignosVitalesEntity;
import com.example.sanrafael20.util.Fechas;

public class SignosVitalesRepository {

    private final AppDatabase db;

    public SignosVitalesRepository(Application app) {
        db = AppDatabase.getInstance(app);
    }

    public LiveData<Integer> observarCantidadPorEmpleado(long idEmpleado) {
        return db.signosVitalesDao().observarCantidadPorEmpleado(idEmpleado);
    }

    public LiveData<java.util.List<SignosVitalesEntity>> observarRegistrosPaciente(long idPaciente) {
        return db.signosVitalesDao().observarPorPaciente(idPaciente);
    }

    /** Completa las llaves foráneas (historia, enfermero, cita de hoy) y guarda. Llamar en segundo plano. */
    public long registrar(long idPaciente, long idEnfermero, SignosVitalesEntity signos) {
        HistoriaClinicaEntity historia = db.historiaClinicaDao().buscarPorPaciente(idPaciente);
        if (historia == null) {
            historia = new HistoriaClinicaEntity();
            historia.setIdPaciente(idPaciente);
            historia.setNumeroHistoria("HC-2025-" + String.format(java.util.Locale.US, "%03d", idPaciente));
            historia.setFechaApertura(Fechas.hoy());
            long idH = db.historiaClinicaDao().insertar(historia);
            historia.setIdHistoria(idH);
        }
        ConsultaEntity citaDeHoy = db.consultaDao().buscarCitaAbierta(idPaciente, Fechas.hoy());

        signos.setIdHistoria(historia.getIdHistoria());
        signos.setIdEmpleado(idEnfermero > 0 ? idEnfermero : 1);
        signos.setIdConsulta(citaDeHoy == null ? null : citaDeHoy.getIdConsulta());
        signos.setFechaHoraToma(Fechas.ahora());
        return db.signosVitalesDao().insertar(signos);
    }
}
