package com.example.sanrafael20.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.example.sanrafael20.data.local.entity.CentroMedicoEntity;

@Dao
public interface CentroMedicoDao {

    /** LiveData: Room la ejecuta en segundo plano y avisa a la pantalla cuando cambia. */
    @Query("SELECT * FROM CentroMedico ORDER BY id_centro LIMIT 1")
    LiveData<CentroMedicoEntity> observarPrincipal();

    /** Versión directa: solo se puede llamar fuera del hilo principal. */
    @Query("SELECT * FROM CentroMedico ORDER BY id_centro LIMIT 1")
    CentroMedicoEntity buscarPrincipal();
}
