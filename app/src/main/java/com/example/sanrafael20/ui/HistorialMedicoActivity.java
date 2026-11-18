package com.example.sanrafael20.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.sanrafael20.R;
import com.example.sanrafael20.data.local.dto.ConsultaConPaciente;
import com.example.sanrafael20.data.local.entity.PacienteEntity;
import com.example.sanrafael20.data.local.entity.SignosVitalesEntity;
import com.example.sanrafael20.ui.viewmodel.ConsultaMedicoViewModel;
import com.example.sanrafael20.util.SesionManager;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

public class HistorialMedicoActivity extends AppCompatActivity {

    public static final String EXTRA_ID_PACIENTE = "id_paciente";

    private long idPaciente;
    private long idMedico;
    private TextView tvSubtitulo, tvPaciente, tvInfo, tvIniciales;
    private TextView tabTodo, tabDiagnosticos, tabTratamientos;
    private LinearLayout contenedorLista;
    private MaterialAutoCompleteTextView acPacienteSelector;
    private ConsultaMedicoViewModel viewModel;

    private final List<PacienteEntity> listaPacientes = new ArrayList<>();
    private final List<Object> registrosTotales = new ArrayList<>();
    private String filtroActual = "todo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_medico);

        idPaciente = getIntent().getLongExtra(EXTRA_ID_PACIENTE, 1);
        idMedico = new SesionManager(this).getIdEmpleado();

        View btnVolver = findViewById(R.id.volver);
        if (btnVolver != null) {
            btnVolver.setOnClickListener(v -> finish());
        }

        tvSubtitulo = findViewById(R.id.subtitulo);
        tvPaciente = findViewById(R.id.paciente);
        tvInfo = findViewById(R.id.info);
        tvIniciales = findViewById(R.id.tvInicialesPaciente);
        tabTodo = findViewById(R.id.todo);
        tabDiagnosticos = findViewById(R.id.diagnosticos);
        tabTratamientos = findViewById(R.id.tratamientos);
        contenedorLista = findViewById(R.id.lista);
        acPacienteSelector = findViewById(R.id.acPacienteSelector);

        viewModel = new ViewModelProvider(this).get(ConsultaMedicoViewModel.class);

