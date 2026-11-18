package com.example.sanrafael20.data.repository;

import android.app.Application;

import com.example.sanrafael20.data.local.AppDatabase;
import com.example.sanrafael20.data.local.dto.ConsultaFacturable;
import com.example.sanrafael20.data.local.entity.FacturaEntity;
import com.example.sanrafael20.util.Fechas;

import java.util.Locale;

public class FacturaRepository {

    private final AppDatabase db;

    public FacturaRepository(Application app) {
        db = AppDatabase.getInstance(app);
    }

    /** Llamar en segundo plano. Devuelve null si el paciente no tiene consultas por facturar. */
    public ConsultaFacturable buscarConsultaPorFacturar(long idPaciente) {
        return db.facturaDao().buscarConsultaPorFacturar(idPaciente);
    }

    /** Calcula el siguiente número y guarda la factura en una transacción. Llamar en segundo plano. */
    public FacturaEntity emitir(ConsultaFacturable consulta, long idAdministrativo) {
        return db.runInTransaction(() -> {
            long siguiente = db.facturaDao().ultimoId() + 1;

            FacturaEntity factura = new FacturaEntity();
            factura.setNumeroFactura(String.format(Locale.US, "FAC-%06d", siguiente));
            factura.setIdConsulta(consulta.idConsulta);
            factura.setIdPaciente(consulta.idPaciente);
            factura.setIdEmpleado(idAdministrativo);
            factura.setFechaEmision(Fechas.ahora());
            factura.setConcepto("Consulta médica del " + consulta.fechaConsulta);
            factura.setValor(consulta.valorConsulta);
            factura.setEstado("Emitida");

            factura.setIdFactura(db.facturaDao().insertar(factura));
            return factura;
        });
    }
}
