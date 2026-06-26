package com.example.fbmodels;

import com.google.firebase.database.PropertyName;

/**
 * Map node "products/{id}": { productName, price, stock, categoryId, imageUrl, isActive }.
 * @PropertyName giữ đúng key "isActive" (mặc định Firebase sẽ đổi thành "active").
 */
public class Product {
    private String productName;
    private double price;
    private long stock;
    private String categoryId;
    private String imageUrl;
    private boolean isActive;

    public Product() {
    }

    public Product(String productName, double price, long stock,
                   String categoryId, String imageUrl, boolean isActive) {
        this.productName = productName;
        this.price = price;
        this.stock = stock;
        this.categoryId = categoryId;
        this.imageUrl = imageUrl;
        this.isActive = isActive;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public long getStock() {
        return stock;
    }

    public void setStock(long stock) {
        this.stock = stock;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @PropertyName("isActive")
    public boolean isActive() {
        return isActive;
    }

    @PropertyName("isActive")
    public void setActive(boolean active) {
        isActive = active;
    }

    public String summary() {
        return productName + "\n" + String.format("%,.0f", price) + "đ - SL: " + stock
                + " - " + categoryId + (isActive ? "" : " (ẩn)");
    }
}
