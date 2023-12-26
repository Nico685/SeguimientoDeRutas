package com.example.seguimientoderutas;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Mapa extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    private GoogleMap googleMap; // Declarar googleMap como una variable de clase
    private Button btnAgregarMarcador;
    private Marker marcadorPendiente; // Marcador que se va a agregar al pulsar el botón
    private boolean marcadorPulsado = false; // Variable para rastrear si se pulsó el mapa sin agregar un marcador

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        TextView volver = findViewById(R.id.btnVolver);
        btnAgregarMarcador = findViewById(R.id.btnAgregarMarcador);

        // Obtener el SupportMapFragment y recibir la notificación cuando el mapa esté listo para ser usado.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Spinner spinnerMapType = findViewById(R.id.spinnerMapType);
        spinnerMapType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Obtén el tipo de mapa seleccionado
                int mapType = GoogleMap.MAP_TYPE_NORMAL;
                switch (position) {
                    case 0:
                        mapType = GoogleMap.MAP_TYPE_NORMAL;
                        break;
                    case 1:
                        mapType = GoogleMap.MAP_TYPE_HYBRID;
                        break;
                    case 2:
                        mapType = GoogleMap.MAP_TYPE_TERRAIN;
                        break;
                }

                // Cambia el tipo de mapa
                if (googleMap != null) {
                    googleMap.setMapType(mapType);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Configurar el listener para el botón de agregar marcador
        btnAgregarMarcador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (marcadorPendiente != null) {
                    // Confirmar el marcador y asignarle el título
                    marcadorPendiente.setTitle("Marcador");
                    marcadorPendiente = null; // Reiniciar el marcador pendiente
                    btnAgregarMarcador.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "Marcador agregado con éxito", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        // Configurar el mapa cuando esté listo
        googleMap = map; // Asignar el mapa a la variable de clase

        // Configurar controles de zoom
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        // Configurar gestos de zoom
        googleMap.getUiSettings().setZoomGesturesEnabled(true);

        // Habilitar la capa de mi ubicación actual
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        } else {
            // Si no tienes permisos, solicítalos aquí
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        // Mover la cámara a Chile al abrir el mapa
        LatLng chile = new LatLng(-35.675147, -71.542969); // Coordenadas de Chile
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(chile, 6));

        // Configurar el listener para el click en el mapa
        googleMap.setOnMapClickListener(this);

        // Configurar el listener para el clic en el marcador
        googleMap.setOnMarkerClickListener(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // Ocultar el botón si se hace clic en un marcador existente
        btnAgregarMarcador.setVisibility(View.INVISIBLE);

        // Quitar el marcador pendiente si se pulsó el mapa sin agregar un marcador
        if (marcadorPulsado && marcadorPendiente != null) {
            marcadorPendiente.remove();
            marcadorPendiente = null;
        }

        // Restablecer el estado
        marcadorPulsado = false;

        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        // Colocar un marcador pendiente en la ubicación al pulsar el mapa
        if (marcadorPendiente != null) {
            marcadorPendiente.remove(); // Quitar marcador anterior si hay uno pendiente
        }
        marcadorPendiente = googleMap.addMarker(new MarkerOptions().position(latLng));
        btnAgregarMarcador.setVisibility(View.VISIBLE);

        // Marcar que se ha pulsado el mapa sin agregar un marcador
        marcadorPulsado = true;
    }
}
