package com.example.sanrafael20.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/** Tabla Factura: una por consulta completada, emitida por un administrativo. */
@Entity(tableName = "Factura",
        foreignKeys = {
                @ForeignKey(entity = ConsultaEntity.class,
                        parentColumns = "id_consulta", childColumns = "id_consulta",
                        onDelete = ForeignKey.RESTRICT),
                @ForeignKey(entity = PacienteEntity.class,
                        parentColumns = "id_paciente", childColumns = "id_paciente",
                        onDelete = ForeignKey.RESTRICT),
                @ForeignKey(entity = EmpleadoEntity.class,
                        parentColumns = "id_empleado", childColumns = "id_empleado",
                        onDelete = ForeignKey.RESTRICT)
        },
        indices = {
                @Index(value = "numero_factura", unique = true),
                @Index(value = "id_consulta", unique = true),
                @Index("id_paciente"),
                @Index("id_empleado")
        })
public class FacturaEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_factura")
    private long idFactura;

    @NonNull
    @ColumnInfo(name = "numero_factura")
    private String numeroFactura = ""; // FAC-000001

    @ColumnInfo(name = "id_consulta")
    private long idConsulta;

    @ColumnInfo(name = "id_paciente")
    private long idPaciente;

    @ColumnInfo(name = "id_empleado")
    private long idEmpleado; // administrativo que emite

    @NonNull
    @ColumnInfo(name = "fecha_emision")
    private String fechaEmision = ""; // AAAA-MM-DD HH:mm

    @NonNull
    @ColumnInfo(name = "concepto")
    private String concepto = "";

    @ColumnInfo(name = "valor")
    private double valor;

    @NonNull
    @ColumnInfo(name = "estado")
    private String estado = ""; // Emitida o Anulada

    public long getIdFactura() { return idFactura; }
    public void setIdFactura(long idFactura) { this.idFactura = idFactura; }
    @NonNull public String getNumeroFactura() { return numeroFactura; }
    public void setNumeroFactura(@NonNull String numeroFactura) { this.numeroFactura = numeroFactura; }
    public long getIdConsulta() { return idConsulta; }
    public void setIdConsulta(long idConsulta) { this.idConsulta = idConsulta; }
    public long getIdPaciente() { return idPaciente; }
    public void setIdPaciente(long idPaciente) { this.idPaciente = idPaciente; }
    public long getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(long idEmpleado) { this.idEmpleado = idEmpleado; }
    @NonNull public String getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(@NonNull String fechaEmision) { this.fechaEmision = fechaEmision; }
    @NonNull public String getConcepto() { return concepto; }
    public void setConcepto(@NonNull String concepto) { this.concepto = concepto; }
    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }
    @NonNull public String getEstado() { return estado; }
    public void setEstado(@NonNull String estado) { this.estado = estado; }
}
