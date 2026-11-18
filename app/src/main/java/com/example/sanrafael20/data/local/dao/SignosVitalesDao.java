package com.example.sanrafael20.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.sanrafael20.data.local.entity.SignosVitalesEntity;

import java.util.List;

@Dao
public interface SignosVitalesDao {

    @Insert
    long insertar(SignosVitalesEntity signos);

    @Query("SELECT COUNT(*) FROM SignosVitales WHERE id_empleado = :idEmpleado")
    LiveData<Integer> observarCantidadPorEmpleado(long idEmpleado);

    @Query("SELECT sv.* FROM SignosVitales sv JOIN HistoriaClinica h ON h.id_historia = sv.id_historia WHERE h.id_paciente = :idPaciente ORDER BY sv.id_signos DESC")
    LiveData<List<SignosVitalesEntity>> observarPorPaciente(long idPaciente);
}
