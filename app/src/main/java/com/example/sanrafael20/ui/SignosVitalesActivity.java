package com.example.sanrafael20.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.sanrafael20.R;
import com.example.sanrafael20.data.local.entity.SignosVitalesEntity;
import com.example.sanrafael20.ui.viewmodel.SignosVitalesViewModel;
import com.example.sanrafael20.util.SesionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SignosVitalesActivity extends AppCompatActivity {

    public static final String EXTRA_ID_PACIENTE = "id_paciente";

    private TextInputLayout tilPresion, tilFc, tilTemp, tilSat, tilFr, tilPeso;
    private TextInputEditText etPresion, etFc, etTemp, etSat, etFr, etPeso, etObservaciones;
    private SignosVitalesViewModel viewModel;
    private long idPaciente;
    private long idEnfermero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signos_vitales);

        idPaciente = getIntent().getLongExtra(EXTRA_ID_PACIENTE, -1);
        idEnfermero = new SesionManager(this).getIdEmpleado();
        if (idPaciente < 0) {
            finish();
            return;
        }

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

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

        // Resumen del paciente (include view_paciente_resumen.xml)
        View resumen = findViewById(R.id.pacienteResumen);
        if (resumen != null) {
            TextView tvIniciales = resumen.findViewById(R.id.tvIniciales);
            TextView tvNombre = resumen.findViewById(R.id.tvNombre);
            TextView tvDetalle = resumen.findViewById(R.id.tvDetalle);
            viewModel.getPaciente(idPaciente).observe(this, p -> {
                if (p != null) {
                    if (tvIniciales != null) tvIniciales.setText(p.getIniciales());
                    if (tvNombre != null) tvNombre.setText(p.getNombreCompleto());
                    if (tvDetalle != null) tvDetalle.setText("Doc. " + p.getIdentidad() + " · " + p.getEdad() + " años");
                }
            });
        }

        viewModel.getGuardado().observe(this, resultado -> {
            if (resultado.esExito()) {
                Toast.makeText(this, "Signos guardados", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, resultado.getError(), Toast.LENGTH_LONG).show();
                findViewById(R.id.btnGuardarSignos).setEnabled(true);
            }
        });

        findViewById(R.id.btnGuardarSignos).setOnClickListener(v -> guardar());

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

        // La tabla no tiene columna de peso: se guarda dentro de las observaciones.
        String obs = texto(etObservaciones);
        if (peso != null) {
            obs = "Peso: " + peso + " kg" + (obs.isEmpty() ? "" : ". " + obs);
        }
        signos.setObservaciones(obs.isEmpty() ? null : obs);

        findViewById(R.id.btnGuardarSignos).setEnabled(false);
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
