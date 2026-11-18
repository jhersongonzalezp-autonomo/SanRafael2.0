package com.example.sanrafael20.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.sanrafael20.data.local.entity.EmpleadoEntity;

import java.util.List;

@Dao
public interface EmpleadoDao {

    @Insert
    long insertar(EmpleadoEntity empleado);

    @Query("SELECT * FROM Empleado WHERE email = :email LIMIT 1")
    EmpleadoEntity buscarPorEmail(String email);

    @Query("SELECT * FROM Empleado WHERE id_empleado = :id LIMIT 1")
    EmpleadoEntity buscarPorId(long id);

    @Query("SELECT * FROM Empleado WHERE rol IN ('Medico', 'Médico') ORDER BY apellido, nombre")
    LiveData<List<EmpleadoEntity>> observarMedicos();

    @Query("SELECT * FROM Empleado ORDER BY apellido, nombre")
    LiveData<List<EmpleadoEntity>> observarTodos();

    @Query("SELECT COUNT(*) FROM Empleado")
    LiveData<Integer> observarCantidad();
}
