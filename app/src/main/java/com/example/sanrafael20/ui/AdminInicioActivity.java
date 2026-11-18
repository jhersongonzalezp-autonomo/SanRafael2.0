package com.example.sanrafael20.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.sanrafael20.R;
import com.example.sanrafael20.ui.viewmodel.AdminViewModel;
import com.example.sanrafael20.util.SesionManager;
import com.google.android.material.appbar.MaterialToolbar;

public class AdminInicioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_inicio);

        SesionManager sesion = new SesionManager(this);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.inflateMenu(R.menu.menu_main);
            toolbar.setTitle("Hola, " + sesion.getNombre());
            toolbar.setSubtitle("Administrativo · Recepción");
            toolbar.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_cerrar_sesion) {
                    LoginActivity.cerrarSesion(this);
                    return true;
                }
                return false;
            });
        }

        // Grid 2x2 Acciones
        View btnRegPac = findViewById(R.id.cardRegistrarPaciente);
        if (btnRegPac != null) {
            btnRegPac.setOnClickListener(v -> startActivity(new Intent(this, RegistrarPacienteActivity.class)));
        }

        View btnAgendar = findViewById(R.id.agendarCita);
        if (btnAgendar != null) {
            btnAgendar.setOnClickListener(v -> startActivity(new Intent(this, AgendarCitaActivity.class)));
        }

        View btnVerPac = findViewById(R.id.verPacientes);
        if (btnVerPac != null) {
            btnVerPac.setOnClickListener(v -> startActivity(new Intent(this, PacientesActivity.class)));
        }

        View btnFactura = findViewById(R.id.cardGenerarFactura);
        if (btnFactura != null) {
            btnFactura.setOnClickListener(v -> startActivity(new Intent(this, FacturaActivity.class)));
        }

        // Carga de contadores dinámicos desde SQLite
        AdminViewModel viewModel = new ViewModelProvider(this).get(AdminViewModel.class);

        TextView tvTotalPacientes = findViewById(R.id.totalPacientes);
        if (tvTotalPacientes != null) {
            viewModel.getTotalPacientes().observe(this, count -> tvTotalPacientes.setText(String.valueOf(count)));
        }

        TextView tvTotalConsultas = findViewById(R.id.totalConsultas);
        if (tvTotalConsultas != null) {
            viewModel.getTotalConsultas().observe(this, count -> tvTotalConsultas.setText(String.valueOf(count)));
        }

        TextView tvTotalEmpleados = findViewById(R.id.totalEmpleados);
        if (tvTotalEmpleados != null) {
            viewModel.getTotalEmpleados().observe(this, count -> tvTotalEmpleados.setText(String.valueOf(count)));
        }

        // Cargar próxima cita desde SQLite
        TextView tvCitaTitulo = findViewById(R.id.citaTitulo);
        TextView tvCitaDetalle = findViewById(R.id.citaDetalle);
        viewModel.getProximasConsultas().observe(this, lista -> {
            if (lista != null && !lista.isEmpty()) {
                if (tvCitaTitulo != null) tvCitaTitulo.setText(lista.get(0).getNombreCompleto());
                if (tvCitaDetalle != null) tvCitaDetalle.setText(lista.get(0).fecha + " · " + lista.get(0).motivo);
            }
        });

        View tarjetaProxima = findViewById(R.id.tarjetaProximaCita);
        if (tarjetaProxima != null) {
            tarjetaProxima.setOnClickListener(v -> startActivity(new Intent(this, AgendarCitaActivity.class)));
        }

        // Navegación Inferior
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
}
