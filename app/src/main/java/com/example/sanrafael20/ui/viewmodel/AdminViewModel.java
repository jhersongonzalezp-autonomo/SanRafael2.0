package com.example.sanrafael20.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sanrafael20.data.local.AppDatabase;
import com.example.sanrafael20.data.local.dto.ConsultaConPaciente;
import com.example.sanrafael20.data.local.entity.ConsultaEntity;
import com.example.sanrafael20.data.local.entity.EmpleadoEntity;
import com.example.sanrafael20.data.local.entity.PacienteEntity;
import com.example.sanrafael20.util.Resultado;

import java.util.List;

public class AdminViewModel extends AndroidViewModel {

    private final AppDatabase db;
    private final MutableLiveData<Resultado<Long>> citaGuardada = new MutableLiveData<>();

    public AdminViewModel(@NonNull Application app) {
        super(app);
        db = AppDatabase.getInstance(app);
    }

    public LiveData<List<PacienteEntity>> getPacientes() {
        return db.pacienteDao().observarTodos();
    }

    public LiveData<List<EmpleadoEntity>> getMedicos() {
        return db.empleadoDao().observarMedicos();
    }

    public LiveData<Integer> getTotalPacientes() {
        return db.pacienteDao().observarCantidad();
    }

    public LiveData<Integer> getTotalConsultas() {
        return db.consultaDao().observarCantidad();
    }

    public LiveData<Integer> getTotalEmpleados() {
        return db.empleadoDao().observarCantidad();
    }

    public LiveData<List<ConsultaConPaciente>> getProximasConsultas() {
        return db.consultaDao().observarProximas();
    }

    public LiveData<Resultado<Long>> getCitaGuardada() {
        return citaGuardada;
    }

    public void agendarCita(long idPaciente, long idMedico, String fecha, String motivo) {
        AppDatabase.EJECUTOR.execute(() -> {
            try {
                ConsultaEntity cita = new ConsultaEntity();
                cita.setIdCentro(1);
                cita.setIdPaciente(idPaciente);
                cita.setIdEmpleado(idMedico);
                cita.setFechaConsulta(fecha);
                cita.setEstado("Pendiente");
                cita.setMotivo(motivo.isEmpty() ? "Consulta general" : motivo);

                long idCita = db.consultaDao().insertar(cita);
                citaGuardada.postValue(Resultado.exito(idCita));
            } catch (Exception e) {
                citaGuardada.postValue(Resultado.error("Error al agendar la cita: " + e.getMessage()));
            }
        });
    }
}
