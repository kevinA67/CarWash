package com.example.grupo6.Vistas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.grupo6.MainActivity;
import com.example.grupo6.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ActivityLogIn_SignUp extends AppCompatActivity {

    ToggleButton switchViewLog, switchViewSign;
    final int heightInicial = 1086, heightFinal = 1500;
    EditText txtNombre, txtCelular, txtCorreo, txtContra, txtConfirContra, txtNombreUsuario, txtPassWord;
    Button btnRegistrate, btnIniciarSesion;
    CardView cardView;
    TextView txtRecuperar;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseFirestore db;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        // Initialize Firebase Auth/Users
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        //---------CASTING-----------

        cardView = (CardView) findViewById(R.id.cardView);

        //---------INFLAR EL DISEÑO DE INICIO DE SESIÓN Y DE REGISTRO-----------

        LayoutInflater inflater = LayoutInflater.from(ActivityLogIn_SignUp.this);
        View loginLayout = inflater.inflate(R.layout.log_in_design, null);
        View signUpLayout = inflater.inflate(R.layout.sign_up_design, null);
        switchViewLog = loginLayout.findViewById(R.id.switchLog);
        switchViewSign = signUpLayout.findViewById(R.id.switchSign);

        //---------IDENTIFICANDO SI LLAMARON A INIAR SESIÓN O REGISTRARSE-----------

        if ("1".equals(getIntent().getStringExtra("logIn"))) {
            establecerDisenio(cardView, loginLayout, "log");
        } else if ("1".equals(getIntent().getStringExtra("signUp"))) {
            ViewGroup.LayoutParams layoutParams = cardView.getLayoutParams();
            layoutParams.height = heightFinal;
            cardView.setLayoutParams(layoutParams);
            establecerDisenio(cardView, signUpLayout, "sign");
        }

        //---------CAMBIO DE DISEÑO-----------

        switchViewLog.setOnCheckedChangeListener(cambioDeSwitch(heightInicial, heightFinal, signUpLayout, "sign"));
        switchViewSign.setOnCheckedChangeListener(cambioDeSwitch(heightFinal, heightInicial, loginLayout, "log"));


    }

    private CompoundButton.OnCheckedChangeListener cambioDeSwitch(int heightInicial, int heightFinal, View layout, String disenio) {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton botonSwitch, boolean isChecked) {
                if (isChecked) {
                    botonSwitch.setChecked(false);

                    ValueAnimator animator = ValueAnimator.ofInt(heightInicial, heightFinal);
                    animator.setDuration(500);

                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            int animacionValue = (int) valueAnimator.getAnimatedValue();
                            ViewGroup.LayoutParams layoutParams = cardView.getLayoutParams();
                            layoutParams.height = animacionValue;
                            cardView.setLayoutParams(layoutParams);
                        }
                    });
                    animator.start();

                    establecerDisenio(cardView, layout, disenio);
                }
            }
        };
    }

    private void establecerDisenio(CardView cardView, View view, String disenio) {

        cardView.removeAllViews();
        cardView.addView(view);

        //************************************************ REGISTRARSE ************************************************************
        if (disenio.equals("sign")) {

            //---------CASTING-----------

            txtNombre = (EditText) findViewById(R.id.txtNombre);
            txtCelular = (EditText) findViewById(R.id.txtCelular);
            txtCorreo = (EditText) findViewById(R.id.txtCorreoRegistro);
            txtContra = (EditText) findViewById(R.id.txtPasswordRegistrate);
            txtConfirContra = (EditText) findViewById(R.id.txtConfPassword);
            btnRegistrate = (Button) findViewById(R.id.btnRegistrate);

            animacionDesvanecer(findViewById(R.id.btnRegistrate));

            btnRegistrate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //---------OBTENER DATOS-----------
                    String username = txtNombre.getText().toString().trim();
                    String celular = txtCelular.getText().toString().trim();
                    String correo = txtCorreo.getText().toString().trim();
                    String contra = txtContra.getText().toString().trim();
                    String confirContra = txtConfirContra.getText().toString().trim();


                    //---------VALIDACIONES-----------

                    if (username.isEmpty()) {
                        txtNombre.setError("Campo obligatorio");
                    } else if (celular.isEmpty()) {
                        txtCelular.setError("Campo obligatorio");
                    } else if (correo.isEmpty()) {
                        txtCorreo.setError("Campo obligatorio");
                    } else if (contra.isEmpty() || confirContra.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Favor establecer la contraseña.", Toast.LENGTH_SHORT).show();
                    } else if (celular.length() != 8) {
                        Toast.makeText(getApplicationContext(), "El campo celular debe contener 8 dígitos.", Toast.LENGTH_SHORT).show();
                    } else {
                        if (contra.equals(confirContra)) {
                            if (contra.length() < 8) {
                                Toast.makeText(getApplicationContext(), "La contraseña debe de ser de al menos 8 dígitos.", Toast.LENGTH_SHORT).show();
                            } else {
                                crearUsuario(correo, contra);
                                //crearUsuarioFirestore();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show();
                        }
                    }


                }
            });


            //************************************************INICIO DE SESIÓN************************************************************
        } else {

            //---------ANIMACIÓN-----------

            animacionDesvanecer(findViewById(R.id.btnIniciarSesion));
            animacionDesvanecer(findViewById(R.id.txtViewOlvidoContra));

            //---------CASTING-----------

            btnIniciarSesion = (Button) findViewById(R.id.btnIniciarSesion);
            txtNombreUsuario = (EditText) findViewById(R.id.txtCorreo);
            txtPassWord = (EditText) findViewById(R.id.txtPassword);
            txtRecuperar = (TextView) findViewById(R.id.txtViewOlvidoContra);

            btnIniciarSesion.setOnClickListener(View -> {
                acceder();
            });

            txtRecuperar.setOnClickListener(View -> {
                Intent intent = new Intent(getApplicationContext(), ActivityRecuperarContra.class);
                startActivity(intent);
            });
        }
    }

    private void animacionDesvanecer(View view) {
        AlphaAnimation fadeInAnimation = new AlphaAnimation(0.0f, 1.0f);
        fadeInAnimation.setDuration(1000);
        view.startAnimation(fadeInAnimation);
    }


    private void acceder() {
        if (validar() != false) {
            mAuth.signInWithEmailAndPassword(txtNombreUsuario.getText().toString(), txtPassWord.getText().toString())
                    .addOnCompleteListener(ActivityLogIn_SignUp.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //Inicia sesion
                                FirebaseUser user = mAuth.getCurrentUser();

                                if (user != null && user.isEmailVerified()) {
                                    Intent intent = new Intent(getApplicationContext(), ActivityMenu.class);
                                    startActivity(intent);
                                } else {
                                    // El correo electrónico no está verificado
                                    Toast.makeText(ActivityLogIn_SignUp.this, "Por favor, verifica tu correo electrónico", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // El inicio de sesión falló, manejar el error aquí
                                Toast.makeText(ActivityLogIn_SignUp.this, "Correo o contraseña no valido", Toast.LENGTH_SHORT).show();
                                Log.e("LoginError", task.getException().getMessage());
                            }
                        }
                    });
        }
    }

    private boolean validar() {
        boolean valor = false;

        String pass = txtPassWord.getText().toString().replaceAll("\\s", "");
        String correo = txtNombreUsuario.getText().toString().replaceAll("\\s", "");

        if (correo.isEmpty() && pass.isEmpty()) {
            Toast.makeText(getApplicationContext(), "LLene todos los campos", Toast.LENGTH_LONG).show();
        } else if (correo.isEmpty()) {
            txtNombreUsuario.setError("Debe llenar este campo");
        } else if (pass.isEmpty()) {
            txtPassWord.setError("Debe llenar este campo");
        } else {
            valor = true;
        }
        return valor;
    }


    private void crearUsuario(String correo, String contra) {
        mAuth.createUserWithEmailAndPassword(correo, contra)
                .addOnCompleteListener(ActivityLogIn_SignUp.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Registro exitoso, obtener el token de autenticación
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                user.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> emailVerificationTask) {
                                                if (emailVerificationTask.isSuccessful()) {
                                                    // Correo de verificación enviado exitosamente
                                                    Toast.makeText(ActivityLogIn_SignUp.this, "Correo de verificación enviado. Por favor, verifica tu correo electrónico para iniciar sesión.", Toast.LENGTH_LONG).show();
                                                    mAuth.signOut(); // Cerrar sesión para que el usuario verifique su correo electrónico antes de iniciar sesión.
                                                    //crearUsuarioFirestore();
                                                } else {
                                                    // Error al enviar el correo de verificación
                                                    Log.e("ActivityLogIn_SignUp", "sendEmailVerification", emailVerificationTask.getException());
                                                    Toast.makeText(ActivityLogIn_SignUp.this, "Error al enviar correo de verificación. Por favor, inténtalo nuevamente.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } else {
                                // Error al obtener el usuario actual
                                Log.e("ActivityLogIn_SignUp", "getCurrentUser:failure");
                                Toast.makeText(ActivityLogIn_SignUp.this, "Error al registrar cuenta. Por favor, inténtalo nuevamente.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Registro fallido
                            Log.w("ActivityLogIn_SignUp", "createUserWithEmailAndPassword:failure", task.getException());
                            Toast.makeText(ActivityLogIn_SignUp.this, "Autenticación fallida.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void crearUsuarioFirestore() {
        //String userID = mAuth.getCurrentUser().getUid();

        // Crear un objeto Map para almacenar los datos que deseas guardar en Firestore
        Map<String, Object> userData = new HashMap<>();
        userData.put("nombres", txtNombre.getText().toString().trim());
        userData.put("correo", txtCorreo.getText().toString().trim());
        userData.put("celular", txtCelular.getText().toString().trim());

        // Puedes agregar más campos según sea necesario

        // Agregar los datos del usuario a Firestore
        db.collection("clientes")
                .add(userData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // El documento ha sido creado exitosamente
                        //Toast.makeText(getApplicationContext(), "Persona creada exitosamente", Toast.LENGTH_SHORT).show();
                        // Puedes agregar aquí cualquier otra lógica que necesites después de crear la persona
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Ocurrió un error al intentar crear el documento
                        Toast.makeText(getApplicationContext(), "Error al crear persona: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        // Puedes manejar el error de acuerdo a tus necesidades
                    }
                });
    }
}
