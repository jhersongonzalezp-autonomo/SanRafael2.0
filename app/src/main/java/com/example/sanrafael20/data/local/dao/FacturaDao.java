package com.example.sanrafael20.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.sanrafael20.data.local.dto.ConsultaFacturable;
import com.example.sanrafael20.data.local.entity.FacturaEntity;

@Dao
public interface FacturaDao {

    /** Primera consulta Completada del paciente que todavía no tiene factura. */
    @Query("SELECT c.id_consulta AS idConsulta, c.fecha_consulta AS fechaConsulta, "
            + "p.id_paciente AS idPaciente, p.nombre AS nombrePaciente, p.apellido AS apellidoPaciente, "
            + "p.identidad AS identidad, h.numero_historia AS numeroHistoria, "
            + "e.nombre AS nombreMedico, e.apellido AS apellidoMedico, "
            + "cm.valor_consulta AS valorConsulta "
            + "FROM Consulta c "
            + "JOIN Paciente p ON p.id_paciente = c.id_paciente "
            + "JOIN Empleado e ON e.id_empleado = c.id_empleado "
            + "JOIN CentroMedico cm ON cm.id_centro = c.id_centro "
            + "LEFT JOIN HistoriaClinica h ON h.id_paciente = p.id_paciente "
            + "WHERE c.id_paciente = :idPaciente AND c.estado = 'Completada' "
            + "AND c.id_consulta NOT IN (SELECT id_consulta FROM Factura) "
            + "ORDER BY c.fecha_consulta LIMIT 1")
    ConsultaFacturable buscarConsultaPorFacturar(long idPaciente);

    @Query("SELECT COALESCE(MAX(id_factura), 0) FROM Factura")
    long ultimoId();

    @Insert
    long insertar(FacturaEntity factura);
}
