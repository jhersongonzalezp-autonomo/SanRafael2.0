package com.example.sanrafael20.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

/** Tabla Consulta: una cita entre un paciente y un médico en una fecha. */
@Entity(tableName = "Consulta",
        foreignKeys = {
                @ForeignKey(entity = CentroMedicoEntity.class,
                        parentColumns = "id_centro", childColumns = "id_centro",
                        onDelete = ForeignKey.RESTRICT),
                @ForeignKey(entity = PacienteEntity.class,
                        parentColumns = "id_paciente", childColumns = "id_paciente",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = EmpleadoEntity.class,
                        parentColumns = "id_empleado", childColumns = "id_empleado",
                        onDelete = ForeignKey.RESTRICT)
        })
public class ConsultaEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_consulta")
    private long idConsulta;

    @ColumnInfo(name = "id_centro")
    private long idCentro;

    @ColumnInfo(name = "id_paciente")
    private long idPaciente;

    @ColumnInfo(name = "id_empleado")
    private long idEmpleado; // médico que atiende

    @ColumnInfo(name = "fecha_consulta")
    private String fechaConsulta; // AAAA-MM-DD

    @ColumnInfo(name = "estado")
    private String estado; // Pendiente, Confirmada, Cancelada o Completada

    @ColumnInfo(name = "motivo")
    private String motivo;

    public long getIdConsulta() { return idConsulta; }
    public void setIdConsulta(long idConsulta) { this.idConsulta = idConsulta; }
    public long getIdCentro() { return idCentro; }
    public void setIdCentro(long idCentro) { this.idCentro = idCentro; }
    public long getIdPaciente() { return idPaciente; }
    public void setIdPaciente(long idPaciente) { this.idPaciente = idPaciente; }
    public long getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(long idEmpleado) { this.idEmpleado = idEmpleado; }
    public String getFechaConsulta() { return fechaConsulta; }
    public void setFechaConsulta(String fechaConsulta) { this.fechaConsulta = fechaConsulta; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
