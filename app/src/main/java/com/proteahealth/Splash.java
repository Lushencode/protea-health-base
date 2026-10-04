package com.proteahealth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity {

    private ImageView logoImage;
    private TextView appName;
    private TextView tagline;
    private View pinkGlow;
    private View greenGlow;

    private static final int SPLASH_TIME = 2800;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        // Connect XML components
        logoImage = findViewById(R.id.logoImage);
        appName = findViewById(R.id.appName);
        tagline = findViewById(R.id.tagline);

        pinkGlow = findViewById(R.id.pinkGlow);
        greenGlow = findViewById(R.id.greenGlow);

        // Start animations
        animateLogo();
        animateText();
        animateGlows();

        new Handler().postDelayed(() -> {

            Intent intent = new Intent(
                    Splash.this, MedicationOrderActivity.class
            );

            startActivity(intent);


            overridePendingTransition(
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
            );

            finish();

        }, SPLASH_TIME);
    }


    private void animateLogo() {

        // Starting position
        logoImage.setScaleX(0.65f);
        logoImage.setScaleY(0.65f);
        logoImage.setAlpha(0f);

        // Fade + scale
        logoImage.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(1000)
                .setInterpolator(
                        new AccelerateDecelerateInterpolator()
                )
                .start();

        // Gentle floating movement
        logoImage.animate()
                .translationY(-10f)
                .setDuration(1200)
                .setStartDelay(1000)
                .withEndAction(() -> {

                    logoImage.animate()
                            .translationY(0f)
                            .setDuration(1200)
                            .start();

                })
                .start();
    }


    private void animateText() {

        appName.setTranslationY(25f);
        appName.setAlpha(0f);

        tagline.setTranslationY(25f);
        tagline.setAlpha(0f);


        // App name
        appName.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(700)
                .setStartDelay(600)
                .start();


        // Tagline
        tagline.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(700)
                .setStartDelay(900)
                .start();
    }


    private void animateGlows() {

        // Pink glow movement
        pinkGlow.animate()
                .translationX(-35f)
                .translationY(40f)
                .scaleX(1.2f)
                .scaleY(1.2f)
                .setDuration(2500)
                .setInterpolator(
                        new AccelerateDecelerateInterpolator()
                )
                .withEndAction(() -> {

                    pinkGlow.animate()
                            .translationX(0f)
                            .translationY(0f)
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(2500)
                            .start();

                })
                .start();


        // Green glow movement
        greenGlow.animate()
                .translationX(40f)
                .translationY(-30f)
                .scaleX(1.15f)
                .scaleY(1.15f)
                .setDuration(2500)
                .setInterpolator(
                        new AccelerateDecelerateInterpolator()
                )
                .withEndAction(() -> {

                    greenGlow.animate()
                            .translationX(0f)
                            .translationY(0f)
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(2500)
                            .start();

                })
                .start();
    }
}