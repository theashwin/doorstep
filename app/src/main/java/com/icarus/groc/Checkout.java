package com.icarus.groc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.spec.ECField;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Checkout extends AppCompatActivity {
    private String orderId;
    private String currentUser;

    FirebaseDatabase database;

    Button confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");
        currentUser = intent.getStringExtra("currentUser");

        database = FirebaseDatabase.getInstance();
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
                                i = new Intent(getApplicationContext(), About.class);
                                startActivity(i);
                                break;
                            case R.id.nav_shop:
                                i = new Intent(getApplicationContext(), Root.class);
                                startActivity(i);
                                finish();
                                break;
                            case R.id.nav_cart:
                                i = new Intent(getApplicationContext(), ViewCart.class);
                                startActivity(i);
                                finish();
                                break;
                            case R.id.nav_profile:
                                break;
                        }
                        return true;
                    }
                });

        confirm = findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    EditText phoneText = findViewById(R.id.phone);
                    ConstraintLayout constraintLayout = findViewById(R.id.end);
                    TextView op = findViewById(R.id.op);
                    String phone = phoneText.getText().toString();
                    if(isValid(phone)) {
                        String isoDatePattern = "yyyy-MM-dd'T'HH:mm:ss'Z'";
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(isoDatePattern);
                        String dateString = simpleDateFormat.format(new Date());

                        database.getReference("orders").child(currentUser).child(orderId).child("phone").setValue(phone);
                        database.getReference("orders").child(currentUser).child(orderId).child("time").setValue(dateString);
                        database.getReference("carts").child(currentUser).removeValue();
                        constraintLayout.setVisibility(ConstraintLayout.VISIBLE);
                        op.setText("Order #"+orderId+" Placed!");
                        confirm.setClickable(false);
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"Please, Enter Correct Number!",Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),"DATABASE STORE ERROR.",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public static boolean isValid(String s) {
        Pattern p = Pattern.compile("[7-9][0-9]{9}");
        Matcher m = p.matcher(s);
        return (m.find() && m.group().equals(s));
    }
}
