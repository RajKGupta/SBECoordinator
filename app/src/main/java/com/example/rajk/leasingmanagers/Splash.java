package com.example.rajk.leasingmanagers;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

public class Splash extends AppCompatActivity
{
    private static int SPLASH_TIME_OUT = 2500;
    ImageView verylarge, large, medium;
    TextView small;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_splash);

        verylarge = (ImageView)findViewById(R.id.verylarge);
        large = (ImageView)findViewById(R.id.large);
        medium = (ImageView)findViewById(R.id.medium);

        small = (TextView)findViewById(R.id.small);

        fadein_verylarge();
        fadein_large();
        fadein_medium();
        fadein_small();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(Splash.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    private void fadein_verylarge()
    {
        Animation fadeIn1 = new AlphaAnimation(0, 1);
        fadeIn1.setDuration(500);
        fadeIn1.setStartOffset(200);

        verylarge.startAnimation(fadeIn1);
    }

    private void fadein_large()
    {
        Animation fadeIn1 = new AlphaAnimation(0, 1);
        fadeIn1.setDuration(500);
        fadeIn1.setStartOffset(700);

        large.startAnimation(fadeIn1);
    }

    private void fadein_medium()
    {
        Animation fadeIn1 = new AlphaAnimation(0, 1);
        fadeIn1.setDuration(500);
        fadeIn1.setStartOffset(1200);

        medium.startAnimation(fadeIn1);
    }

    private void fadein_small()
    {
        Animation fadeIn1 = new AlphaAnimation(0, 1);
        fadeIn1.setDuration(500);
        fadeIn1.setStartOffset(1700);

        small.startAnimation(fadeIn1);
    }
}