        // 1. Cargar únicamente los pacientes asignados a este médico en concreto
        viewModel.getPacientesPorMedico(idMedico).observe(this, pacientes -> {
            listaPacientes.clear();
            if (pacientes != null) {
                listaPacientes.addAll(pacientes);
            }
            List<String> nombres = new ArrayList<>();
            for (PacienteEntity p : listaPacientes) {
                nombres.add(p.getNombreCompleto() + " — DNI: " + p.getIdentidad());
            }
            if (acPacienteSelector != null) {
                acPacienteSelector.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nombres));
            }
        });

        if (acPacienteSelector != null) {
            acPacienteSelector.setOnItemClickListener((parent, v, pos, id) -> {
                if (pos < listaPacientes.size()) {
                    idPaciente = listaPacientes.get(pos).getIdPaciente();
                    cargarDatosPaciente(idPaciente);
                }
            });
        }

        cargarDatosPaciente(idPaciente);

        // 2. Pestañas de filtro
        if (tabTodo != null) {
            tabTodo.setOnClickListener(v -> {
                filtroActual = "todo";
                actualizarPestanas();
                aplicarFiltroYRenderizar();
            });
        }
        if (tabDiagnosticos != null) {
            tabDiagnosticos.setOnClickListener(v -> {
                filtroActual = "diagnosticos";
                actualizarPestanas();
                aplicarFiltroYRenderizar();
            });
        }
        if (tabTratamientos != null) {
            tabTratamientos.setOnClickListener(v -> {
                filtroActual = "tratamientos";
                actualizarPestanas();
                aplicarFiltroYRenderizar();
            });
        }

        // 3. Navegación Inferior
        View navAgenda = findViewById(R.id.agenda);
        if (navAgenda != null) {
            navAgenda.setOnClickListener(v -> {
                startActivity(new Intent(this, MedicoAgendaActivity.class));
                finish();
            });
        }

        View navConsulta = findViewById(R.id.consulta);
        if (navConsulta != null) {
            navConsulta.setOnClickListener(v -> {
                Intent intent = new Intent(this, RealizarConsultaActivity.class);
                intent.putExtra(RealizarConsultaActivity.EXTRA_ID_PACIENTE, idPaciente);
                startActivity(intent);
            });
        }
    }

    private void cargarDatosPaciente(long idPac) {
        viewModel.getPaciente(idPac).observe(this, p -> {
            if (p != null) {
                if (tvPaciente != null) tvPaciente.setText(p.getNombreCompleto());
                if (tvIniciales != null) tvIniciales.setText(p.getIniciales());
                viewModel.getHistoriaClinica(idPac).observe(this, h -> {
                    String numHist = (h != null) ? h.getNumeroHistoria() : "HC-2024-001";
                    if (tvSubtitulo != null) tvSubtitulo.setText(numHist);
                    if (tvInfo != null) {
                        tvInfo.setText(numHist + " · DNI " + p.getIdentidad() + " · " + p.getEdad() + " años");
                    }
                });
            }
        });

        // Observar historial de consultas y signos/diagnósticos
        viewModel.getHistorialPaciente(idPac).observe(this, consultas -> {
            registrosTotales.clear();
            if (consultas != null) {
                registrosTotales.addAll(consultas);
            }
            viewModel.getRegistrosPaciente(idPac).observe(this, signos -> {
                if (signos != null) {
                    registrosTotales.addAll(signos);
                }
                aplicarFiltroYRenderizar();
            });
        });
    }

    private void actualizarPestanas() {
        if (tabTodo != null) {
            tabTodo.setBackgroundColor(getResources().getColor("todo".equals(filtroActual) ? R.color.hospital_light_green : R.color.white, getTheme()));
            tabTodo.setTextColor(getResources().getColor("todo".equals(filtroActual) ? R.color.hospital_green : R.color.black, getTheme()));
        }
        if (tabDiagnosticos != null) {
            tabDiagnosticos.setBackgroundColor(getResources().getColor("diagnosticos".equals(filtroActual) ? R.color.hospital_light_green : R.color.white, getTheme()));
            tabDiagnosticos.setTextColor(getResources().getColor("diagnosticos".equals(filtroActual) ? R.color.hospital_green : R.color.black, getTheme()));
        }
        if (tabTratamientos != null) {
            tabTratamientos.setBackgroundColor(getResources().getColor("tratamientos".equals(filtroActual) ? R.color.hospital_light_green : R.color.white, getTheme()));
            tabTratamientos.setTextColor(getResources().getColor("tratamientos".equals(filtroActual) ? R.color.hospital_green : R.color.black, getTheme()));
        }
    }

    private void aplicarFiltroYRenderizar() {
        if (contenedorLista == null) return;
        contenedorLista.removeAllViews();

        List<Object> filtrados = new ArrayList<>();
        for (Object obj : registrosTotales) {
            if (obj instanceof ConsultaConPaciente) {
                ConsultaConPaciente c = (ConsultaConPaciente) obj;
                if ("todo".equals(filtroActual)) {
                    filtrados.add(c);
                }
            } else if (obj instanceof SignosVitalesEntity) {
                SignosVitalesEntity s = (SignosVitalesEntity) obj;
                String obs = s.getObservaciones() != null ? s.getObservaciones().toLowerCase() : "";
                if ("todo".equals(filtroActual)) {
                    filtrados.add(s);
                } else if ("diagnosticos".equals(filtroActual) && obs.contains("diagnóstico")) {
                    filtrados.add(s);
                } else if ("tratamientos".equals(filtroActual) && (obs.contains("tratamiento") || obs.contains("medicamento") || obs.contains("indicaciones"))) {
                    filtrados.add(s);
                }
            }
        }

        if (filtrados.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No hay registros médicos para este filtro.");
            empty.setPadding(24, 24, 24, 24);
            empty.setTextColor(getResources().getColor(R.color.text_secondary, getTheme()));
            contenedorLista.addView(empty);
            return;
        }

        for (Object obj : filtrados) {
            MaterialCardView card = new MaterialCardView(this);
            card.setRadius(20f);
            card.setStrokeColor(getResources().getColor(R.color.input_stroke, getTheme()));
            card.setCardBackgroundColor(getResources().getColor(R.color.white, getTheme()));

            LinearLayout inner = new LinearLayout(this);
            inner.setOrientation(LinearLayout.VERTICAL);
            inner.setPadding(20, 20, 20, 20);

            TextView tvTitulo = new TextView(this);
            TextView tvContenido = new TextView(this);
            tvTitulo.setTextSize(15f);
            tvTitulo.setTypeface(null, android.graphics.Typeface.BOLD);
            tvTitulo.setTextColor(getResources().getColor(R.color.black, getTheme()));
            tvContenido.setTextSize(13f);
            tvContenido.setPadding(0, 8, 0, 0);
            tvContenido.setTextColor(getResources().getColor(R.color.text_secondary, getTheme()));

            if (obj instanceof ConsultaConPaciente) {
                ConsultaConPaciente c = (ConsultaConPaciente) obj;
                tvTitulo.setText("📅 Cita #" + c.idConsulta + " · " + c.fecha);
                tvContenido.setText("Motivo: " + c.motivo + "\nEstado: " + c.estado);
            } else if (obj instanceof SignosVitalesEntity) {
                SignosVitalesEntity s = (SignosVitalesEntity) obj;
                tvTitulo.setText("🩺 Registro Clínico · " + s.getFechaHoraToma());
                tvContenido.setText(s.getObservaciones() != null ? s.getObservaciones() : "Sin observaciones");
            }

            inner.addView(tvTitulo);
            inner.addView(tvContenido);
            card.addView(inner);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 0, 16);
            card.setLayoutParams(lp);

            contenedorLista.addView(card);
        }
    }
}
