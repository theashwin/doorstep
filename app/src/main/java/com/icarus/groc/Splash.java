package com.icarus.groc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import java.util.Random;

public class Splash extends AppCompatActivity {
    private ProgressBar progressBar;
    private int progressStatus = 0;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progressBar = findViewById(R.id.progressBar);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(progressStatus < 100) {
                    progressStatus += new Random().nextInt(15);
                    handler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressStatus);
                        }
                    });
                    try {
                        Thread.sleep(50);    //SET TO ARBITRARY NUMBER
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Intent onbIntent = new Intent(Splash.this, Root.class);
                Splash.this.startActivity(onbIntent);
                finish();
            }
        }).start();

    }
}
