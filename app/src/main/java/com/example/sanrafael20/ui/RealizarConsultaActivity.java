package com.example.sanrafael20.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.sanrafael20.R;
import com.example.sanrafael20.data.local.dto.ConsultaConPaciente;
import com.example.sanrafael20.ui.viewmodel.AgendaViewModel;
import com.example.sanrafael20.ui.viewmodel.ConsultaMedicoViewModel;
import com.example.sanrafael20.util.SesionManager;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class RealizarConsultaActivity extends AppCompatActivity {

    public static final String EXTRA_ID_CONSULTA = "id_consulta";
    public static final String EXTRA_ID_PACIENTE = "id_paciente";

    private long idConsulta;
    private long idPaciente;
    private long idMedico;

    private TextView tvSubtitulo, tvPaciente, tvInfo, tvIniciales;
    private TextView tvFechaSignos, tvSignosVitalesDetalle;
    private TextInputEditText etDiagnostico, etTratamiento, etIndicaciones;
    private MaterialAutoCompleteTextView acCitaSelector;
    private ConsultaMedicoViewModel viewModel;
    private AgendaViewModel agendaViewModel;

    private final List<ConsultaConPaciente> listaConsultasMedico = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realizar_consulta);

        idConsulta = getIntent().getLongExtra(EXTRA_ID_CONSULTA, -1);
        idPaciente = getIntent().getLongExtra(EXTRA_ID_PACIENTE, -1);
        idMedico = new SesionManager(this).getIdEmpleado();

        View btnVolver = findViewById(R.id.volver);
        if (btnVolver != null) {
            btnVolver.setOnClickListener(v -> finish());
        }

        tvSubtitulo = findViewById(R.id.subtitulo);
        tvPaciente = findViewById(R.id.paciente);
        tvInfo = findViewById(R.id.info);
        tvIniciales = findViewById(R.id.tvInicialesConsulta);
        tvFechaSignos = findViewById(R.id.tvFechaSignos);
        tvSignosVitalesDetalle = findViewById(R.id.tvSignosVitalesDetalle);
        etDiagnostico = findViewById(R.id.diagnostico);
        etTratamiento = findViewById(R.id.tratamiento);
        etIndicaciones = findViewById(R.id.indicaciones);
        acCitaSelector = findViewById(R.id.acCitaSelector);

        viewModel = new ViewModelProvider(this).get(ConsultaMedicoViewModel.class);
        agendaViewModel = new ViewModelProvider(this).get(AgendaViewModel.class);

        // 1. Cargar únicamente las citas/pacientes asignados al médico logueado
        agendaViewModel.getAgenda(idMedico).observe(this, lista -> {
            listaConsultasMedico.clear();
            if (lista != null) {
                listaConsultasMedico.addAll(lista);
            }
            List<String> nombresCitas = new ArrayList<>();
            for (ConsultaConPaciente c : listaConsultasMedico) {
                nombresCitas.add(c.getNombreCompleto() + " — " + c.motivo + " (" + c.fecha + ")");
            }
            if (acCitaSelector != null) {
                acCitaSelector.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nombresCitas));
            }
        });

        if (acCitaSelector != null) {
            acCitaSelector.setOnItemClickListener((parent, v, pos, id) -> {
                if (pos < listaConsultasMedico.size()) {
                    ConsultaConPaciente seleccionada = listaConsultasMedico.get(pos);
                    idConsulta = seleccionada.idConsulta;
                    idPaciente = seleccionada.idPaciente;
                    mostrarConsulta(seleccionada);
                }
            });
        }

        // 2. Cargar datos si se pasó por Intent
        if (idConsulta > 0) {
            viewModel.getConsulta(idConsulta).observe(this, consulta -> {
                if (consulta != null) {
                    idPaciente = consulta.idPaciente;
                    mostrarConsulta(consulta);
                }
            });
        } else if (idPaciente > 0) {
            cargarPacienteInfo(idPaciente);
        }

        // 3. Botón Guardar (Persiste el progreso en SQLite para que aparezca en el historial)
        View btnGuardar = findViewById(R.id.guardar);
        if (btnGuardar != null) {
            btnGuardar.setOnClickListener(v -> {
                String diag = etDiagnostico != null && etDiagnostico.getText() != null ? etDiagnostico.getText().toString() : "";
                String trat = etTratamiento != null && etTratamiento.getText() != null ? etTratamiento.getText().toString() : "";
                String ind = etIndicaciones != null && etIndicaciones.getText() != null ? etIndicaciones.getText().toString() : "";

                if (diag.trim().isEmpty() && trat.trim().isEmpty()) {
                    Toast.makeText(this, "Escribe al menos un diagnóstico o tratamiento para guardar", Toast.LENGTH_SHORT).show();
                    return;
                }

                viewModel.finalizarConsulta(idConsulta, idPaciente, idMedico, diag, trat, ind);
                Toast.makeText(this, "✓ Progreso guardado en el historial", Toast.LENGTH_SHORT).show();
            });
        }

        // 4. Botón Finalizar y Cerrar
        View btnFinalizar = findViewById(R.id.finalizar);
        if (btnFinalizar != null) {
            btnFinalizar.setOnClickListener(v -> finalizarConsulta());
        }

        // 5. Navegación Inferior
        View navAgenda = findViewById(R.id.agenda);
        if (navAgenda != null) {
            navAgenda.setOnClickListener(v -> {
                startActivity(new Intent(this, MedicoAgendaActivity.class));
                finish();
            });
        }

        View navHistorial = findViewById(R.id.historial);
        if (navHistorial != null) {
            navHistorial.setOnClickListener(v -> {
                Intent intent = new Intent(this, HistorialMedicoActivity.class);
                intent.putExtra(HistorialMedicoActivity.EXTRA_ID_PACIENTE, idPaciente);
                startActivity(intent);
            });
        }
    }

    private void mostrarConsulta(ConsultaConPaciente c) {
        if (tvSubtitulo != null) {
            tvSubtitulo.setText(c.fecha + " · " + c.motivo);
        }
        if (tvPaciente != null) {
            tvPaciente.setText(c.getNombreCompleto());
        }
        if (tvIniciales != null) {
            tvIniciales.setText(c.getIniciales());
        }
        cargarPacienteInfo(c.idPaciente);
    }

    private void cargarPacienteInfo(long idPac) {
        if (idPac <= 0) return;
        viewModel.getPaciente(idPac).observe(this, p -> {
            if (p != null) {
                if (tvPaciente != null) tvPaciente.setText(p.getNombreCompleto());
                if (tvIniciales != null) tvIniciales.setText(p.getIniciales());
                viewModel.getHistoriaClinica(idPac).observe(this, h -> {
                    String numHist = (h != null) ? h.getNumeroHistoria() : "HC-2024-001";
                    if (tvInfo != null) {
                        tvInfo.setText(numHist + " · DNI " + p.getIdentidad() + " · " + p.getEdad() + " años");
                    }
                });
            }
        });

        // Cargar los signos vitales tomados por el enfermero para este paciente
        viewModel.getRegistrosPaciente(idPac).observe(this, registros -> {
            if (registros != null && !registros.isEmpty()) {
                com.example.sanrafael20.data.local.entity.SignosVitalesEntity ultimoSignos = null;
                for (com.example.sanrafael20.data.local.entity.SignosVitalesEntity s : registros) {
                    if (s.getPresionSistolica() != null || s.getFrecuenciaCardiaca() != null || s.getTemperatura() != null) {
                        ultimoSignos = s;
                        break;
                    }
                }
                if (ultimoSignos == null) {
                    ultimoSignos = registros.get(0);
                }

                if (tvFechaSignos != null) {
                    tvFechaSignos.setText(ultimoSignos.getFechaHoraToma());
                }
                if (tvSignosVitalesDetalle != null) {
                    StringBuilder sb = new StringBuilder();
                    if (ultimoSignos.getPresionSistolica() != null && ultimoSignos.getPresionDiastolica() != null) {
                        sb.append("PA: ").append(ultimoSignos.getPresionSistolica()).append("/").append(ultimoSignos.getPresionDiastolica()).append(" mmHg | ");
                    }
                    if (ultimoSignos.getFrecuenciaCardiaca() != null) {
                        sb.append("FC: ").append(ultimoSignos.getFrecuenciaCardiaca()).append(" lpm | ");
                    }
                    if (ultimoSignos.getTemperatura() != null) {
                        sb.append("Temp: ").append(ultimoSignos.getTemperatura()).append(" °C | ");
                    }
                    if (ultimoSignos.getSaturacionOxigeno() != null) {
                        sb.append("SatO₂: ").append(ultimoSignos.getSaturacionOxigeno()).append("% | ");
                    }
                    if (ultimoSignos.getFrecuenciaRespiratoria() != null) {
                        sb.append("FR: ").append(ultimoSignos.getFrecuenciaRespiratoria()).append(" rpm | ");
                    }
                    if (ultimoSignos.getObservaciones() != null && !ultimoSignos.getObservaciones().isEmpty()) {
                        sb.append("\nNotes: ").append(ultimoSignos.getObservaciones());
                    }
                    tvSignosVitalesDetalle.setText(sb.length() > 0 ? sb.toString() : "Signos tomados por enfermería");
                }
            } else {
                if (tvFechaSignos != null) tvFechaSignos.setText("");
                if (tvSignosVitalesDetalle != null) tvSignosVitalesDetalle.setText("Pendiente: No se han registrado signos vitales aún.");
            }
        });
    }

    private void finalizarConsulta() {
        String diag = etDiagnostico != null && etDiagnostico.getText() != null ? etDiagnostico.getText().toString() : "";
        String trat = etTratamiento != null && etTratamiento.getText() != null ? etTratamiento.getText().toString() : "";
        String ind = etIndicaciones != null && etIndicaciones.getText() != null ? etIndicaciones.getText().toString() : "";

        if (diag.trim().isEmpty()) {
            Toast.makeText(this, "Escribe un diagnóstico antes de finalizar", Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.finalizarConsulta(idConsulta, idPaciente, idMedico, diag, trat, ind);
        Toast.makeText(this, "✓ Consulta finalizada y cerrada con éxito", Toast.LENGTH_SHORT).show();
        finish();
    }
}
