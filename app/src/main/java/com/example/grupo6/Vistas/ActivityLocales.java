package com.example.grupo6.Vistas;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grupo6.Adapter.ListAdapterLocales;
import com.example.grupo6.Config.Locales;
import com.example.grupo6.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ActivityLocales extends AppCompatActivity {

    Toolbar toolbarLocales;
    ListAdapterLocales listAdapter;
    List<Locales> ListLocales;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locales);

        // Inicializa Firestore
        db = FirebaseFirestore.getInstance();

        //---------CASTING-----------
        toolbarLocales = (Toolbar) findViewById(R.id.toolbarLocales);

        setSupportActionBar(toolbarLocales);

        //---------HABILITAR FLECHA DE RETROCESO-----------
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        llenarLista();
    }

    private void llenarLista() {
        //ListLocales = new ArrayList<>();
        //ListLocales.add(new Locales("Car Wash EL Catracho #1", "Ciudad: Tegucigalpa", "Dirección: Media cuadra antes del \nparque central.","0","-1"));
        //ListLocales.add(new Locales("Car Wash EL Catracho #2", "Ciudad: San Pedro Sula", "Dirección: Media cuadra antes del \nparque central.","2","-2"));
        obtainDataFromFirestore();
        listAdapter = new ListAdapterLocales(ListLocales, this, new ListAdapterLocales.OnItemDoubleClickListener() {
            @Override
            public void onItemDoubleClick(Locales locales) {
                alertaUbicacion(locales);
            }
        });
        RecyclerView recyclerView = findViewById(R.id.recyclerViewLocales);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listAdapter);
    }

    private void alertaUbicacion(Locales locales) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityLocales.this);
        builder.setTitle("Ubicación.");
        builder.setMessage("¿Desea ver la ubicación del local seleccionado?");

        // Agregar botón de buscar ubicacón
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int selectedItemIndex = ListAdapterLocales.getSelectedItem();
                if (selectedItemIndex != -1) {
                    Locales locales = ListLocales.get(selectedItemIndex);
                    Intent intent = new Intent(getApplicationContext(), ActivityMapa.class);
                    intent.putExtra("latitud", locales.getLatitud_gps());
                    intent.putExtra("longitud", locales.getLongitud_gps());
                    startActivity(intent);

                }
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Si el usuario cancela la eliminación, no hacer nada
            }
        });

        builder.show();
    }

    private void obtainDataFromFirestore() {
        ListLocales = new ArrayList<>();
        db.collection("locales")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                // Obtén los datos y agrega a la lista
                                String nombre = document.getString("nombre");
                                String ciudad = document.getString("ciudad");
                                String direccion = document.getString("direccion");
                                String latitud = document.getString("latitud");
                                String longitud = document.getString("longitud");
                                ListLocales.add(new Locales(nombre,ciudad,direccion,latitud,longitud));
                            }
                            // Notifica al adaptador que los datos han cambiado
                            listAdapter.notifyDataSetChanged();
                        } else {
                            Log.d("Error obteniendo ifnormacion", "..Distancia: ");
//                            Log.d("Error obteniendo información: ", task.getException());
                        }
                    }
                });
    }
}