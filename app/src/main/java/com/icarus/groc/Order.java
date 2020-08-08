package com.icarus.groc;

import java.util.HashMap;

public class Order {
    public String uId;
    public String phone = "";
    public HashMap<String,HashMap<String,String>> cart;

    public Order(String uId) {
        this.uId = uId;
        cart = new HashMap<String,HashMap<String,String>>();
    }

    public Order(){
        cart = new HashMap<>();
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void addToCart(String pid, HashMap<String,String> item) {
        cart.put(pid, item);
    }

    public String getuId(){return uId;}
    public String getPhone(){return phone;}
    public HashMap<String,HashMap<String,String>> getCart(){return cart;}
}



