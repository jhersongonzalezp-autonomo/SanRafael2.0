package com.example.sanrafael20.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.sanrafael20.R;
import com.example.sanrafael20.data.local.entity.PacienteEntity;
import com.example.sanrafael20.data.local.entity.SignosVitalesEntity;
import com.example.sanrafael20.ui.viewmodel.SignosVitalesViewModel;
import com.example.sanrafael20.util.SesionManager;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class AdministrarMedicamentoActivity extends AppCompatActivity {

    public static final String EXTRA_ID_PACIENTE = "id_paciente";

    private long idPaciente;
    private long idEnfermero;

    private TextView tvIniciales, tvPaciente, tvInfo, tvBannerInfo;
    private TextInputEditText etMedicamento, etDosis;
    private AutoCompleteTextView autoCompleteVia;
    private MaterialAutoCompleteTextView acPacienteSelectorMedicar;
    private SignosVitalesViewModel viewModel;
    private final List<PacienteEntity> listaPacientes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrar_medicamento);

        idPaciente = getIntent().getLongExtra(EXTRA_ID_PACIENTE, 1);
        idEnfermero = new SesionManager(this).getIdEmpleado();

        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        acPacienteSelectorMedicar = findViewById(R.id.acPacienteSelectorMedicar);
        tvIniciales = findViewById(R.id.tvIniciales);
        tvPaciente = findViewById(R.id.paciente);
        tvInfo = findViewById(R.id.info);
        tvBannerInfo = findViewById(R.id.tvBannerInfo);
        etMedicamento = findViewById(R.id.etMedicamento);
        etDosis = findViewById(R.id.etDosis);
        autoCompleteVia = findViewById(R.id.autoCompleteVia);

        if (autoCompleteVia != null) {
            String[] vias = new String[]{"Oral", "Intravenosa", "Intramuscular", "Subcutánea"};
            autoCompleteVia.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, vias));
        }

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
            if (acPacienteSelectorMedicar != null) {
                acPacienteSelectorMedicar.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nombres));
            }
        });

        if (acPacienteSelectorMedicar != null) {
            acPacienteSelectorMedicar.setOnItemClickListener((parent, v, pos, id) -> {
                if (pos >= 0 && pos < listaPacientes.size()) {
                    idPaciente = listaPacientes.get(pos).getIdPaciente();
                    cargarPacienteInfo(idPaciente);
                }
            });
        }

        viewModel.getGuardado().observe(this, resultado -> {
            if (resultado.esExito()) {
                Toast.makeText(this, "✓ Medicamento registrado con éxito", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, resultado.getError(), Toast.LENGTH_LONG).show();
                View btnReg = findViewById(R.id.btnRegistrarAdmin);
                if (btnReg != null) btnReg.setEnabled(true);
            }
        });

        View btnRegistrar = findViewById(R.id.btnRegistrarAdmin);
        if (btnRegistrar != null) {
            btnRegistrar.setOnClickListener(v -> registrar());
        }

        // Navegación Inferior
        View navTurno = findViewById(R.id.navTurno);
        if (navTurno != null) {
            navTurno.setOnClickListener(v -> {
                startActivity(new Intent(this, EnfermeroInicioActivity.class));
                finish();
            });
        }

        View navSignos = findViewById(R.id.navSignos);
        if (navSignos != null) {
            navSignos.setOnClickListener(v -> {
                Intent intent = new Intent(this, SignosVitalesActivity.class);
                intent.putExtra(SignosVitalesActivity.EXTRA_ID_PACIENTE, idPaciente);
                startActivity(intent);
            });
        }
    }

    private void cargarPacienteInfo(long idPac) {
        viewModel.getPaciente(idPac).observe(this, p -> {
            if (p != null) {
                if (tvIniciales != null) tvIniciales.setText(p.getIniciales());
                if (tvPaciente != null) tvPaciente.setText(p.getNombreCompleto());
                if (tvInfo != null) tvInfo.setText("Doc. " + p.getIdentidad() + " · " + p.getEdad() + " años");
                if (tvBannerInfo != null) tvBannerInfo.setText("Se agregará como tratamiento en el historial de " + p.getNombreCompleto() + ".");
                if (acPacienteSelectorMedicar != null) {
                    acPacienteSelectorMedicar.setText(p.getNombreCompleto() + " — DNI: " + p.getIdentidad(), false);
                }
            }
        });

        TextView tvPrescripcion = findViewById(R.id.tvPrescripcion);
        TextView tvMedicoPrescribe = findViewById(R.id.tvMedicoPrescribe);

        viewModel.getRegistrosPaciente(idPac).observe(this, registros -> {
            String prescripcionEncontrada = null;
            if (registros != null) {
                for (SignosVitalesEntity s : registros) {
                    String obs = s.getObservaciones();
                    if (obs != null && (obs.contains("Tratamiento:") || obs.contains("Indicaciones:") || obs.contains("Diagnóstico:"))) {
                        prescripcionEncontrada = obs;
                        break;
                    }
                }
            }
            if (prescripcionEncontrada != null) {
                if (tvPrescripcion != null) tvPrescripcion.setText(prescripcionEncontrada);
                if (tvMedicoPrescribe != null) tvMedicoPrescribe.setText("Prescripción registrada por el médico tratante");
            } else {
                if (tvPrescripcion != null) tvPrescripcion.setText("Sin prescripción médica registrada aún");
                if (tvMedicoPrescribe != null) tvMedicoPrescribe.setText("Consulte las indicaciones antes de administrar");
            }
        });
    }

    private void registrar() {
        String med = etMedicamento != null && etMedicamento.getText() != null ? etMedicamento.getText().toString().trim() : "";
        String dosis = etDosis != null && etDosis.getText() != null ? etDosis.getText().toString().trim() : "";
        String via = autoCompleteVia != null && autoCompleteVia.getText() != null ? autoCompleteVia.getText().toString().trim() : "Oral";

        if (TextUtils.isEmpty(med)) {
            if (etMedicamento != null) etMedicamento.setError("Ingresa el medicamento");
            return;
        }

        SignosVitalesEntity registro = new SignosVitalesEntity();
        registro.setObservaciones("💊 Administración de medicamento: " + med + " (Dosis: " + (dosis.isEmpty() ? "Según indicación" : dosis + " mg") + ", Vía: " + via + ")");

        View btnReg = findViewById(R.id.btnRegistrarAdmin);
        if (btnReg != null) btnReg.setEnabled(false);

        viewModel.guardar(idPaciente, idEnfermero, registro);
    }
}
