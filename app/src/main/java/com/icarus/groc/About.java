package com.icarus.groc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Button callUs = findViewById(R.id.callus);
        callUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "+918764114433"));
                startActivity(intent);
            }
        });

        Button emailUs = findViewById(R.id.emailus);
        emailUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mailto = "mailto:sidhchopra16@gmail.com" +
                        "?subject=" + Uri.encode("Regarding Doorstep Application") +
                        "&body=" + Uri.encode("Please write your message here.");

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse(mailto));

                try {
                    startActivity(emailIntent);
                } catch (Exception e) {

                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Intent i;
                        switch (item.getItemId()) {
                            case R.id.nav_info:
                                break;
                            case R.id.nav_shop:
                                i = new Intent(getApplicationContext(), Root.class);
                                startActivity(i);
                                break;
                            case R.id.nav_cart:
                                i = new Intent(getApplicationContext(), ViewCart.class);
                                startActivity(i);
                                break;
                            case R.id.nav_profile:
                                break;
                        }
                        return true;
                    }
                });
    }
}
