package com.icarus.groc;

public class Product {
    private String name;
    private String desc;
    private String photo;
    private float price;
    private String quantity;
    private int stock;
    private String category;
    private long id;

    public  Product(){}

    public Product(String category, String desc, String name, String photo, float price, String quantity, int stock, long id) {
        this.name = name;
        this.desc = desc;
        this.photo = photo;
        this.price = price;
        this.quantity = quantity;
        this.stock = stock;
        this.category = category;
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getPhoto() {
        return photo;
    }

    public String getQuantity(){
        return quantity;
    }

    public float getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public long getId() {
        return id;
    }
}
