package com.example.sanrafael20.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/** Tabla SignosVitales: una toma de signos registrada por enfermería. */
@Entity(tableName = "SignosVitales",
        foreignKeys = {
                @ForeignKey(entity = HistoriaClinicaEntity.class,
                        parentColumns = "id_historia", childColumns = "id_historia",
                        onDelete = ForeignKey.RESTRICT),
                @ForeignKey(entity = EmpleadoEntity.class,
                        parentColumns = "id_empleado", childColumns = "id_empleado",
                        onDelete = ForeignKey.RESTRICT),
                @ForeignKey(entity = ConsultaEntity.class,
                        parentColumns = "id_consulta", childColumns = "id_consulta",
                        onDelete = ForeignKey.SET_NULL)
        },
        indices = {@Index("id_historia"), @Index("id_empleado"), @Index("id_consulta")})
public class SignosVitalesEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_signos")
    private long idSignos;

    @ColumnInfo(name = "id_historia")
    private long idHistoria;

    @ColumnInfo(name = "id_empleado")
    private long idEmpleado; // enfermero que toma los signos

    @ColumnInfo(name = "id_consulta")
    private Long idConsulta; // null si no hay una cita abierta

    @NonNull
    @ColumnInfo(name = "fecha_hora_toma")
    private String fechaHoraToma = ""; // AAAA-MM-DD HH:mm

    @ColumnInfo(name = "presion_sistolica")
    private Integer presionSistolica; // 40 a 300 mmHg

    @ColumnInfo(name = "presion_diastolica")
    private Integer presionDiastolica; // 20 a 200 mmHg

    @ColumnInfo(name = "frecuencia_cardiaca")
    private Integer frecuenciaCardiaca; // 20 a 300 lpm

    @ColumnInfo(name = "frecuencia_respiratoria")
    private Integer frecuenciaRespiratoria; // 4 a 100 rpm

    @ColumnInfo(name = "temperatura")
    private Double temperatura; // 25 a 45 °C

    @ColumnInfo(name = "saturacion_oxigeno")
    private Integer saturacionOxigeno; // 0 a 100 %

    @ColumnInfo(name = "nivel_triage")
    private String nivelTriage; // Rojo, Naranja, Amarillo, Verde o Azul

    @ColumnInfo(name = "observaciones")
    private String observaciones;

    public long getIdSignos() { return idSignos; }
    public void setIdSignos(long idSignos) { this.idSignos = idSignos; }
    public long getIdHistoria() { return idHistoria; }
    public void setIdHistoria(long idHistoria) { this.idHistoria = idHistoria; }
    public long getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(long idEmpleado) { this.idEmpleado = idEmpleado; }
    public Long getIdConsulta() { return idConsulta; }
    public void setIdConsulta(Long idConsulta) { this.idConsulta = idConsulta; }
    @NonNull public String getFechaHoraToma() { return fechaHoraToma; }
    public void setFechaHoraToma(@NonNull String fechaHoraToma) { this.fechaHoraToma = fechaHoraToma; }
    public Integer getPresionSistolica() { return presionSistolica; }
    public void setPresionSistolica(Integer presionSistolica) { this.presionSistolica = presionSistolica; }
    public Integer getPresionDiastolica() { return presionDiastolica; }
    public void setPresionDiastolica(Integer presionDiastolica) { this.presionDiastolica = presionDiastolica; }
    public Integer getFrecuenciaCardiaca() { return frecuenciaCardiaca; }
    public void setFrecuenciaCardiaca(Integer frecuenciaCardiaca) { this.frecuenciaCardiaca = frecuenciaCardiaca; }
    public Integer getFrecuenciaRespiratoria() { return frecuenciaRespiratoria; }
    public void setFrecuenciaRespiratoria(Integer frecuenciaRespiratoria) { this.frecuenciaRespiratoria = frecuenciaRespiratoria; }
    public Double getTemperatura() { return temperatura; }
    public void setTemperatura(Double temperatura) { this.temperatura = temperatura; }
    public Integer getSaturacionOxigeno() { return saturacionOxigeno; }
    public void setSaturacionOxigeno(Integer saturacionOxigeno) { this.saturacionOxigeno = saturacionOxigeno; }
    public String getNivelTriage() { return nivelTriage; }
    public void setNivelTriage(String nivelTriage) { this.nivelTriage = nivelTriage; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
