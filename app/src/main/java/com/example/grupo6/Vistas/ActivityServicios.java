package com.example.grupo6.Vistas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.grupo6.Adapter.ListAdapterDetalle;
import com.example.grupo6.Adapter.ListAdapterLocales;
import com.example.grupo6.Adapter.ListAdapterServicios;
import com.example.grupo6.Config.Locales;
import com.example.grupo6.Config.Servicios;
import com.example.grupo6.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ActivityServicios extends AppCompatActivity {

    Toolbar toolbarServicios;
    ListAdapterServicios listAdapter;
    List<Servicios> ListServicios;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicios);

        // Inicializa Firestore
        db = FirebaseFirestore.getInstance();

        //---------CASTING-----------
        toolbarServicios = (Toolbar) findViewById(R.id.toolbarServicios);

        setSupportActionBar(toolbarServicios);

        //---------HABILITAR FLECHA DE RETROCESO-----------
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        llenarLista();
    }

    private void llenarLista() {
//        List<Servicios> detalleOrdens = new ArrayList<>();
//        detalleOrdens.add(new Servicios("#2345", "Lavado sencillo", "Cantidad: 1", "Precio: 100 LPS"));
//        detalleOrdens.add(new Servicios("#2345", "Lavado sencillo", "Cantidad: 1", "Precio: 100 LPS"));
//        detalleOrdens.add(new Servicios("#2345", "Lavado sencillo", "Cantidad: 1", "Precio: 100 LPS"));
        obtainDataFromFirestore();

        //listAdapter=new ListAdapterServicios(detalleOrdens,this);
        listAdapter=new ListAdapterServicios(ListServicios,this);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewServicios);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listAdapter);
    }

    private void obtainDataFromFirestore() {
        ListServicios = new ArrayList<>();
        db.collection("servicios")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                // se obtienen los los datos y agregar a la lista
                                String nombre = document.getString("nombre");
                                String precio = document.getString("precio");
                                ListServicios.add(new Servicios(nombre,precio));
                            }
                            // Notifica al adaptador que los datos han cambiado
                            listAdapter.notifyDataSetChanged();
                        } else {
                            Log.d("Error obteniendo informacion", "");
                        }
                    }
                });
    }
}