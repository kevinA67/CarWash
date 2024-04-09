package com.example.grupo6.Vistas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.grupo6.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ActivityRecuperarContra extends AppCompatActivity {

    Button btnRecuperar;
    EditText txtCorreo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_contra);

        btnRecuperar = (Button) findViewById(R.id.btnRecuperar);
        txtCorreo = (EditText) findViewById(R.id.txtCorreoRecuperar);

        btnRecuperar.setOnClickListener(View -> {
            String correo = txtCorreo.getText().toString().trim();
            if (correo.isEmpty()) {
                txtCorreo.setError("Favor ingresar un correo.");
            } else {
                recuperarContraseña(correo);
            }
        });
    }

    private void recuperarContraseña(String correo) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(correo)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // El correo electrónico de restablecimiento se envió correctamente
                            Toast.makeText(ActivityRecuperarContra.this, "Se ha enviado un correo electrónico para restablecer tu contraseña.", Toast.LENGTH_SHORT).show();
                        } else {
                            // Error al enviar el correo electrónico de restablecimiento
                            Toast.makeText(ActivityRecuperarContra.this, "Error al enviar el correo electrónico para restablecer la contraseña. Verifica la dirección de correo electrónico.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}