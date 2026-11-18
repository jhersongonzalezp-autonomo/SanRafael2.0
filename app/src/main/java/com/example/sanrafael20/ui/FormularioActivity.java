package com.example.sanrafael20.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sanrafael20.R;
import com.example.sanrafael20.data.local.AppDatabase;
import com.example.sanrafael20.data.local.entity.EmpleadoEntity;
import com.example.sanrafael20.util.Fechas;
import com.example.sanrafael20.util.PasswordUtils;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalDate;

public class FormularioActivity extends AppCompatActivity {

    private TextView tabMedico, tabEnfermero, tabAdministrativo;
    private TextInputLayout tilCampoDinamico1;
    private TextInputEditText etNombre, etApellido, etIdentidad, etEdad, etFechaIngreso, etSueldo;
    private Button btnContratar, btnMostrarLista;

    private String rolSeleccionado = "Medico";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);

        MaterialToolbar toolbar = findViewById(R.id.toolbarFormulario);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        tabMedico = findViewById(R.id.tabMedico);
        tabEnfermero = findViewById(R.id.tabEnfermero);
        tabAdministrativo = findViewById(R.id.tabAdministrativo);

        tilCampoDinamico1 = findViewById(R.id.tilCampoDinamico1);
        etNombre = findViewById(R.id.etNombre);
        etApellido = findViewById(R.id.etApellido);
        etIdentidad = findViewById(R.id.etIdentidad);
        etEdad = findViewById(R.id.etEdad);
        etFechaIngreso = findViewById(R.id.etFechaIngreso);
        etSueldo = findViewById(R.id.etSueldo);

        btnContratar = findViewById(R.id.btnContratar);
        btnMostrarLista = findViewById(R.id.btnMostrarLista);

        if (etFechaIngreso != null) {
            etFechaIngreso.setText(Fechas.FECHA.format(LocalDate.now()));
        }

        // Pestañas de rol
        if (tabMedico != null) {
            tabMedico.setOnClickListener(v -> seleccionarRol("Medico"));
        }
        if (tabEnfermero != null) {
            tabEnfermero.setOnClickListener(v -> seleccionarRol("Enfermero"));
        }
        if (tabAdministrativo != null) {
            tabAdministrativo.setOnClickListener(v -> seleccionarRol("Administrativo"));
        }

        if (btnContratar != null) {
            btnContratar.setOnClickListener(v -> contratar());
        }

        if (btnMostrarLista != null) {
            btnMostrarLista.setOnClickListener(v -> {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            });
        }
    }

    private void seleccionarRol(String rol) {
        this.rolSeleccionado = rol;
        if (tabMedico != null) {
            tabMedico.setBackgroundColor(getResources().getColor("Medico".equals(rol) ? R.color.white : android.R.color.transparent, getTheme()));
        }
        if (tabEnfermero != null) {
            tabEnfermero.setBackgroundColor(getResources().getColor("Enfermero".equals(rol) ? R.color.white : android.R.color.transparent, getTheme()));
        }
        if (tabAdministrativo != null) {
            tabAdministrativo.setBackgroundColor(getResources().getColor("Administrativo".equals(rol) ? R.color.white : android.R.color.transparent, getTheme()));
        }

        if (tilCampoDinamico1 != null) {
            if ("Medico".equals(rol)) {
                tilCampoDinamico1.setHint("Especialidad");
            } else if ("Enfermero".equals(rol)) {
                tilCampoDinamico1.setHint("Turno asignado");
            } else {
                tilCampoDinamico1.setHint("Departamento");
            }
        }
    }

    private void contratar() {
        String nombre = texto(etNombre);
        String apellido = texto(etApellido);
        String dni = texto(etIdentidad);
        String edadStr = texto(etEdad);
        String sueldoStr = texto(etSueldo);

        if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(apellido) || TextUtils.isEmpty(dni)) {
            Toast.makeText(this, "Completa nombre, apellido y DNI", Toast.LENGTH_SHORT).show();
            return;
        }

        int edad = 30;
        try {
            if (!TextUtils.isEmpty(edadStr)) edad = Integer.parseInt(edadStr);
        } catch (NumberFormatException ignored) {}

        double salario = 3000000;
        try {
            if (!TextUtils.isEmpty(sueldoStr)) salario = Double.parseDouble(sueldoStr.replace(',', '.'));
        } catch (NumberFormatException ignored) {}

        EmpleadoEntity nuevo = new EmpleadoEntity();
        nuevo.setIdCentro(1);
        nuevo.setNombre(nombre);
        nuevo.setApellido(apellido);
        nuevo.setTipoIdentidad("CC");
        nuevo.setIdentidad(dni);
        nuevo.setEdad(edad);
        nuevo.setSalario(salario);
        nuevo.setRol(rolSeleccionado);
        nuevo.setFechaIngreso(texto(etFechaIngreso).isEmpty() ? Fechas.hoy() : texto(etFechaIngreso));
        nuevo.setHashPassword(PasswordUtils.sha256("123456"));
        String emailClean = (nombre + "." + apellido).toLowerCase().replaceAll("[^a-z0-9.]", "");
        nuevo.setEmail(emailClean + System.currentTimeMillis() + "@sanrafael.test");

        if (btnContratar != null) btnContratar.setEnabled(false);

        AppDatabase.EJECUTOR.execute(() -> {
            try {
                long idGenerado = AppDatabase.getInstance(getApplicationContext()).empleadoDao().insertar(nuevo);
                runOnUiThread(() -> {
                    Toast.makeText(this, "✓ Empleado contratado con éxito (ID: " + idGenerado + ")", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error al contratar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    if (btnContratar != null) btnContratar.setEnabled(true);
                });
            }
        });
    }

    private static String texto(TextInputEditText et) {
        return et == null || et.getText() == null ? "" : et.getText().toString().trim();
    }
}
