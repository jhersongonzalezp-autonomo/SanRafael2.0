package com.example.sanrafael20.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sanrafael20.R;
import com.example.sanrafael20.data.local.entity.PacienteEntity;
import com.example.sanrafael20.ui.viewmodel.PacienteViewModel;
import com.example.sanrafael20.ui.viewmodel.SignosVitalesViewModel;
import com.example.sanrafael20.util.SesionManager;
import com.google.android.material.appbar.MaterialToolbar;

public class EnfermeroInicioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turno_enfermero);
        SesionManager sesion = new SesionManager(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setSubtitle(sesion.getNombre() + " · Enfermería");
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_cerrar_sesion) {
                LoginActivity.cerrarSesion(this);
                return true;
            }
            return false;
        });

        TextView tvPacientesAsignados = findViewById(R.id.tvPacientesAsignados);
        TextView tvRegistrosHechos = findViewById(R.id.tvRegistrosHechos);

        RecyclerView rv = findViewById(R.id.rvPacientesTurno);
        rv.setLayoutManager(new LinearLayoutManager(this));
        PacienteTurnoAdapter adapter = new PacienteTurnoAdapter(new PacienteTurnoAdapter.OnPacienteTurnoClickListener() {
            @Override
            public void onSignos(PacienteEntity paciente) {
                abrirSignos(paciente.getIdPaciente());
            }

            @Override
            public void onMedicar(PacienteEntity paciente) {
                abrirMedicar(paciente.getIdPaciente());
            }
        });
        rv.setAdapter(adapter);

        // Botones de acción tarjeta destacada
        View btnIrSignos = findViewById(R.id.btnIrSignos);
        if (btnIrSignos != null) {
            btnIrSignos.setOnClickListener(v -> abrirSignos(1));
        }

        View btnIrMedicar = findViewById(R.id.btnIrMedicar);
        if (btnIrMedicar != null) {
            btnIrMedicar.setOnClickListener(v -> abrirMedicar(1));
        }

        // Navegación Inferior
        View navSignos = findViewById(R.id.navSignos);
        if (navSignos != null) {
            navSignos.setOnClickListener(v -> abrirSignos(1));
        }

        View navMedicacion = findViewById(R.id.navMedicacion);
        if (navMedicacion != null) {
            navMedicacion.setOnClickListener(v -> abrirMedicar(1));
        }

        PacienteViewModel pacientes = new ViewModelProvider(this).get(PacienteViewModel.class);
        pacientes.getPacientes().observe(this, lista -> {
            adapter.setPacientes(lista);
            tvPacientesAsignados.setText(String.valueOf(lista.size()));
        });

        // Cuenta en vivo: al guardar signos/medicamento y volver, el número sube automáticamente.
        SignosVitalesViewModel signos = new ViewModelProvider(this).get(SignosVitalesViewModel.class);
        signos.getCantidadRegistros(sesion.getIdEmpleado())
                .observe(this, total -> tvRegistrosHechos.setText(String.valueOf(total)));
    }

    private void abrirSignos(long idPaciente) {
        Intent i = new Intent(this, SignosVitalesActivity.class);
        i.putExtra(SignosVitalesActivity.EXTRA_ID_PACIENTE, idPaciente);
        startActivity(i);
    }

    private void abrirMedicar(long idPaciente) {
        Intent i = new Intent(this, AdministrarMedicamentoActivity.class);
        i.putExtra(AdministrarMedicamentoActivity.EXTRA_ID_PACIENTE, idPaciente);
        startActivity(i);
    }
}
