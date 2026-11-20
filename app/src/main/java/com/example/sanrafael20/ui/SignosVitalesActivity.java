package com.example.sanrafael20.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.sanrafael20.R;
import com.example.sanrafael20.data.local.entity.PacienteEntity;
import com.example.sanrafael20.data.local.entity.SignosVitalesEntity;
import com.example.sanrafael20.ui.viewmodel.SignosVitalesViewModel;
import com.example.sanrafael20.util.SesionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class SignosVitalesActivity extends AppCompatActivity {

    public static final String EXTRA_ID_PACIENTE = "id_paciente";

    private TextInputLayout tilPresion, tilFc, tilTemp, tilSat, tilFr, tilPeso;
    private TextInputEditText etPresion, etFc, etTemp, etSat, etFr, etPeso, etObservaciones;
    private MaterialAutoCompleteTextView acPacienteSelector;
    private SignosVitalesViewModel viewModel;
    private long idPaciente;
    private long idEnfermero;
    private final List<PacienteEntity> listaPacientes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signos_vitales);

        idPaciente = getIntent().getLongExtra(EXTRA_ID_PACIENTE, 1);
        idEnfermero = new SesionManager(this).getIdEmpleado();

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        acPacienteSelector = findViewById(R.id.acPacienteSelectorSignos);
        tilPresion = findViewById(R.id.tilPresionArterial);
        tilFc = findViewById(R.id.tilFrecuenciaCardiaca);
        tilTemp = findViewById(R.id.tilTemperatura);
        tilSat = findViewById(R.id.tilSaturacion);
        tilFr = findViewById(R.id.tilFrecuenciaRespiratoria);
        tilPeso = findViewById(R.id.tilPeso);
        etPresion = findViewById(R.id.etPresionArterial);
        etFc = findViewById(R.id.etFrecuenciaCardiaca);
        etTemp = findViewById(R.id.etTemperatura);
        etSat = findViewById(R.id.etSaturacion);
        etFr = findViewById(R.id.etFrecuenciaRespiratoria);
        etPeso = findViewById(R.id.etPeso);
        etObservaciones = findViewById(R.id.etObservaciones);

        viewModel = new ViewModelProvider(this).get(SignosVitalesViewModel.class);

        cargarPacienteInfo(idPaciente);

        // Desplegable de selección de paciente
        viewModel.getTodosPacientes().observe(this, lista -> {
            listaPacientes.clear();
            if (lista != null) {
                listaPacientes.addAll(lista);
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
                if (pos >= 0 && pos < listaPacientes.size()) {
                    idPaciente = listaPacientes.get(pos).getIdPaciente();
                    cargarPacienteInfo(idPaciente);
                }
            });
        }

        viewModel.getGuardado().observe(this, resultado -> {
            if (resultado.esExito()) {
                Toast.makeText(this, "✓ Signos guardados correctamente", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, resultado.getError(), Toast.LENGTH_LONG).show();
                View btnGuardar = findViewById(R.id.btnGuardarSignos);
                if (btnGuardar != null) btnGuardar.setEnabled(true);
            }
        });

        View btnGuardar = findViewById(R.id.btnGuardarSignos);
        if (btnGuardar != null) {
            btnGuardar.setOnClickListener(v -> guardar());
        }

        // Navegación Inferior
        View navTurno = findViewById(R.id.navTurno);
        if (navTurno != null) {
            navTurno.setOnClickListener(v -> {
                startActivity(new Intent(this, EnfermeroInicioActivity.class));
                finish();
            });
        }

        View navMedicacion = findViewById(R.id.navMedicacion);
        if (navMedicacion != null) {
            navMedicacion.setOnClickListener(v -> {
                Intent i = new Intent(this, AdministrarMedicamentoActivity.class);
                i.putExtra(AdministrarMedicamentoActivity.EXTRA_ID_PACIENTE, idPaciente);
                startActivity(i);
            });
        }
    }

    private void cargarPacienteInfo(long idPac) {
        View resumen = findViewById(R.id.pacienteResumen);
        if (resumen != null) {
            TextView tvIniciales = resumen.findViewById(R.id.tvIniciales);
            TextView tvNombre = resumen.findViewById(R.id.tvNombre);
            TextView tvDetalle = resumen.findViewById(R.id.tvDetalle);
            viewModel.getPaciente(idPac).observe(this, p -> {
                if (p != null) {
                    if (tvIniciales != null) tvIniciales.setText(p.getIniciales());
                    if (tvNombre != null) tvNombre.setText(p.getNombreCompleto());
                    if (tvDetalle != null) tvDetalle.setText("Doc. " + p.getIdentidad() + " · " + p.getEdad() + " años");
                    if (acPacienteSelector != null) {
                        acPacienteSelector.setText(p.getNombreCompleto() + " — DNI: " + p.getIdentidad(), false);
                    }
                }
            });
        }
    }

    private void guardar() {
        // Presión en formato 120/80
        Integer sistolica = null, diastolica = null;
        String[] partes = texto(etPresion).split("/");
        try {
            if (partes.length == 2) {
                sistolica = Integer.parseInt(partes[0].trim());
                diastolica = Integer.parseInt(partes[1].trim());
            }
        } catch (NumberFormatException ignorada) {
            // se informa abajo
        }
        boolean presionOk = sistolica != null && diastolica != null
                && sistolica >= 40 && sistolica <= 300 && diastolica >= 20 && diastolica <= 200;
        tilPresion.setError(presionOk ? null : "Usa el formato 120/80");

        // Los rangos son los mismos CHECK de la tabla SignosVitales
        Integer fc = entero(tilFc, etFc, 20, 300, true);
        Double temp = decimal(tilTemp, etTemp, 25, 45, true);
        Integer sat = entero(tilSat, etSat, 0, 100, true);
        Integer fr = entero(tilFr, etFr, 4, 100, false);
        Double peso = decimal(tilPeso, etPeso, 1, 400, false);

        if (!presionOk || fc == null || temp == null || sat == null
                || tilFr.getError() != null || tilPeso.getError() != null) {
            return;
        }

        SignosVitalesEntity signos = new SignosVitalesEntity();
        signos.setPresionSistolica(sistolica);
        signos.setPresionDiastolica(diastolica);
        signos.setFrecuenciaCardiaca(fc);
        signos.setTemperatura(temp);
        signos.setSaturacionOxigeno(sat);
        signos.setFrecuenciaRespiratoria(fr);

        // Construir registro detallado para trazabilidad
        StringBuilder trace = new StringBuilder();
        trace.append("⚡ Signos Vitales — PA: ").append(sistolica).append("/").append(diastolica).append(" mmHg");
        trace.append(" | FC: ").append(fc).append(" lpm");
        trace.append(" | Temp: ").append(temp).append(" °C");
        trace.append(" | SatO₂: ").append(sat).append("%");
        if (fr != null) trace.append(" | FR: ").append(fr).append(" rpm");
        if (peso != null) trace.append(" | Peso: ").append(peso).append(" kg");

        String obsUser = texto(etObservaciones);
        if (!obsUser.isEmpty()) {
            trace.append("\nObs: ").append(obsUser);
        }
        signos.setObservaciones(trace.toString());

        View btnGuardar = findViewById(R.id.btnGuardarSignos);
        if (btnGuardar != null) btnGuardar.setEnabled(false);
        viewModel.guardar(idPaciente, idEnfermero, signos);
    }

    /** Devuelve null si está vacío o fuera de rango; marca el error solo si es obligatorio o inválido. */
    private static Integer entero(TextInputLayout til, TextInputEditText et, int min, int max, boolean obligatorio) {
        String t = texto(et);
        if (TextUtils.isEmpty(t)) {
            til.setError(obligatorio ? "Campo obligatorio" : null);
            return null;
        }
        try {
            int v = Integer.parseInt(t);
            if (v >= min && v <= max) {
                til.setError(null);
                return v;
            }
        } catch (NumberFormatException ignorada) {
            // se informa abajo
        }
        til.setError("Entre " + min + " y " + max);
        return null;
    }

    private static Double decimal(TextInputLayout til, TextInputEditText et, double min, double max, boolean obligatorio) {
        String t = texto(et).replace(',', '.');
        if (TextUtils.isEmpty(t)) {
            til.setError(obligatorio ? "Campo obligatorio" : null);
            return null;
        }
        try {
            double v = Double.parseDouble(t);
            if (v >= min && v <= max) {
                til.setError(null);
                return v;
            }
        } catch (NumberFormatException ignorada) {
            // se informa abajo
        }
        til.setError("Entre " + min + " y " + max);
        return null;
    }

    private static String texto(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }
}
