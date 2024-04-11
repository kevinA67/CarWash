package com.example.grupo6.Vistas;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.grupo6.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

public class ActivityMapa extends AppCompatActivity implements OnMapReadyCallback {
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    double latitudOrigen, longitudOrigen;
    String latitudDestino, longitudDestino;
    private LatLng origin;
    private LatLng destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtener el fragmento del mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);

        // Recibe geolocalización del local
        Intent intent = getIntent();
        latitudDestino = intent.getStringExtra("latitud");
        longitudDestino = intent.getStringExtra("longitud");

        // Solicitar permiso de ubicación si no está concedido
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permiso concedido, obtener la ubicación
            obtainLocation();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
    }

    private void obtainLocation() {
        // Obtener la última ubicación conocida
        fusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // Ubicación obtenida con éxito
                        Location lastLocation = task.getResult();
                        latitudOrigen = lastLocation.getLatitude();
                        longitudOrigen = lastLocation.getLongitude();

                        // Establecer los puntos de origen y destino
                        origin = new LatLng(latitudOrigen, longitudOrigen);
                        destination = new LatLng(Double.parseDouble(latitudDestino), Double.parseDouble(longitudDestino));

                        // Mostrar la ubicación en el mapa
                        mMap.addMarker(new MarkerOptions().position(origin).title("Origen"));
                        mMap.addMarker(new MarkerOptions().position(destination).title("Destino"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 15));

                        // Obtener la distancia y el tiempo de viaje
                        getDirections(origin, destination);
                    } else {
                        Toast.makeText(getApplicationContext(), "No es posible obtener la ubicación",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void getDirections(LatLng origin, LatLng destination) {
        GeoApiContext geoApiContext = new GeoApiContext.Builder()
                .apiKey("AIzaSyDL4TIXTTaIG5VU0ncRT9Kcx4oDiVtoNSc")
                .build();

        DirectionsApi.newRequest(geoApiContext)
                .origin(new com.google.maps.model.LatLng(origin.latitude, origin.longitude))
                .destination(new com.google.maps.model.LatLng(destination.latitude, destination.longitude))
                .mode(TravelMode.DRIVING)
                .setCallback(new PendingResult.Callback<DirectionsResult>() {
                    @Override
                    public void onResult(DirectionsResult result) {
                        // Parsear la respuesta para obtener la distancia y el tiempo de viaje
                        if (result.routes != null && result.routes.length > 0) {
                            String distance = result.routes[0].legs[0].distance.humanReadable;
                            String duration = result.routes[0].legs[0].duration.humanReadable;

                            // Mostrar la distancia y el tiempo de viaje
                            Log.d("Directions", "Distancia: " + distance);
                            Log.d("Directions", "Tiempo de viaje: " + duration);
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        // Manejar el caso de fallo
                        Log.e("Directions", "Error al obtener direcciones: " + e.getMessage());
                    }
                });
    }
}
