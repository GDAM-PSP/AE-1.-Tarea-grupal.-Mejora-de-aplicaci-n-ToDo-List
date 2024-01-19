package com.example.todolist;

import static com.example.todolist.R.raw.audio_error;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
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

public class Login extends AppCompatActivity {
    Button botonLogin;
    TextView botonRegistro;
    private FirebaseAuth mAuth;
    EditText emailText, passText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        emailText = findViewById(R.id.cajaCorreo);
        passText = findViewById(R.id.cajaContrasena);


        botonRegistro = findViewById(R.id.cuentaNueva);
        botonLogin = findViewById(R.id.login);





        botonRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CREAR USUARIO EN FIREBASE
                String email = emailText.getText().toString();
                String password = passText.getText().toString();
                if(password.length()<6) {
                    MediaPlayer mediaPlayer = MediaPlayer.create(Login.this, R.raw.audio_error);
                    mediaPlayer.start();
                    Toast.makeText(Login.this, "La contraseña no puede contener menos de 6 caracteres", Toast.LENGTH_SHORT).show();
                } else if (email.trim().isEmpty() || password.trim().isEmpty()) {
                        MediaPlayer mediaPlayer = MediaPlayer.create(Login.this, R.raw.audio_error);
                        mediaPlayer.start();
                    Toast.makeText(Login.this, "Ningun campo puede estar vacio", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {
                                        //Sign in success, update UI with the signed-in user's information
                                            MediaPlayer mediaPlayer = MediaPlayer.create(Login.this, R.raw.start);
                                            mediaPlayer.start();
                                        Toast.makeText(Login.this, "Usuario registrado", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Login.this, MainActivity.class);
                                        startActivity(intent);
                                    } else {
                                        //If sign in fails, display a message to the user.
                                            MediaPlayer mediaPlayer = MediaPlayer.create(Login.this, audio_error);
                                            mediaPlayer.start();
                                        Toast.makeText(Login.this, "La autentificación a fallado", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    }

                }
            });

        botonLogin.setOnClickListener(view -> {
            //LOGUEAR USUARIO EN FIREBASE
            String email = emailText.getText().toString();
            String password = passText.getText().toString();
            if(email.trim().isEmpty() || password.trim().isEmpty()){
                MediaPlayer mediaPlayer = MediaPlayer.create(Login.this, R.raw.audio_error);
                mediaPlayer.start();
                Toast.makeText(Login.this, "Ningun campo puede estar vacio",Toast.LENGTH_SHORT).show();
            }else {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    //Sign in success
                                        MediaPlayer mediaPlayer = MediaPlayer.create(Login.this, R.raw.start);
                                        mediaPlayer.start();
                                    Toast.makeText(Login.this, "Usuario logueado", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Login.this, MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    //If sign in fails, display a message to the user.
                                        MediaPlayer mediaPlayer = MediaPlayer.create(Login.this, R.raw.audio_error);
                                        mediaPlayer.start();
                                    Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

        });

    }

}