package com.example.sanrafael20.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sanrafael20.R;
import com.example.sanrafael20.data.local.dto.ConsultaConPaciente;

import java.util.ArrayList;
import java.util.List;

/** Pinta cada consulta de la agenda con item_consulta.xml. */
public class ConsultaAdapter extends RecyclerView.Adapter<ConsultaAdapter.VH> {

    public interface OnConsultaClickListener {
        void onIniciarConsulta(ConsultaConPaciente consulta);
    }

    private final List<ConsultaConPaciente> consultas = new ArrayList<>();
    private OnConsultaClickListener listener;

    public ConsultaAdapter() { }

    public ConsultaAdapter(OnConsultaClickListener listener) {
        this.listener = listener;
    }

    public void setOnConsultaClickListener(OnConsultaClickListener listener) {
        this.listener = listener;
    }

    public void setConsultas(List<ConsultaConPaciente> nuevas) {
        consultas.clear();
        consultas.addAll(nuevas);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_consulta, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        ConsultaConPaciente c = consultas.get(position);
        h.tvIniciales.setText(c.getIniciales());
        h.tvNombre.setText(c.getNombreCompleto());
        h.tvDetalle.setText("Cita #" + c.idConsulta + " · " + c.fecha);
        h.tvMotivo.setText("Motivo: " + c.motivo);
        h.chipEstado.setText(c.estado);
        h.btnIniciarConsulta.setVisibility(View.VISIBLE);
        h.btnIniciarConsulta.setOnClickListener(v -> {
            if (listener != null) {
                listener.onIniciarConsulta(c);
            }
        });
    }

    @Override
    public int getItemCount() { return consultas.size(); }

    static class VH extends RecyclerView.ViewHolder {
        final TextView tvIniciales, tvNombre, tvDetalle, tvMotivo;
        final TextView chipEstado;
        final View btnIniciarConsulta;

        VH(@NonNull View v) {
            super(v);
            tvIniciales = v.findViewById(R.id.tvIniciales);
            tvNombre = v.findViewById(R.id.tvNombre);
            tvDetalle = v.findViewById(R.id.tvDetalle);
            tvMotivo = v.findViewById(R.id.tvMotivo);
            chipEstado = v.findViewById(R.id.chipEstado);
            btnIniciarConsulta = v.findViewById(R.id.btnIniciarConsulta);
        }
    }
}
