package com.example.sanrafael20.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/** Tabla Rol: cargos configurables. Se declara porque Empleado tiene una llave foránea hacia ella. */
@Entity(tableName = "Rol", indices = {@Index(value = "nombre", unique = true)})
public class RolEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_rol")
    private long idRol;

    @ColumnInfo(name = "nombre")
    private String nombre;

    @ColumnInfo(name = "rol_base")
    private String rolBase;

    public long getIdRol() { return idRol; }
    public void setIdRol(long idRol) { this.idRol = idRol; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getRolBase() { return rolBase; }
    public void setRolBase(String rolBase) { this.rolBase = rolBase; }
}
