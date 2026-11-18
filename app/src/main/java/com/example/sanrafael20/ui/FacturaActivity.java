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
import com.example.sanrafael20.data.local.dto.ConsultaFacturable;
import com.example.sanrafael20.data.local.entity.FacturaEntity;
import com.example.sanrafael20.data.local.entity.PacienteEntity;
import com.example.sanrafael20.ui.viewmodel.FacturaViewModel;
import com.example.sanrafael20.util.SesionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FacturaActivity extends AppCompatActivity {

    private final NumberFormat pesos = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO"));
    private final List<PacienteEntity> pacientes = new ArrayList<>();

    private TextView tvNumeroFactura, tvPaciente, tvHistoria, tvDni, tvFechaConsulta,
            tvMedico, tvValorConsulta, tvTotal;
    private TextView chipEstado;
    private View cardFactura, btnEmitir, btnCompartir;
    private FacturaViewModel viewModel;
    private ConsultaFacturable seleccionada;
    private FacturaEntity emitida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_factura);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        tvNumeroFactura = findViewById(R.id.tvNumeroFactura);
        tvPaciente = findViewById(R.id.tvFacturaPaciente);
        tvHistoria = findViewById(R.id.tvFacturaHistoria);
        tvDni = findViewById(R.id.tvFacturaDni);
        tvFechaConsulta = findViewById(R.id.tvFacturaFechaConsulta);
        tvMedico = findViewById(R.id.tvFacturaMedico);
        tvValorConsulta = findViewById(R.id.tvFacturaValorConsulta);
        tvTotal = findViewById(R.id.tvFacturaTotal);
        chipEstado = findViewById(R.id.chipEstadoFactura);
        cardFactura = findViewById(R.id.cardFactura);
        btnEmitir = findViewById(R.id.btnEmitirFactura);
        btnCompartir = findViewById(R.id.btnCompartir);

        cardFactura.setVisibility(View.GONE);
        btnEmitir.setEnabled(false);
        btnCompartir.setEnabled(false);

        viewModel = new ViewModelProvider(this).get(FacturaViewModel.class);

        // 1. Lista desplegable con los pacientes de la base
        MaterialAutoCompleteTextView acPaciente = findViewById(R.id.acPaciente);
        viewModel.getPacientes().observe(this, lista -> {
            pacientes.clear();
            pacientes.addAll(lista);
            List<String> nombres = new ArrayList<>();
            for (PacienteEntity p : lista) {
                nombres.add(p.getNombreCompleto() + " · " + p.getIdentidad());
            }
            if (acPaciente != null) {
                acPaciente.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nombres));
            }
        });
        if (acPaciente != null) {
            acPaciente.setOnItemClickListener((parent, v, pos, id) ->
                    viewModel.prepararFactura(pacientes.get(pos).getIdPaciente()));
        }

        // 2. Vista previa con la consulta pendiente de facturar
        viewModel.getPrevia().observe(this, resultado -> {
            emitida = null;
            btnCompartir.setEnabled(false);
            if (!resultado.esExito()) {
                seleccionada = null;
                cardFactura.setVisibility(View.GONE);
                btnEmitir.setEnabled(false);
                Toast.makeText(this, resultado.getError(), Toast.LENGTH_LONG).show();
                return;
            }
            seleccionada = resultado.getDato();
            mostrar(seleccionada);
            btnEmitir.setEnabled(true);
        });

        // 3. Resultado de emitir
        viewModel.getEmitida().observe(this, resultado -> {
            if (resultado.esExito()) {
                emitida = resultado.getDato();
                tvNumeroFactura.setText("N.º " + emitida.getNumeroFactura());
                chipEstado.setText(emitida.getEstado());
                btnCompartir.setEnabled(true);
                Toast.makeText(this, "Factura " + emitida.getNumeroFactura() + " emitida", Toast.LENGTH_SHORT).show();
            } else {
                btnEmitir.setEnabled(true);
                Toast.makeText(this, resultado.getError(), Toast.LENGTH_LONG).show();
            }
        });

        long idAdministrativo = new SesionManager(this).getIdEmpleado();
        btnEmitir.setOnClickListener(v -> {
            btnEmitir.setEnabled(false);
            viewModel.emitir(seleccionada, idAdministrativo);
        });
        btnCompartir.setOnClickListener(v -> compartir());

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
            navPacientes.setOnClickListener(v -> startActivity(new Intent(this, PacientesActivity.class)));
        }

        View navCitas = findViewById(R.id.opcionCitas);
        if (navCitas != null) {
            navCitas.setOnClickListener(v -> startActivity(new Intent(this, AgendarCitaActivity.class)));
        }

        View navPersonal = findViewById(R.id.opcionPersonal);
        if (navPersonal != null) {
            navPersonal.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        }
    }

    private void mostrar(ConsultaFacturable c) {
        cardFactura.setVisibility(View.VISIBLE);
        tvNumeroFactura.setText("N.º se asigna al emitir");
        chipEstado.setText("Borrador");
        tvPaciente.setText(c.getPaciente());
        tvHistoria.setText(c.numeroHistoria);
        tvDni.setText(c.identidad);
        tvFechaConsulta.setText(c.fechaConsulta);
        tvMedico.setText(c.getMedico());
        tvValorConsulta.setText(pesos.format(c.valorConsulta));
        tvTotal.setText(pesos.format(c.valorConsulta));
    }

    private void compartir() {
        String texto = "Factura " + emitida.getNumeroFactura()
                + "\nPaciente: " + seleccionada.getPaciente() + " (" + seleccionada.numeroHistoria + ")"
                + "\n" + emitida.getConcepto() + ": " + pesos.format(emitida.getValor());
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, texto);
        startActivity(Intent.createChooser(intent, "Compartir factura"));
    }
}
