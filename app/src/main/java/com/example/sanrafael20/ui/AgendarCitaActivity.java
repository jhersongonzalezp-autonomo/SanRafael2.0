package com.example.sanrafael20.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.sanrafael20.R;
import com.example.sanrafael20.data.local.entity.EmpleadoEntity;
import com.example.sanrafael20.data.local.entity.PacienteEntity;
import com.example.sanrafael20.ui.viewmodel.AdminViewModel;
import com.example.sanrafael20.util.Fechas;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AgendarCitaActivity extends AppCompatActivity {

    private static final Locale ES = Locale.forLanguageTag("es-CO");
    private static final int[] CARD_IDS = {R.id.lunes, R.id.martes, R.id.miercoles, R.id.jueves, R.id.viernes, R.id.sabado};
    private static final int[] LABEL_IDS = {R.id.labelLunes, R.id.labelMartes, R.id.labelMiercoles, R.id.labelJueves, R.id.labelViernes, R.id.labelSabado};
    private static final int[] NUM_IDS = {R.id.numLunes, R.id.numMartes, R.id.numMiercoles, R.id.numJueves, R.id.numViernes, R.id.numSabado};

    private final Map<Integer, LocalDate> fechaPorCard = new HashMap<>();

    private MaterialAutoCompleteTextView acPaciente, acMedico;
    private TextInputEditText etMotivo;
    private TextView tvResumenCita, tvResumenDetalle;
    private Button btnConfirmar;
    private AdminViewModel viewModel;

    private final List<PacienteEntity> listaPacientes = new ArrayList<>();
    private final List<EmpleadoEntity> listaMedicos = new ArrayList<>();

    private PacienteEntity pacienteSeleccionado;
    private EmpleadoEntity medicoSeleccionado;
    private String fechaSeleccionada = Fechas.FECHA.format(LocalDate.now());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agendar_cita);

        View btnVolver = findViewById(R.id.volver);
        if (btnVolver != null) {
            btnVolver.setOnClickListener(v -> finish());
        }

        acPaciente = findViewById(R.id.paciente);
        acMedico = findViewById(R.id.medico);
        etMotivo = findViewById(R.id.motivo);
        tvResumenCita = findViewById(R.id.resumenCita);
        tvResumenDetalle = findViewById(R.id.resumenDetalle);
        btnConfirmar = findViewById(R.id.confirmar);

        viewModel = new ViewModelProvider(this).get(AdminViewModel.class);

        configurarDiasSemana();

        // 1. Cargar lista de pacientes desde SQLite
        viewModel.getPacientes().observe(this, lista -> {
            listaPacientes.clear();
            if (lista != null) {
                listaPacientes.addAll(lista);
            }
            List<String> nombres = new ArrayList<>();
            for (PacienteEntity p : listaPacientes) {
                nombres.add(p.getNombreCompleto() + " — " + p.getIdentidad());
            }
            if (acPaciente != null) {
                acPaciente.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nombres));
            }
        });

        if (acPaciente != null) {
            acPaciente.setOnItemClickListener((parent, v, pos, id) -> {
                if (pos < listaPacientes.size()) {
                    pacienteSeleccionado = listaPacientes.get(pos);
                    actualizarResumen();
                }
            });
        }

        // 2. Cargar lista de médicos desde SQLite
        viewModel.getMedicos().observe(this, lista -> {
            listaMedicos.clear();
            if (lista != null) {
                listaMedicos.addAll(lista);
            }
            List<String> nombres = new ArrayList<>();
            for (EmpleadoEntity e : listaMedicos) {
                nombres.add(e.getNombreCompleto());
            }
            if (acMedico != null) {
                acMedico.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nombres));
            }
        });

        if (acMedico != null) {
            acMedico.setOnItemClickListener((parent, v, pos, id) -> {
                if (pos < listaMedicos.size()) {
                    medicoSeleccionado = listaMedicos.get(pos);
                    actualizarResumen();
                }
            });
        }

        if (etMotivo != null) {
            etMotivo.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    actualizarResumen();
                }
                @Override public void afterTextChanged(Editable s) {}
            });
        }

        // 3. Resultado al agendar
        viewModel.getCitaGuardada().observe(this, res -> {
            if (res.esExito()) {
                Toast.makeText(this, "✓ Cita agendada con éxito", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, res.getError(), Toast.LENGTH_LONG).show();
                if (btnConfirmar != null) btnConfirmar.setEnabled(true);
            }
        });

        if (btnConfirmar != null) {
            btnConfirmar.setOnClickListener(v -> agendar());
        }

        // 4. Navegación Inferior
        View navInicio = findViewById(R.id.opcionInicio);
        if (navInicio != null) {
            navInicio.setOnClickListener(v -> {
                startActivity(new Intent(this, AdminInicioActivity.class));
                finish();
            });
        }

        View navPacientes = findViewById(R.id.opcionPacientes);
        if (navPacientes != null) {
            navPacientes.setOnClickListener(v -> {
                startActivity(new Intent(this, PacientesActivity.class));
                finish();
            });
        }

        View navPersonal = findViewById(R.id.opcionPersonal);
        if (navPersonal != null) {
            navPersonal.setOnClickListener(v -> {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            });
        }
    }

    /** Configura dinámicamente el mes, año y días de la semana actual. */
    private void configurarDiasSemana() {
        LocalDate hoy = LocalDate.now();
        TextView tvMesAno = findViewById(R.id.mesAno);
        if (tvMesAno != null) {
            String mesAnoStr = hoy.format(DateTimeFormatter.ofPattern("MMMM yyyy", ES));
            tvMesAno.setText(mesAnoStr.substring(0, 1).toUpperCase() + mesAnoStr.substring(1));
        }

        LocalDate lunes = hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        DateTimeFormatter formatoDiaNombre = DateTimeFormatter.ofPattern("EEE", ES);

        for (int i = 0; i < CARD_IDS.length; i++) {
            LocalDate dia = lunes.plusDays(i);
            int cardId = CARD_IDS[i];
            fechaPorCard.put(cardId, dia);

            MaterialCardView card = findViewById(cardId);
            TextView lbl = findViewById(LABEL_IDS[i]);
            TextView num = findViewById(NUM_IDS[i]);

            if (lbl != null) {
                String dName = dia.format(formatoDiaNombre).toUpperCase();
                lbl.setText(dName.length() >= 2 ? dName.substring(0, 2) : dName);
            }
            if (num != null) {
                num.setText(String.valueOf(dia.getDayOfMonth()));
            }

            if (card != null) {
                card.setOnClickListener(v -> seleccionarDia(cardId));
            }

            if (dia.equals(hoy)) {
                seleccionarDia(cardId);
            }
        }
        if (fechaSeleccionada == null && !fechaPorCard.isEmpty()) {
            seleccionarDia(CARD_IDS[0]);
        }
    }

    private void seleccionarDia(int cardIdSeleccionado) {
        LocalDate dia = fechaPorCard.get(cardIdSeleccionado);
        if (dia != null) {
            fechaSeleccionada = dia.format(Fechas.FECHA);
            actualizarResumen();
        }

        for (int i = 0; i < CARD_IDS.length; i++) {
            int cId = CARD_IDS[i];
            MaterialCardView card = findViewById(cId);
            TextView lbl = findViewById(LABEL_IDS[i]);
            TextView num = findViewById(NUM_IDS[i]);

            boolean esSeleccionado = (cId == cardIdSeleccionado);
            if (card != null) {
                card.setCardBackgroundColor(getResources().getColor(esSeleccionado ? R.color.hospital_green : R.color.white, getTheme()));
                card.setStrokeColor(getResources().getColor(esSeleccionado ? R.color.hospital_green : R.color.input_stroke, getTheme()));
            }
            if (lbl != null) {
                lbl.setTextColor(getResources().getColor(esSeleccionado ? R.color.white : R.color.black, getTheme()));
            }
            if (num != null) {
                num.setTextColor(getResources().getColor(esSeleccionado ? R.color.white : R.color.black, getTheme()));
            }
        }
    }

    private void actualizarResumen() {
        String pNom = pacienteSeleccionado != null ? pacienteSeleccionado.getNombreCompleto() : "Paciente";
        String mNom = medicoSeleccionado != null ? medicoSeleccionado.getNombreCompleto() : "Médico";
        String mot = etMotivo != null && etMotivo.getText() != null ? etMotivo.getText().toString().trim() : "";

        if (tvResumenCita != null) {
            tvResumenCita.setText(pNom + " ➔ " + mNom);
        }
        if (tvResumenDetalle != null) {
            tvResumenDetalle.setText(fechaSeleccionada + (mot.isEmpty() ? "" : " · " + mot));
        }
    }

    private void agendar() {
        if (pacienteSeleccionado == null) {
            Toast.makeText(this, "Selecciona un paciente", Toast.LENGTH_SHORT).show();
            return;
        }
        if (medicoSeleccionado == null) {
            Toast.makeText(this, "Selecciona un médico", Toast.LENGTH_SHORT).show();
            return;
        }

        String motivo = etMotivo != null && etMotivo.getText() != null ? etMotivo.getText().toString().trim() : "";
        if (btnConfirmar != null) btnConfirmar.setEnabled(false);

        viewModel.agendarCita(pacienteSeleccionado.getIdPaciente(), medicoSeleccionado.getIdEmpleado(), fechaSeleccionada, motivo);
    }
}
