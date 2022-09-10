package com.vvcompany.playsome_1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.Objects;

public class SplashScreenActivity extends AppCompatActivity {
    ImageView image;
    LinearLayout linearLayout;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Objects.requireNonNull(getSupportActionBar()).hide();
        image = findViewById(R.id.splash_screen_id);
        linearLayout = findViewById(R.id.ss_ll);
        linearLayout.setVisibility(View.INVISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);
        animation.setDuration(1000);
        Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
        animation1.setDuration(1500);
//        image.setImageResource(R.drawable.logo1);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                image.setImageResource(R.drawable.logo1);
                linearLayout.setVisibility(View.VISIBLE);
                animation1.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
//                        linearLayout.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                image.startAnimation(animation1);




            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        image.startAnimation(animation);


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });thread.start();


    }

}