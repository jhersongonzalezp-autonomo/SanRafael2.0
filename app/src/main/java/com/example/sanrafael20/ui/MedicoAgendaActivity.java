package com.example.sanrafael20.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sanrafael20.R;
import com.example.sanrafael20.data.local.dto.ConsultaConPaciente;
import com.example.sanrafael20.ui.viewmodel.AgendaViewModel;
import com.example.sanrafael20.util.Fechas;
import com.example.sanrafael20.util.SesionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MedicoAgendaActivity extends AppCompatActivity {

    private static final Locale ES = Locale.forLanguageTag("es-CO");
    private static final int[] CHIPS = {R.id.chipDia8, R.id.chipDia9, R.id.chipDia10,
            R.id.chipDia11, R.id.chipDia12, R.id.chipDia13};

    private final Map<Integer, LocalDate> fechaPorChip = new HashMap<>();
    private final List<ConsultaConPaciente> todas = new ArrayList<>();
    private ConsultaAdapter adapter;
    private TextView tvFechaSeleccionada;
    private ChipGroup cgDias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda_medico);
        SesionManager sesion = new SesionManager(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setSubtitle(sesion.getNombre());
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_cerrar_sesion) {
                LoginActivity.cerrarSesion(this);
                return true;
            }
            return false;
        });

        tvFechaSeleccionada = findViewById(R.id.tvFechaSeleccionada);
        RecyclerView rvConsultas = findViewById(R.id.rvConsultas);
        rvConsultas.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ConsultaAdapter(consulta -> abrirConsulta(consulta.idConsulta, consulta.idPaciente));
        rvConsultas.setAdapter(adapter);

        configurarSemana();

        View btnIniciarDestacado = findViewById(R.id.iniciar);
        if (btnIniciarDestacado != null) {
            btnIniciarDestacado.setOnClickListener(v -> abrirConsulta(1, 1));
        }

        View navConsulta = findViewById(R.id.opcionConsulta);
        if (navConsulta != null) {
            navConsulta.setOnClickListener(v -> abrirConsulta(-1, 1));
        }

        View navHistorial = findViewById(R.id.opcionHistorial);
        if (navHistorial != null) {
            navHistorial.setOnClickListener(v -> abrirHistorial(1));
        }

        AgendaViewModel viewModel = new ViewModelProvider(this).get(AgendaViewModel.class);
        viewModel.getAgenda(sesion.getIdEmpleado()).observe(this, lista -> {
            todas.clear();
            todas.addAll(lista);
            mostrarDia(cgDias.getCheckedChipId());
        });
    }

    private void abrirConsulta(long idConsulta, long idPaciente) {
        Intent intent = new Intent(this, RealizarConsultaActivity.class);
        if (idConsulta > 0) {
            intent.putExtra(RealizarConsultaActivity.EXTRA_ID_CONSULTA, idConsulta);
        }
        if (idPaciente > 0) {
            intent.putExtra(RealizarConsultaActivity.EXTRA_ID_PACIENTE, idPaciente);
        }
        startActivity(intent);
    }

    private void abrirHistorial(long idPaciente) {
        Intent intent = new Intent(this, HistorialMedicoActivity.class);
        if (idPaciente > 0) {
            intent.putExtra(HistorialMedicoActivity.EXTRA_ID_PACIENTE, idPaciente);
        }
        startActivity(intent);
    }

    /** Los seis chips muestran de lunes a sábado de la semana actual. */
    private void configurarSemana() {
        cgDias = findViewById(R.id.cgDias);
        LocalDate hoy = LocalDate.now();
        LocalDate lunes = hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        DateTimeFormatter corto = DateTimeFormatter.ofPattern("EEE d", ES);

        for (int i = 0; i < CHIPS.length; i++) {
            LocalDate dia = lunes.plusDays(i);
            Chip chip = findViewById(CHIPS[i]);
            chip.setText(dia.format(corto));
            fechaPorChip.put(CHIPS[i], dia);
            if (dia.equals(hoy)) {
                chip.setChecked(true);
            }
        }
        if (cgDias.getCheckedChipId() == ChipGroup.NO_ID) {
            ((Chip) findViewById(CHIPS[0])).setChecked(true); // domingo: queda el lunes
        }
        cgDias.setOnCheckedStateChangeListener((group, ids) -> {
            if (!ids.isEmpty()) {
                mostrarDia(ids.get(0));
            }
        });
    }

    private void mostrarDia(int chipId) {
        LocalDate dia = fechaPorChip.get(chipId);
        if (dia == null) {
            return;
        }
        String fecha = dia.format(Fechas.FECHA);
        List<ConsultaConPaciente> delDia = new ArrayList<>();
        for (ConsultaConPaciente c : todas) {
            if (fecha.equals(c.fecha)) {
                delDia.add(c);
            }
        }
        adapter.setConsultas(delDia);

        String titulo = dia.format(DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", ES));
        String cantidad = delDia.isEmpty() ? "sin consultas"
                : delDia.size() + (delDia.size() == 1 ? " consulta" : " consultas");
        tvFechaSeleccionada.setText(titulo + " · " + cantidad);
    }
}
