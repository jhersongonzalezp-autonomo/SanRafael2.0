package com.example.sanrafael20.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sanrafael20.R;
import com.example.sanrafael20.data.local.entity.PacienteEntity;

import java.util.ArrayList;
import java.util.List;

/** Pinta cada paciente del turno con item_paciente_turno.xml. */
public class PacienteTurnoAdapter extends RecyclerView.Adapter<PacienteTurnoAdapter.VH> {

    public interface OnPacienteTurnoClickListener {
        void onSignos(PacienteEntity paciente);
        void onMedicar(PacienteEntity paciente);
    }

    private final List<PacienteEntity> pacientes = new ArrayList<>();
    private final OnPacienteTurnoClickListener listener;

    public PacienteTurnoAdapter(OnPacienteTurnoClickListener listener) {
        this.listener = listener;
    }

    public void setPacientes(List<PacienteEntity> nuevos) {
        pacientes.clear();
        pacientes.addAll(nuevos);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_paciente_turno, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        PacienteEntity p = pacientes.get(position);
        h.tvIniciales.setText(p.getIniciales());
        h.tvNombre.setText(p.getNombreCompleto());
        h.tvDetalle.setText("Doc. " + p.getIdentidad() + " · " + p.getEdad() + " años");
        h.tvConsulta.setText("Estatura: " + p.getEstatura() + " m");
        h.chipEstado.setVisibility(View.GONE);
        h.btnSignos.setOnClickListener(v -> {
            if (listener != null) listener.onSignos(p);
        });
        h.btnMedicar.setVisibility(View.VISIBLE);
        h.btnMedicar.setOnClickListener(v -> {
            if (listener != null) listener.onMedicar(p);
        });
    }

    @Override
    public int getItemCount() { return pacientes.size(); }

    static class VH extends RecyclerView.ViewHolder {
        final TextView tvIniciales, tvNombre, tvDetalle, tvConsulta;
        final TextView chipEstado;
        final View btnSignos, btnMedicar;

        VH(@NonNull View v) {
            super(v);
            tvIniciales = v.findViewById(R.id.tvIniciales);
            tvNombre = v.findViewById(R.id.tvNombre);
            tvDetalle = v.findViewById(R.id.tvDetalle);
            tvConsulta = v.findViewById(R.id.tvConsulta);
            chipEstado = v.findViewById(R.id.chipEstado);
            btnSignos = v.findViewById(R.id.btnSignos);
            btnMedicar = v.findViewById(R.id.btnMedicar);
        }
    }
}
