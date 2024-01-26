package com.example.todolist;

import static com.example.todolist.R.raw.audio_error;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.PatternsCompat;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import android.graphics.Color;
import android.hardware.biometrics.BiometricManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity {
    Button botonLogin,botonHuella;
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


                    if (email.trim().isEmpty() || password.trim().isEmpty()) { //password.trim().isEmpty()) {
                        Toast.makeText(Login.this, "Ningun campo puede estar vacio", Toast.LENGTH_SHORT).show();
                    }
                    else if (!PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()) {
                        emailText.setError("Introduzca un email válido");
                        MediaPlayer mediaPlayer = MediaPlayer.create(Login.this, R.raw.audio_error);
                        mediaPlayer.start();
                    }
                    else if (passText.length() < 6) {
                            passText.setError("Mínimo 6 caracteres", null);
                            MediaPlayer mediaPlayer = MediaPlayer.create(Login.this, R.raw.audio_error);
                            mediaPlayer.start();


                    }
                    else if(passText.length()>5 && !password.matches(".*[!@$%^&*+=?-].*")){
                        passText.setError("Debe contener al menos 1 caracter especial",null);
                    }
                    else if(passText.length()>5 && !password.matches(".*[a-z].*")){
                        passText.setError("Debe contener al menos 1 letra minúscula",null);
                    }
                    else if(passText.length()>5 && !password.matches(".*[A-Z].*")){
                        passText.setError("Debe contener al menos 1 letra mayúscula",null);
                    }
                    else if(passText.length()>5 && !password.matches(".*\\d.*")){
                        passText.setError("Debe contener al menos 1 número",null);
                    }
                    else if(passText.length()>5 && password.matches(".*\\s.*")){
                        passText.setError("No puede contener espacios en blanco",null);
                    }
                    else {
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
                                            Toast.makeText(Login.this, "No se ha podido crear el usuario", Toast.LENGTH_SHORT).show();

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
            if(email.trim().isEmpty() || password.trim().isEmpty()) {
                Toast.makeText(Login.this, "Ningun campo puede estar vacio", Toast.LENGTH_SHORT).show();
            }
            else if(!PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()){
                    emailText.setError("Introduzca un email válido");
                    MediaPlayer mediaPlayer = MediaPlayer.create(Login.this, R.raw.audio_error);
                    mediaPlayer.start();

            }
            else if(passText.length() <6) {
                    passText.setError("Mínimo 6 caracteres",null);

                    MediaPlayer mediaPlayer = MediaPlayer.create(Login.this, R.raw.audio_error);
                    mediaPlayer.start();
            }


            else{
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
                                        Toast.makeText(Login.this, "No existe el usuario", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                }





        });

    }


}