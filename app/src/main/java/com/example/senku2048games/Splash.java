package com.example.senku2048games;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView logo1 = findViewById(R.id.TextViewTopTitle);
        TextView logo2 = findViewById(R.id.TextViewBottomTitle);

        Animation fade1 = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fade1.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startActivity(new Intent(Splash.this, Main.class));
                Splash.this.finish();
            }


            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        logo1.startAnimation(fade1);
        logo2.startAnimation(fade1);
    }
}


