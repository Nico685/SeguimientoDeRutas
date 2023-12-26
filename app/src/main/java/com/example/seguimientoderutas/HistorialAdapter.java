package com.example.seguimientoderutas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder> {

    private List<HistorialRuta> historialRutas;

    public void setHistorialRutas(List<HistorialRuta> historialRutas) {
        this.historialRutas = historialRutas;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistorialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_historial, parent, false);
        return new HistorialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistorialViewHolder holder, int position) {
        HistorialRuta historialRuta = historialRutas.get(position);
        holder.bind(historialRuta);
    }

    @Override
    public int getItemCount() {
        return historialRutas != null ? historialRutas.size() : 0;
    }

    public HistorialRuta getItem(int position) {
        if (historialRutas != null && position >= 0 && position < historialRutas.size()) {
            return historialRutas.get(position);
        }
        return null;
    }

    static class HistorialViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewNombreRuta;
        private final TextView textViewFecha;
        private final TextView textViewDuracion;
        private final TextView textViewDistancia;
        private final TextView textViewPosicionInicial;
        private final TextView textViewPosicionFinal;

        public HistorialViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombreRuta = itemView.findViewById(R.id.textViewNombreRuta);
            textViewFecha = itemView.findViewById(R.id.textViewFecha);
            textViewDuracion = itemView.findViewById(R.id.textViewDuracion);
            textViewDistancia = itemView.findViewById(R.id.textViewDistancia);
            textViewPosicionInicial = itemView.findViewById(R.id.textViewPosicionInicial);
            textViewPosicionFinal = itemView.findViewById(R.id.textViewPosicionFinal);

            makeTextViewSelectable(textViewNombreRuta);
            makeTextViewSelectable(textViewFecha);
            makeTextViewSelectable(textViewDuracion);
            makeTextViewSelectable(textViewDistancia);
            makeTextViewSelectable(textViewPosicionInicial);
            makeTextViewSelectable(textViewPosicionFinal);
        }

        private void makeTextViewSelectable(TextView textView) {
            textView.setTextIsSelectable(true);
            textView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return false;
                }
            });
        }

        public void bind(HistorialRuta historialRuta) {
            textViewNombreRuta.setText("Nombre de ruta: " + historialRuta.nombreRuta);
            textViewFecha.setText("Fecha: " + historialRuta.fecha);
            textViewDuracion.setText("Duracion: " + String.valueOf(historialRuta.duracion) + " segundos");
            textViewDistancia.setText("Distancia: " + String.format("%.2f km", historialRuta.distanciaTotal / 1000));
            textViewPosicionInicial.setText("Posicion Inicial: " + historialRuta.posicionInicial);
            textViewPosicionFinal.setText("Posicion Final: " + historialRuta.posicionFinal);
        }
    }
}

