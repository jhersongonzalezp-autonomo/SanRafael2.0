package com.example.sanrafael20.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.sanrafael20.data.local.dao.CentroMedicoDao;
import com.example.sanrafael20.data.local.dao.ConsultaDao;
import com.example.sanrafael20.data.local.dao.EmpleadoDao;
import com.example.sanrafael20.data.local.dao.FacturaDao;
import com.example.sanrafael20.data.local.dao.HistoriaClinicaDao;
import com.example.sanrafael20.data.local.dao.PacienteDao;
import com.example.sanrafael20.data.local.dao.SignosVitalesDao;
import com.example.sanrafael20.data.local.entity.CentroMedicoEntity;
import com.example.sanrafael20.data.local.entity.ConsultaEntity;
import com.example.sanrafael20.data.local.entity.EmpleadoEntity;
import com.example.sanrafael20.data.local.entity.FacturaEntity;
import com.example.sanrafael20.data.local.entity.HistoriaClinicaEntity;
import com.example.sanrafael20.data.local.entity.PacienteEntity;
import com.example.sanrafael20.data.local.entity.RolEntity;
import com.example.sanrafael20.data.local.entity.SignosVitalesEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Punto de entrada a la base de datos. La versión (3) debe ser igual a la del archivo
 * agenda_citas.db que está en assets; si no, Room pide una migración y la app se cierra.
 */
@Database(entities = {
        CentroMedicoEntity.class,
        RolEntity.class,
        EmpleadoEntity.class,
        PacienteEntity.class,
        ConsultaEntity.class,
        HistoriaClinicaEntity.class,
        SignosVitalesEntity.class,
        FacturaEntity.class
}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String NOMBRE_ARCHIVO = "agenda_citas.db";

    /** Hilo de trabajo para leer y escribir sin bloquear la pantalla. */
    public static final ExecutorService EJECUTOR = Executors.newSingleThreadExecutor();

    private static volatile AppDatabase instancia;

    public abstract CentroMedicoDao centroMedicoDao();
    public abstract EmpleadoDao empleadoDao();
    public abstract PacienteDao pacienteDao();
    public abstract HistoriaClinicaDao historiaClinicaDao();
    public abstract ConsultaDao consultaDao();
    public abstract SignosVitalesDao signosVitalesDao();
    public abstract FacturaDao facturaDao();

    /** Singleton: toda la app comparte una sola conexión. */
    public static AppDatabase getInstance(Context context) {
        if (instancia == null) {
            synchronized (AppDatabase.class) {
                if (instancia == null) {
                    instancia = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    NOMBRE_ARCHIVO)
                            .createFromAsset(NOMBRE_ARCHIVO)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instancia;
    }
}
