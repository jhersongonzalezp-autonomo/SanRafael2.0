package com.example.sanrafael20.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.sanrafael20.R;
import com.example.sanrafael20.data.local.entity.EmpleadoEntity;
import com.example.sanrafael20.ui.viewmodel.LoginViewModel;
import com.example.sanrafael20.util.SesionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    public static final String ROL_MEDICO = "Medico";
    public static final String ROL_ENFERMERO = "Enfermero";
    public static final String ROL_ADMINISTRATIVO = "Administrativo";

    private TextInputLayout tilIdEmpleado, tilContrasena;
    private TextInputEditText etIdEmpleado, etContrasena;
    private LoginViewModel viewModel;
    private SesionManager sesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sesion = new SesionManager(this);
        if (sesion.estaActiva()) {          // ya había iniciado sesión
            irAInicio(sesion.getRol());
            return;
        }

        setContentView(R.layout.activity_login);
        tilIdEmpleado = findViewById(R.id.tilIdEmpleado);
        tilContrasena = findViewById(R.id.tilContrasena);
        etIdEmpleado = findViewById(R.id.etIdEmpleado);
        etContrasena = findViewById(R.id.etContrasena);

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // Prueba de conexión: el nombre del centro sale de la base de datos.
        TextView tvNombreCentro = findViewById(R.id.tvNombreCentro);
        viewModel.getCentro().observe(this, centro -> {
            if (centro != null) {
                tvNombreCentro.setText(centro.getNombre());
            }
        });

        viewModel.getResultado().observe(this, resultado -> {
            if (resultado.esExito()) {
                EmpleadoEntity e = resultado.getDato();
                sesion.iniciar(e.getIdEmpleado(), e.getNombreCompleto(), e.getRol());
                irAInicio(e.getRol());
            } else {
                Toast.makeText(this, resultado.getError(), Toast.LENGTH_LONG).show();
            }
        });

        // Botón principal de Inicio de Sesión
        findViewById(R.id.btnIniciarSesion).setOnClickListener(v -> intentar());
/*
        // Manejadores opcionales para las tarjetas de rol
        if (findViewById(R.id.cardRolMedico) != null) {
            findViewById(R.id.cardRolMedico).setOnClickListener(v -> intentar());
        }
        if (findViewById(R.id.cardRolEnfermero) != null) {
            findViewById(R.id.cardRolEnfermero).setOnClickListener(v -> intentar());
        }
        if (findViewById(R.id.cardRolAdministrativo) != null) {
            findViewById(R.id.cardRolAdministrativo).setOnClickListener(v -> intentar());
        }
        if (findViewById(R.id.cardRolAdministrador) != null) {
            findViewById(R.id.cardRolAdministrador).setOnClickListener(v -> intentar());
        }
*/
        /*
        // Código anterior de click directo por rol:
        findViewById(R.id.cardRolMedico).setOnClickListener(v -> intentar(ROL_MEDICO));
        findViewById(R.id.cardRolEnfermero).setOnClickListener(v -> intentar(ROL_ENFERMERO));
        findViewById(R.id.cardRolAdministrativo).setOnClickListener(v -> intentar(ROL_ADMINISTRATIVO));
        */
    }

    private void intentar() {
        String identificador = texto(etIdEmpleado);
        String clave = texto(etContrasena);
        tilIdEmpleado.setError(TextUtils.isEmpty(identificador) ? "Ingresa tu ID o correo" : null);
        tilContrasena.setError(TextUtils.isEmpty(clave) ? "Ingresa tu contraseña" : null);
        if (!TextUtils.isEmpty(identificador) && !TextUtils.isEmpty(clave)) {
            viewModel.ingresar(identificador, clave);
        }
    }

    /*
    private void intentar(String rol) {
        String identificador = texto(etIdEmpleado);
        String clave = texto(etContrasena);
        tilIdEmpleado.setError(TextUtils.isEmpty(identificador) ? "Ingresa tu ID o correo" : null);
        tilContrasena.setError(TextUtils.isEmpty(clave) ? "Ingresa tu contraseña" : null);
        if (!TextUtils.isEmpty(identificador) && !TextUtils.isEmpty(clave)) {
            viewModel.ingresar(identificador, clave, rol);
        }
    }
    */

    private void irAInicio(String rol) {
        if (rol == null) rol = "";
        Class<?> destino;
        if (ROL_MEDICO.equalsIgnoreCase(rol) || "Médico".equalsIgnoreCase(rol)) {
            destino = MedicoAgendaActivity.class;
        } else if (ROL_ENFERMERO.equalsIgnoreCase(rol)) {
            destino = EnfermeroInicioActivity.class;
        } else {
            destino = AdminInicioActivity.class;
        }
        startActivity(new Intent(this, destino));
        finish();
    }

    /** Lo usan las pantallas de cada rol desde el menú "Cerrar sesión". */
    public static void cerrarSesion(Activity actual) {
        new SesionManager(actual).cerrar();
        Intent intent = new Intent(actual, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        actual.startActivity(intent);
        actual.finish();
    }

    private static String texto(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }
}
