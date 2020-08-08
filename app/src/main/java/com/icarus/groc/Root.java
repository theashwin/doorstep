package com.icarus.groc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class Root extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private ArrayList<Product> products = new ArrayList<Product>();

    HashMap<String, ArrayList<Product>> map = new HashMap<>();

    public Cart cart;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    ValueEventListener valueEventListener;

    //CATEGORIES
    CardView all;
    CardView fruits;
    CardView groceries;
    CardView vegetables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fresco.initialize(this);

        mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            currentUser = user;
                            cart = new Cart(currentUser.getUid());
                        } else {
                            Toast.makeText(Root.this, "AUTH FAILED.", Toast.LENGTH_SHORT).show();
                            currentUser = null;
                        }
                    }
                });

        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("products");

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                try {
                    Product product = dataSnapshot.getValue(Product.class);
                    products.add(product);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),"DATABASE ERROR: PRODUCT FETCH",Toast.LENGTH_SHORT).show();
                }

                valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try {
                            cart = dataSnapshot.getValue(Cart.class);
                            if(cart == null) {
                                cart = new Cart(currentUser.getUid());
                            }
                            all.performClick();
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(),"DATABASE ERROR: CART FETCH",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //DO NOTHING HERE
                    }
                };
                database.getReference("carts").child(currentUser.getUid())
                        .addListenerForSingleValueEvent(valueEventListener);

                try {
                    if(cart == null) {
                        cart = new Cart(currentUser.getUid());
                    }
                    mAdapter = new MyAdapter(products, cart);
                    recyclerView.setAdapter(mAdapter);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),"UPDATE UI ERROR: CART UI",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                //NOTHING
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //NOTHING
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                //NOTHING
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Root.this, "FAILED TO LOAD DATABASE : CHECK INTERNET",
                        Toast.LENGTH_SHORT).show();
            }
        };
        myRef.addChildEventListener(childEventListener);


        recyclerView = findViewById(R.id.recycler_view);
        layoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        all = findViewById(R.id.card_one);
        fruits = findViewById(R.id.fruits);
        vegetables = findViewById(R.id.veg);
        groceries = findViewById(R.id.groceries);

        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mAdapter = new MyAdapter(products, cart);
                    recyclerView.setAdapter(mAdapter);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),"SEARCH ERROR: PRODUCTS[ALL]",Toast.LENGTH_SHORT).show();
                }
            }
        });

        fruits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ArrayList<Product> search = new ArrayList<Product>();
                    if(map.containsKey("1")) {
                        search = map.get("1");
                    } else {
                        if(products.size() != 0) {
                            search.clear();
                            for(Product product: products){
                                if(product.getCategory().equals("1")) {
                                    search.add(product);
                                }
                            }
                            map.put("1",search);
                        }
                    }
                    mAdapter = new MyAdapter(search, cart);
                    recyclerView.setAdapter(mAdapter);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),"SEARCH ERROR: PRODUCTS[FRUITS]",Toast.LENGTH_SHORT).show();
                }
            }
        });

        vegetables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ArrayList<Product> search = new ArrayList<Product>();
                    if(map.containsKey("2")) {
                        search = map.get("2");
                    } else {
                        if(products.size() != 0) {
                            search.clear();
                            for(Product product: products){
                                if(product.getCategory().equals("2")) {
                                    search.add(product);
                                }
                            }
                            map.put("2",search);
                        }
                    }
                    mAdapter = new MyAdapter(search, cart);
                    recyclerView.setAdapter(mAdapter);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),"SEARCH ERROR: PRODUCTS[VEG]",Toast.LENGTH_SHORT).show();
                }
            }
        });

        groceries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ArrayList<Product> search = new ArrayList<Product>();
                    if(map.containsKey("3")) {
                        search = map.get("3");
                    } else {
                        if(products.size() != 0) {
                            search.clear();
                            for(Product product: products){
                                if(product.getCategory().equals("3")) {
                                    search.add(product);
                                }
                            }
                            map.put("3",search);
                        }
                    }
                    mAdapter = new MyAdapter(search, cart);
                    recyclerView.setAdapter(mAdapter);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),"SEARCH ERROR: PRODUCTS[GROCERIES]",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    public void onStart() {
        super.onStart();

        currentUser = mAuth.getCurrentUser();

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
