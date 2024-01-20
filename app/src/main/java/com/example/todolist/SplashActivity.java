package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;

import com.mikhaellopez.circularfillableloaders.CircularFillableLoaders;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class SplashActivity extends AppCompatActivity implements Animation.AnimationListener {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        CircularFillableLoaders cargando = findViewById(R.id.logo_circular);
        Animation animacion = AnimationUtils.loadAnimation(this, R.anim.splashanimation);
        cargando.startAnimation(animacion);

        animacion.setAnimationListener(this);
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        Intent intent = new Intent(SplashActivity.this, Login.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}