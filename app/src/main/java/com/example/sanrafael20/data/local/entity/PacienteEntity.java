package com.example.sanrafael20.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

/** Tabla Paciente. Cada atributo privado corresponde a una columna de la tabla. */
@Entity(tableName = "Paciente",
        foreignKeys = @ForeignKey(entity = CentroMedicoEntity.class,
                parentColumns = "id_centro", childColumns = "id_centro",
                onDelete = ForeignKey.SET_NULL))
public class PacienteEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_paciente")
    private long idPaciente;

    @ColumnInfo(name = "id_centro")
    private Long idCentro; // Long (con mayúscula) porque la columna acepta null

    @ColumnInfo(name = "nombre")
    private String nombre;

    @ColumnInfo(name = "apellido")
    private String apellido;

    @ColumnInfo(name = "tipo_identidad")
    private String tipoIdentidad;

    @ColumnInfo(name = "identidad")
    private String identidad;

    @ColumnInfo(name = "edad")
    private int edad;

    @ColumnInfo(name = "nacionalidad")
    private String nacionalidad;

    @ColumnInfo(name = "estatura")
    private double estatura; // en metros, debe ser mayor que 0

    public String getNombreCompleto() { return nombre + " " + apellido; }

    public String getIniciales() {
        String n = nombre == null || nombre.isEmpty() ? "" : nombre.substring(0, 1);
        String a = apellido == null || apellido.isEmpty() ? "" : apellido.substring(0, 1);
        return (n + a).toUpperCase();
    }

    public long getIdPaciente() { return idPaciente; }
    public void setIdPaciente(long idPaciente) { this.idPaciente = idPaciente; }
    public Long getIdCentro() { return idCentro; }
    public void setIdCentro(Long idCentro) { this.idCentro = idCentro; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getTipoIdentidad() { return tipoIdentidad; }
    public void setTipoIdentidad(String tipoIdentidad) { this.tipoIdentidad = tipoIdentidad; }
    public String getIdentidad() { return identidad; }
    public void setIdentidad(String identidad) { this.identidad = identidad; }
    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }
    public String getNacionalidad() { return nacionalidad; }
    public void setNacionalidad(String nacionalidad) { this.nacionalidad = nacionalidad; }
    public double getEstatura() { return estatura; }
    public void setEstatura(double estatura) { this.estatura = estatura; }
}
