package com.example.travelbuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        runThreadDelay();
    }
    private void runThreadDelay() {
        Thread myThread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(1500);
                    startActivity(new Intent(SplashActivity.this, Hotel.class));
                   finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        myThread.start();
    }
}