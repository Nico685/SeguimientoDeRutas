package com.example.seguimientoderutas;

import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import android.Manifest;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.content.pm.PackageManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Registro extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap googleMap;
    private EditText editTextNombreRuta;
    private TextView textViewFecha, textViewDuracion, textViewPosicionInicial, textViewPosicionFinal;
    private Button btnInicioRuta, btnFinRuta;
    private TextView textViewDistancia;
    private double distanciaTotal = 0;
    private Chronometer chronometer;
    private DatabaseReference databaseReference;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private String currentRutaKey;

    private boolean rutaIniciada = false;
    private LatLng inicioRutaLatLng;
    private List<LatLng> rutaPoints;
    private Polyline rutaPolyline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapRegistro);
        mapFragment.getMapAsync(this);

        editTextNombreRuta = findViewById(R.id.editTextNombreRuta);
        textViewFecha = findViewById(R.id.textViewFecha);
        textViewDuracion = findViewById(R.id.textViewDuracion);
        textViewDistancia = findViewById(R.id.textViewDistancia);
        textViewPosicionInicial = findViewById(R.id.textViewPosicionInicial);
        textViewPosicionFinal = findViewById(R.id.textViewPosicionFinal);

        TextView volver = findViewById(R.id.btnVolver);
        btnInicioRuta = findViewById(R.id.btnInicioRuta);
        btnFinRuta = findViewById(R.id.btnFinRuta);

        chronometer = findViewById(R.id.chronometer);

        databaseReference = FirebaseDatabase.getInstance().getReference("historial_rutas");

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (rutaIniciada) {
                    // Actualizar la posición del usuario
                    actualizarRuta(location);
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        btnInicioRuta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarRuta();
            }
        });

        btnFinRuta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalizarRuta();
            }
        });

        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void iniciarRuta() {
        final String nombreRuta = editTextNombreRuta.getText().toString().trim();
        if (!rutaIniciada && !nombreRuta.isEmpty()) {
            if (!isGPSEnabled()) {
                Toast.makeText(this, "Por favor, active el GPS para iniciar la ruta.", Toast.LENGTH_SHORT).show();
                return;
            }
            databaseReference.orderByChild("nombreRuta").equalTo(nombreRuta).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Toast.makeText(Registro.this, "El nombre de ruta ya existe. Por favor, elija otro nombre.", Toast.LENGTH_SHORT).show();
                    } else {
                        chronometer.setBase(SystemClock.elapsedRealtime());
                        chronometer.start();

                        textViewFecha.setText(obtenerFechaActual());

                        currentRutaKey = databaseReference.push().getKey();

                        Map<String, Object> ruta = new HashMap<>();
                        ruta.put("nombreRuta", nombreRuta);
                        ruta.put("fecha", obtenerFechaActual());
                        ruta.put("duracion", 0);

                        Location location = obtenerPosicionActual();
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            inicioRutaLatLng = new LatLng(latitude, longitude);
                            rutaPoints = new ArrayList<>();
                            rutaPoints.add(inicioRutaLatLng);
                            rutaPolyline = googleMap.addPolyline(new PolylineOptions().clickable(true).addAll(rutaPoints));
                            rutaPolyline.setColor(Color.RED);

                            ruta.put("posicionInicial", "Latitud: " + latitude + ", Longitud: " + longitude);
                            textViewPosicionInicial.setText("Latitud: " + latitude + ", Longitud: " + longitude);
                        } else {
                            ruta.put("posicionInicial", "Posición inicial no disponible");
                            textViewPosicionInicial.setText("Posición inicial no disponible");
                        }

                        ruta.put("posicionFinal", "");

                        databaseReference.child(currentRutaKey).setValue(ruta);

                        rutaIniciada = true;
                        Toast.makeText(Registro.this, "Ruta iniciada", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(Registro.this, "Error en la base de datos", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Ingrese un nombre de ruta válido o la ruta ya ha sido iniciada", Toast.LENGTH_SHORT).show();
        }
    }

    private void actualizarRuta(Location location) {
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            LatLng nuevaPosicion = new LatLng(latitude, longitude);
            rutaPoints.add(nuevaPosicion);
            rutaPolyline.setPoints(rutaPoints);

            // Calcular la distancia entre los dos puntos y sumarla a la distancia total
            if (rutaPoints.size() > 1) {
                LatLng puntoAnterior = rutaPoints.get(rutaPoints.size() - 2);
                double distancia = calcularDistancia(puntoAnterior, nuevaPosicion);
                distanciaTotal += distancia;
                textViewDistancia.setText(String.format("%.2f km", distanciaTotal / 1000)); // Muestra la distancia en kilómetros
            }

            // Centrar la cámara en la nueva posición
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(nuevaPosicion));
        }
    }

    private void finalizarRuta() {
        if (rutaIniciada) {
            chronometer.stop();
            long duracionEnSegundos = (SystemClock.elapsedRealtime() - chronometer.getBase()) / 1000;
            textViewDuracion.setText(String.valueOf(duracionEnSegundos) + " segundos");

            Location location = obtenerPosicionActual();
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                textViewPosicionFinal.setText("Latitud: " + latitude + ", Longitud: " + longitude);

                if (currentRutaKey != null) {
                    DatabaseReference rutaRef = databaseReference.child(currentRutaKey);
                    rutaRef.child("duracion").setValue(duracionEnSegundos);
                    rutaRef.child("posicionFinal").setValue("Latitud: " + latitude + ", Longitud: " + longitude);

                    LatLng finRutaLatLng = new LatLng(latitude, longitude);
                    googleMap.addMarker(new MarkerOptions().position(finRutaLatLng).title("Fin de Ruta"));

                    // Guardar la distancia total en metros directamente
                    rutaRef.child("distanciaTotal").setValue(distanciaTotal);

                    // Mostrar la distancia en kilómetros en el TextView
                    textViewDistancia.setText(String.format("%.2f km", distanciaTotal / 1000));

                    Toast.makeText(this, "Ruta finalizada", Toast.LENGTH_SHORT).show();
                    rutaIniciada = false;
                } else {
                    Toast.makeText(this, "Error al obtener la clave de la ruta", Toast.LENGTH_SHORT).show();
                }
            } else {
                textViewPosicionFinal.setText("Posición final no disponible");
            }
        } else {
            Toast.makeText(this, "La ruta no ha sido iniciada", Toast.LENGTH_SHORT).show();
        }
    }



    private Location obtenerPosicionActual() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        return null;
    }

    private String obtenerFechaActual() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy  -  HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private boolean isGPSEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        LatLng ovalle = new LatLng(-30.5987, -71.2007);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ovalle, 13));
    }

    private double calcularDistancia(LatLng punto1, LatLng punto2) {
        Location location1 = new Location("Punto 1");
        location1.setLatitude(punto1.latitude);
        location1.setLongitude(punto1.longitude);

        Location location2 = new Location("Punto 2");
        location2.setLatitude(punto2.latitude);
        location2.setLongitude(punto2.longitude);

        return location1.distanceTo(location2);
    }
}
