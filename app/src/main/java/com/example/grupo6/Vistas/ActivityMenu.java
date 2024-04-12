package com.example.grupo6.Vistas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.grupo6.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class ActivityMenu extends AppCompatActivity {

    Toolbar toolbar;
    LinearLayout citas, ordenes, servicios, locales, vehiculos;
    ImageView perfil;
    ImageView cerrarSesion;
    TextView localesContador, serviciosContador, vehiculosContador;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Inicialización de FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        //---------CASTING-----------
        citas = (LinearLayout) findViewById(R.id.citas);
        ordenes = (LinearLayout) findViewById(R.id.ordenes);
        servicios= (LinearLayout) findViewById(R.id.servicios);
        locales= (LinearLayout) findViewById(R.id.locales);
        vehiculos= (LinearLayout) findViewById(R.id.vehiculos);
        perfil= (ImageView) findViewById(R.id.perfil);
        localesContador= (TextView) findViewById(R.id.localesN_txt);
        serviciosContador= (TextView) findViewById(R.id.serviciosN_txt);
        vehiculosContador= (TextView) findViewById(R.id.vehiculoN_txt);

        cerrarSesion = (ImageView) findViewById(R.id.cerrarSesion);

        CollectionReference contLocales= FirebaseFirestore.getInstance().collection("locales");
        contLocales.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                int localesCont=task.getResult().size();
                localesContador.setText(String.valueOf(localesCont));
            }
        });

        CollectionReference contServicios= FirebaseFirestore.getInstance().collection("servicios");
        contServicios.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                int serviciosCont=task.getResult().size();
                serviciosContador.setText(String.valueOf(serviciosCont));
            }
        });

//        CollectionReference contVehiculos= FirebaseFirestore.getInstance().collection("vehiculos");
//        FirebaseUser user = mAuth.getCurrentUser();
//        String userId = user.getUid();
//        contVehiculos.whereEqualTo("userId",userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                int vehiculosCont=task.getResult().size();
//                vehiculosContador.setText(String.valueOf(vehiculosCont));
//            }
//        });

        citas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityCitas.class);
                startActivity(intent);
            }
        });

        ordenes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityOrdenes.class);
                startActivity(intent);
            }
        });

        servicios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityServicios.class);
                startActivity(intent);
            }
        });

        locales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityLocales.class);
                startActivity(intent);
            }
        });

        vehiculos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityVehiculos.class);
                startActivity(intent);
            }
        });
        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityPerfil.class);
                startActivity(intent);
            }
        });
        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarSesion();
            }
        });
    }

    private void cerrarSesion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityMenu.this);
        builder.setTitle("Cerrar Sesión ");
        builder.setMessage("¿Deseas cerrar la sesión?");

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), ActivityInicio.class);
                startActivity(intent);
                finish();
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Si el usuario cancela, no hacer nada
            }
        });

        builder.show();
    }
}