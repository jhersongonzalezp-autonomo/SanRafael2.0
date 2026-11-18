package com.example.sanrafael20.ui.viewmodel;

import android.app.Application;
import android.database.sqlite.SQLiteConstraintException;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sanrafael20.data.local.AppDatabase;
import com.example.sanrafael20.data.local.dto.ConsultaFacturable;
import com.example.sanrafael20.data.local.entity.FacturaEntity;
import com.example.sanrafael20.data.local.entity.PacienteEntity;
import com.example.sanrafael20.data.repository.FacturaRepository;
import com.example.sanrafael20.data.repository.PacienteRepository;
import com.example.sanrafael20.util.Resultado;

import java.util.List;

public class FacturaViewModel extends AndroidViewModel {

    private final FacturaRepository repositorio;
    private final PacienteRepository pacientes;
    private final MutableLiveData<Resultado<ConsultaFacturable>> previa = new MutableLiveData<>();
    private final MutableLiveData<Resultado<FacturaEntity>> emitida = new MutableLiveData<>();

    public FacturaViewModel(@NonNull Application app) {
        super(app);
        repositorio = new FacturaRepository(app);
        pacientes = new PacienteRepository(app);
    }

    public LiveData<List<PacienteEntity>> getPacientes() { return pacientes.observarTodos(); }
    public LiveData<Resultado<ConsultaFacturable>> getPrevia() { return previa; }
    public LiveData<Resultado<FacturaEntity>> getEmitida() { return emitida; }

    public void prepararFactura(long idPaciente) {
        AppDatabase.EJECUTOR.execute(() -> {
            ConsultaFacturable consulta = repositorio.buscarConsultaPorFacturar(idPaciente);
            previa.postValue(consulta == null
                    ? Resultado.error("El paciente no tiene consultas completadas sin facturar")
                    : Resultado.exito(consulta));
        });
    }

    public void emitir(ConsultaFacturable consulta, long idAdministrativo) {
        AppDatabase.EJECUTOR.execute(() -> {
            try {
                emitida.postValue(Resultado.exito(repositorio.emitir(consulta, idAdministrativo)));
            } catch (SQLiteConstraintException e) {
                emitida.postValue(Resultado.error("Esa consulta ya tiene factura"));
            }
        });
    }
}
