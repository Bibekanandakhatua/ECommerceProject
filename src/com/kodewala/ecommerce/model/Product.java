package com.kodewala.ecommerce.model;

public class Product {
    private int productId;
    private String productName;
    private String category;
    private double price;
    private int quantity;
    private String brand;

    public Product() {
    }

    public Product(int productId, String productName, String category, double price, int quantity, String brand) {
        this.productId = productId;
        this.productName = productName;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.brand = brand;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    @Override
    public String toString() {
        return String.format("Product [ID: %d, Name: %s, Category: %s, Brand: %s, Price: $%.2f, Quantity: %d]",
                productId, productName, category, brand, price, quantity);
    }
}
