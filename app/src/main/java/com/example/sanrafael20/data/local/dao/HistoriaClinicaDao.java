package com.example.sanrafael20.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.sanrafael20.data.local.entity.HistoriaClinicaEntity;

@Dao
public interface HistoriaClinicaDao {

    @Insert
    long insertar(HistoriaClinicaEntity historia);

    @Query("SELECT * FROM HistoriaClinica WHERE id_paciente = :idPaciente LIMIT 1")
    HistoriaClinicaEntity buscarPorPaciente(long idPaciente);

    @Query("SELECT * FROM HistoriaClinica WHERE id_paciente = :idPaciente LIMIT 1")
    LiveData<HistoriaClinicaEntity> observarPorPaciente(long idPaciente);

    @Query("SELECT COUNT(*) FROM HistoriaClinica WHERE numero_historia = :numero")
    int contarPorNumero(String numero);
}
