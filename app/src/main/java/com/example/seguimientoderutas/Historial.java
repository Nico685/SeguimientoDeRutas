package com.example.seguimientoderutas;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Historial extends AppCompatActivity {

    private RecyclerView recyclerViewHistorial;
    private HistorialAdapter historialAdapter;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        recyclerViewHistorial = findViewById(R.id.recyclerViewHistorial);
        recyclerViewHistorial.setLayoutManager(new LinearLayoutManager(this));
        historialAdapter = new HistorialAdapter();
        recyclerViewHistorial.setAdapter(historialAdapter);
        TextView volver = findViewById(R.id.btnVolver);

        databaseReference = FirebaseDatabase.getInstance().getReference("historial_rutas");

        obtenerDatosHistorial();

        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void obtenerDatosHistorial() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<HistorialRuta> historialRutas = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    HistorialRuta historialRuta = snapshot.getValue(HistorialRuta.class);
                    if (historialRuta != null) {
                        historialRutas.add(historialRuta);
                    }
                }
                historialAdapter.setHistorialRutas(historialRutas);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}


