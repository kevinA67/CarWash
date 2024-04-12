package com.example.grupo6.Vistas;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.grupo6.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class ActivityPerfil extends AppCompatActivity {

    Toolbar toolbarPerfil;
    EditText txtNombre, txtCorreo, txtCelular, txtContra1, txtContra2;
    Switch cambiarPass;

    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private static final int REQUEST_IMAGE_CAPTURE = 201;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        toolbarPerfil = findViewById(R.id.toolbarPerfil);
        txtNombre = findViewById(R.id.txt_nombre);
        txtCorreo = findViewById(R.id.txt_correo);
        txtCelular = findViewById(R.id.txt_celular);
        txtContra1 = findViewById(R.id.txt_contra1);
        txtContra2 = findViewById(R.id.txt_contra2);
        cambiarPass = findViewById(R.id.cambiarPass);

        setSupportActionBar(toolbarPerfil);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Button takePhotoButton = findViewById(R.id.btn_Foto);
        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(ActivityPerfil.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Si los permisos no están concedidos, solicitarlos al usuario
                    ActivityCompat.requestPermissions(ActivityPerfil.this,
                            new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                } else {
                    // Si los permisos están concedidos, abrir la cámara
                    dispatchTakePictureIntent();
                }
            }
        });

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DocumentReference docRef = FirebaseFirestore.getInstance().collection("clientes").document(uid);

            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        String nombre = documentSnapshot.getString("nombre");
                        String celular = documentSnapshot.getString("celular");

                        txtNombre.setText(nombre);
                        txtCelular.setText(celular);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "Error getting document", e);
                }
            });

            // Establecer el correo electrónico en el EditText correspondiente
            String correoUsuario = currentUser.getEmail();
            txtCorreo.setText(correoUsuario);
        }

        cambiarPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    txtContra1.setEnabled(true);
                    txtContra2.setEnabled(true);
                } else {
                    txtContra1.setEnabled(false);
                    txtContra2.setEnabled(false);
                }
            }
        });

        findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener los valores de los campos
                String nombre = txtNombre.getText().toString();
                String celular = txtCelular.getText().toString();
                String password1 = txtContra1.getText().toString();
                String password2 = txtContra2.getText().toString();

                // Validar que los campos obligatorios no estén vacíos
                if (nombre.isEmpty() || celular.isEmpty()) {
                    Toast.makeText(ActivityPerfil.this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validar que las contraseñas coincidan
                if (!password1.equals(password2)) {
                    Toast.makeText(ActivityPerfil.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Crear un objeto Map con los nuevos valores
                Map<String, Object> data = new HashMap<>();
                data.put("nombre", nombre);
                data.put("celular", celular);

                // Actualizar los datos en Firestore
                FirebaseFirestore.getInstance().collection("clientes").document(currentUser.getUid())
                        .set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Éxito al guardar los datos
                                Toast.makeText(ActivityPerfil.this, "Cambios guardados correctamente", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Error al guardar los datos
                                Log.e(TAG, "Error al guardar los datos en Firestore", e);
                                Toast.makeText(ActivityPerfil.this, "Error al guardar los cambios", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Permisos de cámara denegados", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            // Obtener la imagen capturada como un bitmap
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");

            // Convertir el bitmap a una cadena Base64
            String imageBase64 = bitmapToBase64(imageBitmap);

            // Aquí puedes guardar la imagen en Firebase Storage o hacer lo que necesites con ella
        }
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
}