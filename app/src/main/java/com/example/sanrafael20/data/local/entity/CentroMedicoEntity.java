package com.example.sanrafael20.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/** Tabla CentroMedico: datos generales de la clínica (nombre y valor de la consulta). */
@Entity(tableName = "CentroMedico")
public class CentroMedicoEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_centro")
    private long idCentro;

    @ColumnInfo(name = "nombre")
    private String nombre;

    @ColumnInfo(name = "valor_consulta", defaultValue = "0")
    private double valorConsulta;

    @ColumnInfo(name = "logo_path")
    private String logoPath;

    public long getIdCentro() { return idCentro; }
    public void setIdCentro(long idCentro) { this.idCentro = idCentro; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public double getValorConsulta() { return valorConsulta; }
    public void setValorConsulta(double valorConsulta) { this.valorConsulta = valorConsulta; }
    public String getLogoPath() { return logoPath; }
    public void setLogoPath(String logoPath) { this.logoPath = logoPath; }
}
