package com.icarus.groc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class ViewCart extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private ArrayList<Product> products = new ArrayList<Product>();

    HashMap<String, ArrayList<Product>> map = new HashMap<>();

    public Cart cart;
    public Order order;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    ValueEventListener valueEventListener;

    ArrayList<Product> search;
    Button checkout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_cart);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("products");

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                try {
                    final Product product = dataSnapshot.getValue(Product.class);
                    products.add(product);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),"DATABASE ERROR: PRODUCT FETCH",Toast.LENGTH_SHORT).show();
                }

                valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try {
                            cart = dataSnapshot.getValue(Cart.class);
                            if(!products.isEmpty() && cart != null) {
                                search = getProducts(cart,products);
                                mAdapter = new CartAdapter(search, cart);
                                recyclerView.setAdapter(mAdapter);
                                updateUI(cart, search);
                            }
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(),"DATABASE ERROR: CART FETCH",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //
                    }
                };
                database.getReference("carts").child(currentUser.getUid())
                        .addValueEventListener(valueEventListener);

                try {
                    if(cart == null) {
                        return;
                    }
                    if(search.isEmpty()) {
                        search = getProducts(cart,products);
                        if(search.isEmpty()) {
                            return;
                        }
                    }
                    mAdapter = new CartAdapter(getProducts(cart, products), cart);
                    recyclerView.setAdapter(mAdapter);
                    updateUI(cart, search);
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
                Toast.makeText(ViewCart.this, "FAILED TO LOAD DATABASE : CHECK INTERNET",
                        Toast.LENGTH_SHORT).show();
            }
        };
        myRef.addChildEventListener(childEventListener);

        //RV
        recyclerView = findViewById(R.id.recycler_view);
        layoutManager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onStart() {
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
                                i = new Intent(getApplicationContext(), Root.class);
                                startActivity(i);
                                finish();
                                break;
                            case R.id.nav_cart:
                                break;
                            case R.id.nav_profile:
                                break;
                        }
                        return true;
                    }
                });

        checkout = findViewById(R.id.place);
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String orderId = storeToDatabase();
                Intent i = new Intent(getApplicationContext(), Checkout.class);
                i.putExtra("orderId",orderId);
                i.putExtra("currentUser",currentUser.getUid());
                startActivity(i);
                finish();
            }
        });

        TextView itemsView = findViewById(R.id.items);
        if(itemsView.getText().toString().equals("-")) {
            checkout.setClickable(false);
        }
    }

    ArrayList<Product> getProducts(Cart cart, ArrayList<Product> products) {
        try {
            ArrayList<Product> search = new ArrayList<>();
            Log.d("TAG", "" + cart.getCart().keySet());
            for(Product product : products) {
                if(cart.getCart().containsKey(Long.toString(product.getId()))) {
                    search.add(product);
                }
            }
            Log.d("TAG", "" + search);
            return search;
        } catch (Exception e) {
            Toast.makeText(ViewCart.this, "FAILED TO LOAD PRODUCTS : CHECK INTERNET",
                    Toast.LENGTH_SHORT).show();
        }
        return new ArrayList<Product>();
    }

    void updateUI(Cart cart, ArrayList<Product> products) {
        try {
            double price = 0.0;
            int qty = 0;
            HashMap<Long,Float> p = new HashMap<>();
            for(Product product : products) {
                p.put(product.getId(),product.getPrice());
            }
            Log.d("TAGH","" + p.keySet() + p.values());


            for(String s : cart.getCart().keySet()) {
                price += cart.getCart().get(s) * p.get(Long.parseLong(s));
                qty += cart.getCart().get(s);
            }

            TextView priceView = findViewById(R.id.price);
            TextView itemsView = findViewById(R.id.items);

            priceView.setText("" + price +" ₹");
            itemsView.setText("" + qty);

            if(qty == 0) {
                checkout.setClickable(false);
            } else {
                checkout.setClickable(true);
            }
        } catch (Exception e) {
            Toast.makeText(ViewCart.this, "FAILED TO UPDATE UI : PRICE",
                    Toast.LENGTH_SHORT).show();
        }
    }

    String storeToDatabase() {
        try {
            if(products != null && cart != null) {
                Random rand = new Random();
                Long oID = (long)(rand.nextDouble()*1000000000L);
                String orderId = Long.toString(oID);

                order = new Order(currentUser.getUid());
                HashMap<String,Product> prod = new HashMap<>();
                for(Product product : products) {
                    prod.put(Long.toString(product.getId()),product);
                }

                for(String pid : cart.getCart().keySet()) {
                    HashMap<String,String> item = new HashMap<>();
                    item.put("name", prod.get(pid).getName());
                    item.put("price", Float.toString(prod.get(pid).getPrice()));
                    item.put("quantity", Integer.toString(cart.getCart().get(pid)));
                    order.cart.put(pid,item);
                }

                database.getReference("orders").child(currentUser.getUid()).child(orderId).setValue(order);
                return orderId;
            }
        } catch (Exception e) {
            Toast.makeText(ViewCart.this, "FAILED TO STORE ORDER",
                    Toast.LENGTH_SHORT).show();
        }
        return "";
    }
}