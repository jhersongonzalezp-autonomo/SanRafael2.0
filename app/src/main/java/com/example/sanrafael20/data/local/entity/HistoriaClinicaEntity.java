package com.example.sanrafael20.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/** Tabla HistoriaClinica: una por paciente. Los signos vitales cuelgan de ella. */
@Entity(tableName = "HistoriaClinica",
        foreignKeys = @ForeignKey(entity = PacienteEntity.class,
                parentColumns = "id_paciente", childColumns = "id_paciente",
                onDelete = ForeignKey.RESTRICT),
        indices = {
                @Index(value = "id_paciente", unique = true),
                @Index(value = "numero_historia", unique = true)
        })
public class HistoriaClinicaEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_historia")
    private long idHistoria;

    @ColumnInfo(name = "id_paciente")
    private long idPaciente;

    @NonNull
    @ColumnInfo(name = "numero_historia")
    private String numeroHistoria = "";

    @NonNull
    @ColumnInfo(name = "fecha_apertura")
    private String fechaApertura = ""; // AAAA-MM-DD

    public long getIdHistoria() { return idHistoria; }
    public void setIdHistoria(long idHistoria) { this.idHistoria = idHistoria; }
    public long getIdPaciente() { return idPaciente; }
    public void setIdPaciente(long idPaciente) { this.idPaciente = idPaciente; }
    @NonNull public String getNumeroHistoria() { return numeroHistoria; }
    public void setNumeroHistoria(@NonNull String numeroHistoria) { this.numeroHistoria = numeroHistoria; }
    @NonNull public String getFechaApertura() { return fechaApertura; }
    public void setFechaApertura(@NonNull String fechaApertura) { this.fechaApertura = fechaApertura; }
}
