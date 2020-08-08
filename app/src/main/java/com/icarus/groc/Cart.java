package com.icarus.groc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Cart {
    private String uId;
    private String phone;
    private HashMap<String,Integer> cart;

    public Cart(String uId) {
        this.uId = uId;
        cart = new HashMap<String,Integer>();
    }

    public Cart(){
        cart = new HashMap<>();
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void addToCart(String pid, Integer qty) {
        cart.put(pid, qty);
    }

    public String getuId(){return uId;}
    public String getPhone(){return phone;}
    public HashMap<String,Integer> getCart(){return cart;}
}



