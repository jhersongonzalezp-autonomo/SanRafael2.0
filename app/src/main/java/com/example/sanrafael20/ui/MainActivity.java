package com.example.sanrafael20.ui;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sanrafael20.R;
import com.example.sanrafael20.data.local.AppDatabase;
import com.example.sanrafael20.data.local.entity.EmpleadoEntity;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView tvSubtitulo;
    private LinearLayout layoutLista, layoutDetalle;
    private final List<EmpleadoEntity> todosEmpleados = new ArrayList<>();
    private String filtroRol = "Todos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView btnBack = findViewById(R.id.btnBackArrow);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        tvSubtitulo = findViewById(R.id.tvSubtituloPersonal);
        layoutLista = findViewById(R.id.layoutListaEmpleados);
        layoutDetalle = findViewById(R.id.layoutContenidoDetalle);

        View btnFormulario = findViewById(R.id.btnAbrirFormularioMedico);
        if (btnFormulario != null) {
            btnFormulario.setOnClickListener(v -> startActivity(new Intent(this, FormularioActivity.class)));
        }

        // Cargar lista completa del personal desde SQLite
        AppDatabase.getInstance(this).empleadoDao().observarTodos().observe(this, lista -> {
            todosEmpleados.clear();
            if (lista != null) {
                todosEmpleados.addAll(lista);
            }
            if (tvSubtitulo != null) {
                tvSubtitulo.setText("Clínica San Rafael · " + todosEmpleados.size() + " empleados");
            }
            actualizarLista();
        });

        // Configurar chips de filtro por rol
        setupFilter(R.id.chipTodos, "Todos");
        setupFilter(R.id.chipMedicos, "Medico");
        setupFilter(R.id.chipEnfermeros, "Enfermero");
        setupFilter(R.id.chipAdministrativos, "Administrativo");

        // Navegación Inferior
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

        View navCitas = findViewById(R.id.opcionCitas);
        if (navCitas != null) {
            navCitas.setOnClickListener(v -> {
                startActivity(new Intent(this, AgendarCitaActivity.class));
                finish();
            });
        }
    }

    private void setupFilter(int viewId, String rol) {
        View v = findViewById(viewId);
        if (v != null) {
            v.setOnClickListener(view -> {
                filtroRol = rol;
                actualizarLista();
            });
        }
    }

    private void actualizarLista() {
        if (layoutLista == null) return;
        layoutLista.removeAllViews();

        List<EmpleadoEntity> filtrados = new ArrayList<>();
        for (EmpleadoEntity e : todosEmpleados) {
            String r = e.getRol() == null ? "" : e.getRol();
            if (!"Todos".equals(filtroRol)) {
                if (!r.equalsIgnoreCase(filtroRol) && !("Medico".equals(filtroRol) && "Médico".equalsIgnoreCase(r))) {
                    continue;
                }
            }
            filtrados.add(e);
        }

        int widthPx = (int) (220 * getResources().getDisplayMetrics().density);

        for (EmpleadoEntity e : filtrados) {
            MaterialCardView card = new MaterialCardView(this);
            card.setRadius(24f);
            card.setStrokeColor(getResources().getColor(R.color.input_stroke, getTheme()));
            card.setCardBackgroundColor(getResources().getColor(R.color.white, getTheme()));
            card.setClickable(true);
            card.setFocusable(true);

            LinearLayout inner = new LinearLayout(this);
            inner.setOrientation(LinearLayout.VERTICAL);
            inner.setPadding(24, 24, 24, 24);

            TextView tvNombre = new TextView(this);
            tvNombre.setText(e.getNombreCompleto());
            tvNombre.setTextSize(15f);
            tvNombre.setTypeface(null, Typeface.BOLD);
            tvNombre.setTextColor(getResources().getColor(R.color.black, getTheme()));

            TextView tvDetalle = new TextView(this);
            tvDetalle.setText("Rol: " + e.getRol() + "\nID: " + e.getIdEmpleado());
            tvDetalle.setTextSize(12f);
            tvDetalle.setTextColor(getResources().getColor(R.color.text_secondary, getTheme()));

            inner.addView(tvNombre);
            inner.addView(tvDetalle);
            card.addView(inner);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    widthPx, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 24, 0);
            card.setLayoutParams(lp);

            card.setOnClickListener(v -> mostrarDetalleEmpleado(e));

            layoutLista.addView(card);
        }

        if (!filtrados.isEmpty()) {
            mostrarDetalleEmpleado(filtrados.get(0));
        } else {
            mostrarDetalleEmpleado(null);
        }
    }

    private void mostrarDetalleEmpleado(EmpleadoEntity e) {
        if (layoutDetalle == null) return;
        layoutDetalle.removeAllViews();

        if (e == null) {
            TextView empty = new TextView(this);
            empty.setText("No hay empleados registrados en esta categoría.");
            empty.setPadding(16, 16, 16, 16);
            empty.setTextColor(getResources().getColor(R.color.text_secondary, getTheme()));
            layoutDetalle.addView(empty);
            return;
        }

        String icono = "📋";
        String r = e.getRol() != null ? e.getRol() : "";
        if (r.toLowerCase().contains("medic") || r.toLowerCase().contains("médic")) {
            icono = "🩺";
        } else if (r.toLowerCase().contains("enferm")) {
            icono = "📈";
        }

        TextView tvHeader = new TextView(this);
        tvHeader.setText(icono + " " + e.getNombreCompleto());
        tvHeader.setTextSize(17f);
        tvHeader.setTypeface(null, Typeface.BOLD);
        tvHeader.setTextColor(getResources().getColor(R.color.black, getTheme()));

        TextView tvInfo = new TextView(this);
        String infoText = "• Rol: " + e.getRol()
                + "\n• ID Empleado: " + e.getIdEmpleado()
                + "\n• Documento DNI: " + e.getIdentidad() + " (" + (e.getTipoIdentidad() != null ? e.getTipoIdentidad() : "CC") + ")"
                + "\n• Edad: " + e.getEdad() + " años"
                + "\n• Sueldo Base: $" + String.format(Locale.getDefault(), "%,.0f", e.getSalario()) + " COP"
                + "\n• Fecha Ingreso: " + (e.getFechaIngreso() != null ? e.getFechaIngreso() : "2026-01-01")
                + "\n• Estado: " + (e.getFechaSalida() == null ? "Activo" : "Inactivo desde " + e.getFechaSalida());

        tvInfo.setText(infoText);
        tvInfo.setTextSize(13f);
        tvInfo.setPadding(0, 12, 0, 0);
        tvInfo.setTextColor(getResources().getColor(R.color.text_secondary, getTheme()));

        layoutDetalle.addView(tvHeader);
        layoutDetalle.addView(tvInfo);
    }
}
