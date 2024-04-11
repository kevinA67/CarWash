package com.example.grupo6.Vistas;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.grupo6.Adapter.ListAdapterServiciosCita;
import com.example.grupo6.Config.Servicios;
import com.example.grupo6.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ActivityCitas extends AppCompatActivity {

    Toolbar toolbarCitas;
    private EditText editTextDate;
    private EditText editTextTime;
    private EditText editTextAddress;
    private Switch switchServicioDomicilio;
    private FloatingActionButton newVehiculo;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Spinner spinnerVehiculos, spinnerCentros;
    private boolean servicioDomicilio;

    ListAdapterServiciosCita listAdapter;
    List<Servicios> servicios;

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citas);

        //---------CASTING-----------
        toolbarCitas = (Toolbar) findViewById(R.id.toolbarCitas);
        editTextDate = findViewById(R.id.editTextDate);
        editTextTime = findViewById(R.id.editTextTime);
        editTextAddress = findViewById(R.id.edxAddress);
        newVehiculo = (FloatingActionButton) findViewById(R.id.floatAddVehiculo);

        switchServicioDomicilio = findViewById(R.id.switch1);
        TextView textViewCentro = findViewById(R.id.textView3);
        spinnerCentros = findViewById(R.id.spinnerCentros);
        spinnerVehiculos = findViewById(R.id.spinnerVehiculos);

        // Obtener referencia al botón save_button del toolbarCitas
        ImageButton saveButton = findViewById(R.id.save_button);



        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Inicializar Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Obtener el usuario actualmente logueado
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            // Obtener el identificador único del usuario
            String userId = user.getUid();


            // Obtener los datos del usuario desde Firestore y llenar el Spinner
            obtenerDatosParaSpinner(userId);
            obtenerDatosParaSpinnerCentros();
        } else {
            // El usuario no está autenticado, puedes manejarlo según tus necesidades
        }



        // Estaablecer un estado al Switch de servicio en falso
        switchServicioDomicilio.setChecked(false);
        textViewCentro.setVisibility(View.VISIBLE);
        spinnerCentros.setVisibility(View.VISIBLE);

        //estado de la direccion en caso de que el switch esta apagado
        editTextAddress.setVisibility(View.INVISIBLE);


        switchServicioDomicilio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    // Si el switch está activado, no mostrar el TextView y el Spinner
                    textViewCentro.setVisibility(View.VISIBLE);
                    textViewCentro.setText("Dirección");
                    spinnerCentros.setVisibility(View.INVISIBLE);
                    editTextAddress.setVisibility(View.VISIBLE);
                } else {
                    // Si el switch está desactivado, cambiar el texto de el TextView3 y el EditText
                    textViewCentro.setText("Centro de atención");
                    spinnerCentros.setVisibility(View.VISIBLE);
                    editTextAddress.setVisibility(View.INVISIBLE);

                }
            }
        });


        setSupportActionBar(toolbarCitas);

        //---------HABILITAR FLECHA DE RETROCESO-----------
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Configurar el listener para abrir el DatePickerDialog
        editTextDate.setOnClickListener(view -> showDatePickerDialog());
        editTextTime.setOnClickListener(view -> showTimePickerDialog());


        //Método para ir a agregar un vehículo
        newVehiculo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityCitas.this, ActivityVehiculos.class);
                startActivity(intent);

            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Verificar si los campos obligatorios están llenos y se ha seleccionado un vehículo
                if (editTextDate.getText().toString().isEmpty() ||
                        editTextTime.getText().toString().isEmpty() ||
                        spinnerVehiculos.getSelectedItemPosition() == 0) {
                    // Mostrar un mensaje de error si algún campo está vacío o no se ha seleccionado un vehículo
                    Toast.makeText(ActivityCitas.this, "Por favor completa todos los campos obligatorios", Toast.LENGTH_SHORT).show();
                } else {
                    // Todos los campos obligatorios están llenos y se ha seleccionado un vehículo, almacenar los datos
                    almacenarDatos();
                }
            }
        });

        llenarLista();


    }



    //-------METHODS----------

    private void llenarLista() {
        List<Servicios> detalleOrdens = new ArrayList<>();
        detalleOrdens.add(new Servicios("#2345", "Lavado sencillo", "Cantidad: 1", "Precio: 100 LPS"));
        detalleOrdens.add(new Servicios("#2345", "Lavado sencillo", "Cantidad: 1", "Precio: 100 LPS"));

        listAdapter=new ListAdapterServiciosCita(detalleOrdens,this);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewCitas);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listAdapter);
    }


    // Método para mostrar el selector de fecha
    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.DialogTheme,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editTextDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    //Método para mostrar el selector de hora
    private void showTimePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, R.style.DialogTheme,
                (view, hourOfDay, minute) -> {
                    //
                    String selectedTime = String.format("%02d:%02d", hourOfDay, minute);
                    editTextTime.setText(selectedTime);
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }
    //Fin de metodo de hora


    //Inicio metodo para datos del spinner

    private void obtenerDatosParaSpinner(String userId) {
        // Obtener los datos del usuario desde Firestore
        db.collection("vehiculos").document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            // Obtener los detalles del vehículo
                            String marca = document.getString("marca");
                            String modelo = document.getString("modelo");
                            String placa = document.getString("placa");

                            // Crear un objeto con los detalles del vehículo
                            String detalleVehiculo = marca + " " + modelo + " " + placa;

                            //Parametro inicial
                            List<String> detalles = new ArrayList<>();
                            detalles.add("Seleccione un vehículo");

                            // Agregar el detalle del vehículo al Spinner
                            detalles.add(detalleVehiculo);
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(ActivityCitas.this, android.R.layout.simple_spinner_item, detalles);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerVehiculos.setAdapter(adapter);
                        }
                        else {
                            Toast.makeText(ActivityCitas.this, "No se encontraron detalles para el vehículo", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void obtenerDatosParaSpinnerCentros() {

        db.collection("locales")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> locales = new ArrayList<>();
                            locales.add("Seleccione un local");

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Obtener el nombre del local y añadirlo a la lista
                                String nombreLocal = document.getString("nombre");
                                locales.add(nombreLocal);
                            }

                            // Crear un adaptador para el Spinner
                            ArrayAdapter<String> adapterLocal = new ArrayAdapter<>(ActivityCitas.this, android.R.layout.simple_spinner_item, locales);
                            adapterLocal.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            // Asignar el adaptador al Spinner
                            spinnerCentros.setAdapter(adapterLocal);
                        } else {
                            Toast.makeText(ActivityCitas.this, "Error al obtener los locales.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //Metodo para almacenar datos
    private void almacenarDatos() {
        // Obtener los valores de la fecha, hora y dirección desde los EditText
        String fecha = editTextDate.getText().toString();
        String hora = editTextTime.getText().toString();
        String direccion = editTextAddress.getText().toString();

        // Obtener el vehículo seleccionado del Spinner
        String vehiculoSeleccionado = spinnerVehiculos.getSelectedItem().toString();

        // Obtener el centro de servicio seleccionado del Spinner
        String centroServicioSeleccionado = spinnerCentros.getSelectedItem().toString();

        // Verificar si el switch de servicio a domicilio está activado
        boolean servicioDomicilio = switchServicioDomicilio.isChecked();

        // Si el switch está activado, utilizar la dirección como centro de servicio
        if (servicioDomicilio) {
            centroServicioSeleccionado = direccion;
        }

        // Obtener el ID del usuario actual
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Obtener una referencia a la colección "citas" en Firestore
        CollectionReference citasRef = FirebaseFirestore.getInstance().collection("citas");

        // Crear un nuevo documento con un ID único generado automáticamente por Firestore
        String finalCentroServicioSeleccionado = centroServicioSeleccionado;
        citasRef.add(new HashMap<String, Object>() {{
                    put("userId", userId); // Guardar el ID del usuario
                    put("fecha", fecha);
                    put("hora", hora);
                    put("vehiculo", vehiculoSeleccionado);
                    put("centro_servicio", finalCentroServicioSeleccionado);
                }})
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Los datos se almacenaron correctamente
                        Toast.makeText(ActivityCitas.this, "Datos almacenados correctamente", Toast.LENGTH_SHORT).show();

                        // Limpiar los campos después de guardar los datos
                        editTextDate.setText("");
                        editTextTime.setText("");
                        editTextAddress.setText("");
                        spinnerVehiculos.setSelection(0);
                        spinnerCentros.setSelection(0);
                        switchServicioDomicilio.setChecked(false);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Se produjo un error al almacenar los datos
                        Toast.makeText(ActivityCitas.this, "Error al almacenar los datos", Toast.LENGTH_SHORT).show();
                    }
                });
    }



}//Fin de la clase

