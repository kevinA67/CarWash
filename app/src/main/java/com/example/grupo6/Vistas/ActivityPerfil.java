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
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ActivityPerfil extends AppCompatActivity {

    Toolbar toolbarPerfil;
    EditText txtNombre, txtCorreo, txtCelular, txtContra1, txtContra2;
    Switch cambiarPass;
    Button tomarFoto;
    File foto, foto2;
    String currentPhotoPath;

    static final int REQUEST_IMAGE = 101;
    static final int ACCESS_CAMERA = 201;

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
        tomarFoto = findViewById(R.id.btn_Foto);

        setSupportActionBar(toolbarPerfil);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        tomarFoto.setOnClickListener(View ->{
            PermisosCamara();
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

    private void PermisosCamara() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, ACCESS_CAMERA);
        } else {
            dispatchTakePictureIntent();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.toString();
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE);
            }
        }
    }

    private File createImageFile() throws IOException {

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();

        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACCESS_CAMERA) {
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
        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK && data != null) {
            // Obtener la imagen capturada como un bitmap
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

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