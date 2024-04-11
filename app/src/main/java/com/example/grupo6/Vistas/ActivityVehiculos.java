package com.example.grupo6.Vistas;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.grupo6.Adapter.ListAdapterOrdenes;
import com.example.grupo6.Adapter.ListAdapterVehiculo;
import com.example.grupo6.Config.Locales;
import com.example.grupo6.Config.Vehiculo;
import com.example.grupo6.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.Exception;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;


public class ActivityVehiculos extends AppCompatActivity {

    EditText txt_marca, txt_modelo, txt_anio, txt_color, txt_combustible, txt_placa;
    Button btn_Agregar;
    ImageView  eliminar;
    FirebaseFirestore db;

    FirebaseAuth mAuth;
    String userId;

    private ListAdapterVehiculo listAdapter;
    private List<Vehiculo> listVehiculos;
    Toolbar toolbarVehiculos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehiculos);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();


        //---------CASTING-----------
        toolbarVehiculos = (Toolbar) findViewById(R.id.toolbarVehiculos);
        setSupportActionBar(toolbarVehiculos);

        //---------HABILITAR FLECHA DE RETROCESO-----------
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //----------------MOSTRAR DATOS--------------------------------------
        if (user != null) {
            // Obtener el identificador único del usuario
            String userId = user.getUid();

            // Obtener los datos del usuario desde Firestore y llenar el Spinner
            obtenerDatosParaSpinner(userId);
        } else {
            // El usuario no está autenticado, puedes manejarlo según tus necesidades
        }

        // Obtener los datos del usuario desde Firestore y llenar el Spinner
        obtenerDatosParaSpinner(userId);


        //------------------GARGAR DATOS-------------------------------------


        if (user != null) {
            userId = user.getUid();
        }
        //View view = LayoutInflater.from(this).inflate(R.layout.disenio_vehiculo_vista, null);
        //eliminar = view.findViewById(R.id.eliminarVehiculo);
        txt_marca = findViewById(R.id.txt_marca);
        txt_modelo = findViewById(R.id.txt_modelo);
        txt_anio = findViewById(R.id.txt_anio);
        txt_color = findViewById(R.id.txt_color);
        txt_combustible = findViewById(R.id.txt_combustible);
        txt_placa = findViewById(R.id.txt_placa);
        btn_Agregar = findViewById(R.id.btn_Agregar);
        /*eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

            }
        });*/
        btn_Agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String marca = txt_marca.getText().toString();
                String modelo = txt_modelo.getText().toString();
                String anio = txt_anio.getText().toString();
                String color = txt_color.getText().toString();
                String combustible = txt_combustible.getText().toString();
                String placa = txt_placa.getText().toString();

                Vehiculo vehiculo = new Vehiculo(marca, modelo, anio, color, combustible, placa, userId);

                db.collection("vehiculos")
                        .add(vehiculo)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(ActivityVehiculos.this, "Vehículo agregado correctamente", Toast.LENGTH_SHORT).show();
                                limpiarCampos();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ActivityVehiculos.this, "Error al agregar el vehículo", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void limpiarCampos() {
        // Limpiar los EditText
        ((EditText) findViewById(R.id.txt_marca)).setText("");
        ((EditText) findViewById(R.id.txt_modelo)).setText("");
        ((EditText) findViewById(R.id.txt_anio)).setText("");
        ((EditText) findViewById(R.id.txt_color)).setText("");
        ((EditText) findViewById(R.id.txt_combustible)).setText("");
        ((EditText) findViewById(R.id.txt_placa)).setText("");
    }

    //---------------CONSULTA DE DATOS-----------------------------------------


    private void obtenerDatosParaSpinner(String userId) {
        // Obtener los datos del usuario desde Firestore
        db.collection("vehiculos")
                .whereEqualTo("userId", userId) // Filtrar por el ID de usuario
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        listVehiculos = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            try {
                                Vehiculo vehiculo =new Vehiculo();
                                vehiculo.setMarca(documentSnapshot.get("marca").toString());
                                vehiculo.setModelo(documentSnapshot.get("modelo").toString());
                                vehiculo.setAnio(documentSnapshot.get("anio").toString());
                                vehiculo.setColor(documentSnapshot.get("color").toString());
                                vehiculo.setCombustible(documentSnapshot.get("combustible").toString());
                                vehiculo.setPlaca(documentSnapshot.get("placa").toString());

                                listVehiculos.add(vehiculo);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        llenarLista();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Fallo", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void llenarLista() {
        listAdapter = new ListAdapterVehiculo(getApplicationContext(), listVehiculos);
        RecyclerView recyclerView = findViewById(R.id.recyclerVehiculos);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listAdapter);
    }

    //-------------------ELIMINAR---------------------------------------
/*
    private void alertaVehiculo(Vehiculo vehiculo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityVehiculos.this);
        builder.setTitle("Eliminar Vehiculo");
        builder.setMessage("¿Desea eliminar Vehiculo?");

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int selectedItemIndex = ListAdapterVehiculo.getSelectedItem();
                if (selectedItemIndex != -1) {
                    Vehiculo vehiculo = listVehiculos.get(selectedItemIndex);
                    llenarLista();
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
*/
}