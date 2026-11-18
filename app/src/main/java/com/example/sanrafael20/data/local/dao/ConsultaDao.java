package com.example.sanrafael20.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.sanrafael20.data.local.dto.ConsultaConPaciente;
import com.example.sanrafael20.data.local.entity.ConsultaEntity;
import com.example.sanrafael20.data.local.entity.PacienteEntity;

import java.util.List;

@Dao
public interface ConsultaDao {

    @Insert
    long insertar(ConsultaEntity consulta);

    /** Agenda de un médico con el nombre del paciente (JOIN entre Consulta y Paciente). */
    @Query("SELECT c.id_consulta AS idConsulta, c.id_paciente AS idPaciente, "
            + "c.fecha_consulta AS fecha, c.estado AS estado, c.motivo AS motivo, "
            + "p.nombre AS nombrePaciente, p.apellido AS apellidoPaciente "
            + "FROM Consulta c JOIN Paciente p ON p.id_paciente = c.id_paciente "
            + "WHERE c.id_empleado = :idMedico ORDER BY c.fecha_consulta")
    LiveData<List<ConsultaConPaciente>> observarAgenda(long idMedico);

    /** Pacientes que tienen citas asignadas a este médico en concreto. */
    @Query("SELECT DISTINCT p.* FROM Paciente p JOIN Consulta c ON p.id_paciente = c.id_paciente WHERE c.id_empleado = :idMedico ORDER BY p.apellido, p.nombre")
    LiveData<List<PacienteEntity>> observarPacientesPorMedico(long idMedico);

    /** Cita abierta (Pendiente o Confirmada) del paciente en una fecha AAAA-MM-DD. */
    @Query("SELECT * FROM Consulta WHERE id_paciente = :idPaciente AND fecha_consulta = :fecha "
            + "AND estado IN ('Pendiente', 'Confirmada') ORDER BY id_consulta LIMIT 1")
    ConsultaEntity buscarCitaAbierta(long idPaciente, String fecha);

    /** Detalle de una consulta específica con datos del paciente. */
    @Query("SELECT c.id_consulta AS idConsulta, c.id_paciente AS idPaciente, "
            + "c.fecha_consulta AS fecha, c.estado AS estado, c.motivo AS motivo, "
            + "p.nombre AS nombrePaciente, p.apellido AS apellidoPaciente "
            + "FROM Consulta c JOIN Paciente p ON p.id_paciente = c.id_paciente "
            + "WHERE c.id_consulta = :idConsulta LIMIT 1")
    LiveData<ConsultaConPaciente> observarPorId(long idConsulta);

    /** Historial de consultas de un paciente. */
    @Query("SELECT c.id_consulta AS idConsulta, c.id_paciente AS idPaciente, "
            + "c.fecha_consulta AS fecha, c.estado AS estado, c.motivo AS motivo, "
            + "p.nombre AS nombrePaciente, p.apellido AS apellidoPaciente "
            + "FROM Consulta c JOIN Paciente p ON p.id_paciente = c.id_paciente "
            + "WHERE c.id_paciente = :idPaciente ORDER BY c.fecha_consulta DESC")
    LiveData<List<ConsultaConPaciente>> observarHistorialPaciente(long idPaciente);

    /** Actualiza el estado de la consulta. */
    @Query("UPDATE Consulta SET estado = :estado WHERE id_consulta = :idConsulta")
    void actualizarEstado(long idConsulta, String estado);

    /** Primera consulta abierta de cualquier paciente o del médico para iniciarla rápidamente. */
    @Query("SELECT c.id_consulta AS idConsulta, c.id_paciente AS idPaciente, "
            + "c.fecha_consulta AS fecha, c.estado AS estado, c.motivo AS motivo, "
            + "p.nombre AS nombrePaciente, p.apellido AS apellidoPaciente "
            + "FROM Consulta c JOIN Paciente p ON p.id_paciente = c.id_paciente "
            + "ORDER BY c.id_consulta LIMIT 1")
    LiveData<ConsultaConPaciente> observarPrimera();

    /** Próximas consultas para la vista administrativa. */
    @Query("SELECT c.id_consulta AS idConsulta, c.id_paciente AS idPaciente, "
            + "c.fecha_consulta AS fecha, c.estado AS estado, c.motivo AS motivo, "
            + "p.nombre AS nombrePaciente, p.apellido AS apellidoPaciente "
            + "FROM Consulta c JOIN Paciente p ON p.id_paciente = c.id_paciente "
            + "ORDER BY c.fecha_consulta DESC LIMIT 10")
    LiveData<List<ConsultaConPaciente>> observarProximas();

    /** Total de consultas registradas. */
    @Query("SELECT COUNT(*) FROM Consulta")
    LiveData<Integer> observarCantidad();
}
