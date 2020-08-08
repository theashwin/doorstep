package com.icarus.groc;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {
    private static ArrayList<Product> mDataset;
    private FirebaseAuth mAuth;
    static private FirebaseUser currentUser;
    private FirebaseDatabase database;
    static private DatabaseReference myRef;
    static private Cart cart;

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView textView;
        TextView pqView;
        SimpleDraweeView draweeView;
        ConstraintLayout constraintLayout;
        ConstraintLayout cartView;
        Button addToCart;
        Button add;
        Button minus;
        TextView qty;
        Product item;
        public MyViewHolder(View v) {
            super(v);

            textView = (TextView) v.findViewById(R.id.textView);
            pqView = (TextView) v.findViewById(R.id.pqView);
            draweeView = (SimpleDraweeView) v.findViewById(R.id.imageView);
            constraintLayout = (ConstraintLayout) v.findViewById(R.id.constraintLayout);
            cartView = (ConstraintLayout) v.findViewById(R.id.cart_view);
            qty = (TextView) v.findViewById(R.id.qty);
            addToCart = (Button) v.findViewById(R.id.addToCart);
            draweeView.setOnClickListener(this);
            minus = (Button) v.findViewById(R.id.minus);
            add = (Button) v.findViewById(R.id.plus);

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Long productId = mDataset.get(getAdapterPosition()).getId();
                        Integer q = cart.getCart().get(Long.toString(productId)) + 1;
                        qty.setText(String.valueOf(q));
                        cart.addToCart(Long.toString(productId), q);
                        myRef.setValue(cart);
                    } catch(Exception e) {
                        Toast.makeText(v.getContext(),"CART UPDATE ERROR : +",Toast.LENGTH_SHORT).show();
                    }
                }
            });

            minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Long productId = mDataset.get(getAdapterPosition()).getId();
                        Integer q = cart.getCart().get(Long.toString(productId)) - 1;
                        qty.setText(String.valueOf(q));
                        cart.addToCart(Long.toString(productId), q);
                        if(q == 0) {
                            cart.getCart().remove(Long.toString(productId));
                            addToCart.setVisibility(Button.VISIBLE);
                            cartView.setVisibility(View.GONE);
                        }
                        myRef.setValue(cart);
                    } catch (Exception e) {
                        Toast.makeText(v.getContext(),"CART UPDATE ERROR : -",Toast.LENGTH_SHORT).show();
                    }
                }
            });

            addToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Long productId = mDataset.get(getAdapterPosition()).getId();
                        cart.addToCart(Long.toString(productId), 1);
                        qty.setText(String.valueOf(1));
                        myRef.setValue(cart);
                        addToCart.setVisibility(Button.GONE);
                        cartView.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        Toast.makeText(v.getContext(),"CART UPDATE ERROR : -",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        public void setData(Product item) {
            try {
                this.item = item;

                Uri uri = Uri.parse(item.getPhoto());
                draweeView.setImageURI(uri);
                textView.setText(item.getName());
                pqView.setText(item.getPrice() + " ₹  | " + item.getQuantity());

                String s = Long.toString(item.getId());
                if(cart.getCart().containsKey(s)) {
                    addToCart.setVisibility(Button.GONE);
                    cartView.setVisibility(View.VISIBLE);
                    qty.setText(String.valueOf(cart.getCart().get(s)));
                }
            } catch (Exception e) {

            }
        }

        @Override
        public void onClick(View v) {
            /*
            Toast.makeText(v.getContext(), "position = " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
            Intent itemIntent = new Intent(v.getContext(), Item.class);
            itemIntent.putExtra("id", mDataset.get(getAdapterPosition()).getId());
            v.getContext().startActivity(itemIntent);*/
        }
    }

    public CartAdapter(ArrayList<Product> myDataset, Cart cart) {
        mDataset = myDataset;
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        this.cart = cart;

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("carts").child(currentUser.getUid());
    }

    @Override
    public CartAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_cart, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.setData(mDataset.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}

