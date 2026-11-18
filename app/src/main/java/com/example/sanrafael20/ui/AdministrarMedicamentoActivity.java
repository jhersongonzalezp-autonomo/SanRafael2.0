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
import com.example.sanrafael20.data.local.entity.SignosVitalesEntity;
import com.example.sanrafael20.ui.viewmodel.SignosVitalesViewModel;
import com.example.sanrafael20.util.SesionManager;
import com.google.android.material.textfield.TextInputEditText;

public class AdministrarMedicamentoActivity extends AppCompatActivity {

    public static final String EXTRA_ID_PACIENTE = "id_paciente";

    private long idPaciente;
    private long idEnfermero;

    private TextView tvIniciales, tvPaciente, tvInfo, tvBannerInfo;
    private TextInputEditText etMedicamento, etDosis;
    private AutoCompleteTextView autoCompleteVia;
    private SignosVitalesViewModel viewModel;

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

        // Resumen del paciente cargado dinámicamente desde SQLite
        viewModel.getPaciente(idPaciente).observe(this, p -> {
            if (p != null) {
                if (tvIniciales != null) tvIniciales.setText(p.getIniciales());
                if (tvPaciente != null) tvPaciente.setText(p.getNombreCompleto());
                if (tvInfo != null) tvInfo.setText("Doc. " + p.getIdentidad() + " · " + p.getEdad() + " años");
                if (tvBannerInfo != null) tvBannerInfo.setText("Se agregará como tratamiento en el historial de " + p.getNombreCompleto() + ".");
            }
        });

        viewModel.getGuardado().observe(this, resultado -> {
            if (resultado.esExito()) {
                Toast.makeText(this, "Medicamento registrado con éxito", Toast.LENGTH_SHORT).show();
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

    private void registrar() {
        String med = etMedicamento != null && etMedicamento.getText() != null ? etMedicamento.getText().toString().trim() : "";
        String dosis = etDosis != null && etDosis.getText() != null ? etDosis.getText().toString().trim() : "";
        String via = autoCompleteVia != null && autoCompleteVia.getText() != null ? autoCompleteVia.getText().toString().trim() : "Oral";

        if (TextUtils.isEmpty(med)) {
            if (etMedicamento != null) etMedicamento.setError("Ingresa el medicamento");
            return;
        }

        SignosVitalesEntity registro = new SignosVitalesEntity();
        registro.setObservaciones("Administración de medicamento: " + med + " (Dosis: " + (dosis.isEmpty() ? "Segunda indicación" : dosis + " mg") + ", Vía: " + via + ")");

        View btnReg = findViewById(R.id.btnRegistrarAdmin);
        if (btnReg != null) btnReg.setEnabled(false);

        viewModel.guardar(idPaciente, idEnfermero, registro);
    }
}
