package com.example.seguimientoderutas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private FirebaseAuth auth;
    private TextView btnRegistrar;
    private EditText txtCorreo, txtContrasenia;
    private Button btnIniciarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        txtCorreo = findViewById(R.id.txtCorreo);
        txtContrasenia = findViewById(R.id.txtContrasenia);
        btnRegistrar = findViewById(R.id.BotonRegistrar);
        btnIniciarSesion = findViewById(R.id.btnIniciar);

        Button botonIniciarSesion = findViewById(R.id.btnIniciar);

        btnIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String correo = txtCorreo.getText().toString().trim();
                String contrasenia = txtContrasenia.getText().toString().trim();

                // Verifica que los campos no estén vacíos antes de intentar iniciar sesión
                if (correo.isEmpty() || contrasenia.isEmpty()) {
                    Toast.makeText(Login.this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Metodo para agregar usuarios
                auth.signInWithEmailAndPassword(correo, contrasenia)
                        .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser correo = auth.getCurrentUser();
                                    Toast.makeText(Login.this, "Bienvenido, " + correo.getEmail(), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Login.this, Home.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(Login.this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, RegistrarUsuario.class);
                startActivity(intent);
            }
        });

        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            // Si el usuario está autenticado, cargar sus datos
            startActivity(new Intent(Login.this, Home.class));
        }
    }
}