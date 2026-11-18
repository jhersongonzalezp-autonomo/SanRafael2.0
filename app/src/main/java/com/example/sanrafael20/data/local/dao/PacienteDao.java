package com.example.sanrafael20.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.sanrafael20.data.local.entity.PacienteEntity;

import java.util.List;

@Dao
public interface PacienteDao {

    @Insert
    long insertar(PacienteEntity paciente); // devuelve el id generado

    @Query("SELECT * FROM Paciente ORDER BY apellido, nombre")
    LiveData<List<PacienteEntity>> observarTodos();

    @Query("SELECT * FROM Paciente WHERE id_paciente = :id")
    LiveData<PacienteEntity> observarPorId(long id);

    @Query("SELECT * FROM Paciente WHERE identidad = :identidad LIMIT 1")
    PacienteEntity buscarPorIdentidad(String identidad);

    @Query("SELECT COUNT(*) FROM Paciente")
    LiveData<Integer> observarCantidad();
}
