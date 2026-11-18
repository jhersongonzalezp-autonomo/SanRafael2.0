package com.example.sanrafael20.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

/** Tabla Empleado: médicos, enfermeros y administrativos. El campo rol decide la pantalla de inicio. */
@Entity(tableName = "Empleado",
        foreignKeys = {
                @ForeignKey(entity = CentroMedicoEntity.class,
                        parentColumns = "id_centro", childColumns = "id_centro",
                        onDelete = ForeignKey.RESTRICT),
                @ForeignKey(entity = RolEntity.class,
                        parentColumns = "id_rol", childColumns = "id_rol",
                        onDelete = ForeignKey.RESTRICT)
        })
public class EmpleadoEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_empleado")
    private long idEmpleado;

    @ColumnInfo(name = "id_centro")
    private long idCentro;

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

    @ColumnInfo(name = "salario")
    private double salario;

    @ColumnInfo(name = "rol")
    private String rol; // Medico, Enfermero, Administrativo o Recepcion

    @ColumnInfo(name = "fecha_ingreso")
    private String fechaIngreso;

    @ColumnInfo(name = "fecha_salida")
    private String fechaSalida; // null mientras el empleado esté activo

    @ColumnInfo(name = "hash_password")
    private String hashPassword;

    @ColumnInfo(name = "email")
    private String email;

    @ColumnInfo(name = "id_rol")
    private Long idRol; // puede ser null

    public String getNombreCompleto() { return nombre + " " + apellido; }

    public long getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(long idEmpleado) { this.idEmpleado = idEmpleado; }
    public long getIdCentro() { return idCentro; }
    public void setIdCentro(long idCentro) { this.idCentro = idCentro; }
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
    public double getSalario() { return salario; }
    public void setSalario(double salario) { this.salario = salario; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public String getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(String fechaIngreso) { this.fechaIngreso = fechaIngreso; }
    public String getFechaSalida() { return fechaSalida; }
    public void setFechaSalida(String fechaSalida) { this.fechaSalida = fechaSalida; }
    public String getHashPassword() { return hashPassword; }
    public void setHashPassword(String hashPassword) { this.hashPassword = hashPassword; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Long getIdRol() { return idRol; }
    public void setIdRol(Long idRol) { this.idRol = idRol; }
}
