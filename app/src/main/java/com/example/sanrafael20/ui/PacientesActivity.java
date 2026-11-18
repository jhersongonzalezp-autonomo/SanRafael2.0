package com.example.sanrafael20.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.sanrafael20.R;
import com.example.sanrafael20.data.local.entity.PacienteEntity;
import com.example.sanrafael20.ui.viewmodel.AdminViewModel;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class PacientesActivity extends AppCompatActivity {

    private TextView tvSubtitulo, tvSinPacientes;
    private TextInputEditText etBuscar;
    private LinearLayout contenedorLista;
    private ExtendedFloatingActionButton fabNuevo;
    private AdminViewModel viewModel;

    private final List<PacienteEntity> todosPacientes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pacientes);

        View btnVolver = findViewById(R.id.volver);
        if (btnVolver != null) {
            btnVolver.setOnClickListener(v -> finish());
        }

        tvSubtitulo = findViewById(R.id.subtituloPacientes);
        tvSinPacientes = findViewById(R.id.sinPacientes);
        etBuscar = findViewById(R.id.buscar);
        contenedorLista = findViewById(R.id.listaContenedor);
        fabNuevo = findViewById(R.id.nuevoPaciente);

        if (fabNuevo != null) {
            fabNuevo.setOnClickListener(v -> startActivity(new Intent(this, RegistrarPacienteActivity.class)));
        }

        viewModel = new ViewModelProvider(this).get(AdminViewModel.class);

        // 1. Cargar lista completa de pacientes desde SQLite
        viewModel.getPacientes().observe(this, lista -> {
            todosPacientes.clear();
            if (lista != null) {
                todosPacientes.addAll(lista);
            }
            if (tvSubtitulo != null) {
                tvSubtitulo.setText(todosPacientes.size() + (todosPacientes.size() == 1 ? " registrado" : " registrados"));
            }
            filtrar(etBuscar != null && etBuscar.getText() != null ? etBuscar.getText().toString() : "");
        });

        // 2. Búsqueda en tiempo real
        if (etBuscar != null) {
            etBuscar.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filtrar(s.toString());
                }
                @Override public void afterTextChanged(Editable s) {}
            });
        }

        // 3. Navegación Inferior
        View navInicio = findViewById(R.id.opcionInicio);
        if (navInicio != null) {
            navInicio.setOnClickListener(v -> {
                startActivity(new Intent(this, AdminInicioActivity.class));
                finish();
            });
        }

        View navCitas = findViewById(R.id.opcionCitas);
        if (navCitas != null) {
            navCitas.setOnClickListener(v -> {
                startActivity(new Intent(this, AgendarCitaActivity.class));
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

    private void filtrar(String query) {
        if (contenedorLista == null) return;
        contenedorLista.removeAllViews();

        String q = query.toLowerCase().trim();
        List<PacienteEntity> filtrados = new ArrayList<>();
        for (PacienteEntity p : todosPacientes) {
            if (p.getNombreCompleto().toLowerCase().contains(q) || p.getIdentidad().toLowerCase().contains(q)) {
                filtrados.add(p);
            }
        }

        if (filtrados.isEmpty()) {
            if (tvSinPacientes != null) tvSinPacientes.setVisibility(View.VISIBLE);
            return;
        }

        if (tvSinPacientes != null) tvSinPacientes.setVisibility(View.GONE);

        for (PacienteEntity p : filtrados) {
            MaterialCardView card = new MaterialCardView(this);
            card.setRadius(32f);
            card.setStrokeColor(getResources().getColor(R.color.input_stroke, getTheme()));
            card.setCardBackgroundColor(getResources().getColor(R.color.white, getTheme()));

            LinearLayout inner = new LinearLayout(this);
            inner.setOrientation(LinearLayout.VERTICAL);
            inner.setPadding(32, 32, 32, 32);

            TextView tvNombre = new TextView(this);
            tvNombre.setText(p.getNombreCompleto());
            tvNombre.setTextSize(16f);
            tvNombre.setTextColor(getResources().getColor(R.color.black, getTheme()));

            TextView tvDetalle = new TextView(this);
            tvDetalle.setText("DNI: " + p.getIdentidad() + " · Edad: " + p.getEdad() + " años · Estatura: " + p.getEstatura() + " m");
            tvDetalle.setTextSize(13f);
            tvDetalle.setTextColor(getResources().getColor(R.color.text_secondary, getTheme()));

            inner.addView(tvNombre);
            inner.addView(tvDetalle);
            card.addView(inner);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 0, 24);
            card.setLayoutParams(lp);

            contenedorLista.addView(card);
        }
    }
}
