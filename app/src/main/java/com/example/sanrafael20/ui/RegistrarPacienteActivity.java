package com.example.sanrafael20.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.sanrafael20.R;
import com.example.sanrafael20.data.local.entity.PacienteEntity;
import com.example.sanrafael20.ui.viewmodel.PacienteViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegistrarPacienteActivity extends AppCompatActivity {

    private TextInputLayout tilNombre, tilApellido, tilDni, tilEdad, tilEstatura, tilNumeroHistoria;
    private TextInputEditText etNombre, etApellido, etDni, etEdad, etEstatura, etNumeroHistoria;
    private PacienteViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_paciente);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        tilNombre = findViewById(R.id.tilNombre);
        tilApellido = findViewById(R.id.tilApellido);
        tilDni = findViewById(R.id.tilDni);
        tilEdad = findViewById(R.id.tilEdad);
        tilEstatura = findViewById(R.id.tilEstatura);
        tilNumeroHistoria = findViewById(R.id.tilNumeroHistoria);
        etNombre = findViewById(R.id.etNombre);
        etApellido = findViewById(R.id.etApellido);
        etDni = findViewById(R.id.etDni);
        etEdad = findViewById(R.id.etEdad);
        etEstatura = findViewById(R.id.etEstatura);
        etNumeroHistoria = findViewById(R.id.etNumeroHistoria);

        viewModel = new ViewModelProvider(this).get(PacienteViewModel.class);
        viewModel.getRegistro().observe(this, resultado -> {
            if (resultado.esExito()) {
                Toast.makeText(this, "Paciente registrado con id " + resultado.getDato(),
                        Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, resultado.getError(), Toast.LENGTH_LONG).show();
                findViewById(R.id.btnRegistrar).setEnabled(true);
            }
        });

        findViewById(R.id.btnCancelar).setOnClickListener(v -> finish());
        findViewById(R.id.btnRegistrar).setOnClickListener(v -> registrar());
    }

    private void registrar() {
        boolean ok = requerido(tilNombre, etNombre)
                & requerido(tilApellido, etApellido)
                & requerido(tilDni, etDni)
                & requerido(tilNumeroHistoria, etNumeroHistoria);

        Integer edad = entero(tilEdad, etEdad, 0, 150);
        Double estatura = decimal(tilEstatura, etEstatura, 0.3, 2.5);
        if (!ok || edad == null || estatura == null) {
            return;
        }

        PacienteEntity paciente = new PacienteEntity();
        paciente.setNombre(texto(etNombre));
        paciente.setApellido(texto(etApellido));
        paciente.setTipoIdentidad("CC");
        paciente.setIdentidad(texto(etDni));
        paciente.setEdad(edad);
        paciente.setEstatura(estatura);

        findViewById(R.id.btnRegistrar).setEnabled(false); // evita doble registro
        viewModel.registrar(paciente, texto(etNumeroHistoria));
    }

    private static boolean requerido(TextInputLayout til, TextInputEditText et) {
        boolean vacio = TextUtils.isEmpty(texto(et));
        til.setError(vacio ? "Campo obligatorio" : null);
        return !vacio;
    }

    private static Integer entero(TextInputLayout til, TextInputEditText et, int min, int max) {
        try {
            int valor = Integer.parseInt(texto(et));
            if (valor >= min && valor <= max) {
                til.setError(null);
                return valor;
            }
        } catch (NumberFormatException ignorada) {
            // se informa abajo
        }
        til.setError("Debe estar entre " + min + " y " + max);
        return null;
    }

    private static Double decimal(TextInputLayout til, TextInputEditText et, double min, double max) {
        try {
            double valor = Double.parseDouble(texto(et).replace(',', '.'));
            if (valor >= min && valor <= max) {
                til.setError(null);
                return valor;
            }
        } catch (NumberFormatException ignorada) {
            // se informa abajo
        }
        til.setError("Debe estar entre " + min + " y " + max);
        return null;
    }

    private static String texto(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }
}
