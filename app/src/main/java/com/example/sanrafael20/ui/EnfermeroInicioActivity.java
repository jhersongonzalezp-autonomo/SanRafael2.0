package com.example.sanrafael20.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
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
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

public class EnfermeroInicioActivity extends AppCompatActivity {

    private long idPacienteSeleccionado = 1;
    private final List<PacienteEntity> listaPacientes = new ArrayList<>();

    private MaterialAutoCompleteTextView acPacienteSelectorTurno;
    private TextView tvInicialesDestacado, tvNombreDestacado, tvDetalleDestacado, tvConsultaDestacado, chipEstadoDestacado;

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

        acPacienteSelectorTurno = findViewById(R.id.acPacienteSelectorTurno);
        tvInicialesDestacado = findViewById(R.id.tvInicialesDestacado);
        tvNombreDestacado = findViewById(R.id.tvNombreDestacado);
        tvDetalleDestacado = findViewById(R.id.tvDetalleDestacado);
        tvConsultaDestacado = findViewById(R.id.tvConsultaDestacado);
        chipEstadoDestacado = findViewById(R.id.chipEstadoDestacado);

        RecyclerView rv = findViewById(R.id.rvPacientesTurno);
        rv.setLayoutManager(new LinearLayoutManager(this));
        PacienteTurnoAdapter adapter = new PacienteTurnoAdapter(new PacienteTurnoAdapter.OnPacienteTurnoClickListener() {
            @Override
            public void onSignos(PacienteEntity paciente) {
                if (paciente != null) {
                    idPacienteSeleccionado = paciente.getIdPaciente();
                    mostrarPacienteDestacado(paciente);
                }
                abrirSignos(idPacienteSeleccionado);
            }

            @Override
            public void onMedicar(PacienteEntity paciente) {
                if (paciente != null) {
                    idPacienteSeleccionado = paciente.getIdPaciente();
                    mostrarPacienteDestacado(paciente);
                }
                abrirMedicar(idPacienteSeleccionado);
            }
        });
        rv.setAdapter(adapter);

        // Listener del desplegable de pacientes
        if (acPacienteSelectorTurno != null) {
            acPacienteSelectorTurno.setOnItemClickListener((parent, view, position, id) -> {
                if (position >= 0 && position < listaPacientes.size()) {
                    PacienteEntity p = listaPacientes.get(position);
                    idPacienteSeleccionado = p.getIdPaciente();
                    mostrarPacienteDestacado(p);
                }
            });
        }

        // Botones de acción tarjeta destacada
        View btnIrSignos = findViewById(R.id.btnIrSignos);
        if (btnIrSignos != null) {
            btnIrSignos.setOnClickListener(v -> abrirSignos(idPacienteSeleccionado));
        }

        View btnIrMedicar = findViewById(R.id.btnIrMedicar);
        if (btnIrMedicar != null) {
            btnIrMedicar.setOnClickListener(v -> abrirMedicar(idPacienteSeleccionado));
        }

        // Navegación Inferior
        View navSignos = findViewById(R.id.navSignos);
        if (navSignos != null) {
            navSignos.setOnClickListener(v -> abrirSignos(idPacienteSeleccionado));
        }

        View navMedicacion = findViewById(R.id.navMedicacion);
        if (navMedicacion != null) {
            navMedicacion.setOnClickListener(v -> abrirMedicar(idPacienteSeleccionado));
        }

        PacienteViewModel pacientes = new ViewModelProvider(this).get(PacienteViewModel.class);
        pacientes.getPacientes().observe(this, lista -> {
            listaPacientes.clear();
            if (lista != null) {
                listaPacientes.addAll(lista);
            }
            adapter.setPacientes(listaPacientes);
            if (tvPacientesAsignados != null) {
                tvPacientesAsignados.setText(String.valueOf(listaPacientes.size()));
            }

            // Cargar nombres en el desplegable
            List<String> nombres = new ArrayList<>();
            for (PacienteEntity p : listaPacientes) {
                nombres.add(p.getNombreCompleto() + " — DNI: " + p.getIdentidad());
            }
            if (acPacienteSelectorTurno != null) {
                acPacienteSelectorTurno.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nombres));
            }

            if (!listaPacientes.isEmpty()) {
                // Seleccionar primer paciente si el id actual no está en la lista
                boolean encontrado = false;
                for (PacienteEntity p : listaPacientes) {
                    if (p.getIdPaciente() == idPacienteSeleccionado) {
                        mostrarPacienteDestacado(p);
                        encontrado = true;
                        break;
                    }
                }
                if (!encontrado) {
                    idPacienteSeleccionado = listaPacientes.get(0).getIdPaciente();
                    mostrarPacienteDestacado(listaPacientes.get(0));
                }
            }
        });

        // Cuenta en vivo: al guardar signos/medicamento y volver, el número sube automáticamente.
        SignosVitalesViewModel signos = new ViewModelProvider(this).get(SignosVitalesViewModel.class);
        signos.getCantidadRegistros(sesion.getIdEmpleado())
                .observe(this, total -> {
                    if (tvRegistrosHechos != null && total != null) {
                        tvRegistrosHechos.setText(String.valueOf(total));
                    }
                });
    }

    private void mostrarPacienteDestacado(PacienteEntity p) {
        if (p == null) return;
        if (tvInicialesDestacado != null) tvInicialesDestacado.setText(p.getIniciales());
        if (tvNombreDestacado != null) tvNombreDestacado.setText(p.getNombreCompleto());
        if (tvDetalleDestacado != null) tvDetalleDestacado.setText("DNI: " + p.getIdentidad() + " · " + p.getEdad() + " años");
        if (tvConsultaDestacado != null) tvConsultaDestacado.setText("Paciente en turno · Estatura: " + p.getEstatura() + " m");
        if (chipEstadoDestacado != null) chipEstadoDestacado.setText("Asignado");
        if (acPacienteSelectorTurno != null) {
            acPacienteSelectorTurno.setText(p.getNombreCompleto() + " — DNI: " + p.getIdentidad(), false);
        }
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
