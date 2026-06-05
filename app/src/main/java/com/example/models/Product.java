package com.example.models;

import java.io.Serializable;

public class Product implements Serializable {
    private String productID;
    private String productName;
    private String coupon;
    private double price;
    private double quantity;
    private String VAT;
    private String description;
    private String CateID;

    public Product() {
    }

    public Product(String productID, String productName, String coupon, double price, double quantity, String VAT, String description, String cateID) {
        this.productID = productID;
        this.productName = productName;
        this.coupon = coupon;
        this.price = price;
        this.quantity = quantity;
        this.VAT = VAT;
        this.description = description;
        CateID = cateID;
    }

    public String getProductID() {
        return productID;
    }

    public String getProductName() {
        return productName;
    }

    public String getCoupon() {
        return coupon;
    }

    public double getPrice() {
        return price;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getVAT() {
        return VAT;
    }

    public String getDescription() {
        return description;
    }

    public String getCateID() {
        return CateID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public void setVAT(String VAT) {
        this.VAT = VAT;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCateID(String cateID) {
        CateID = cateID;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productID='" + productID + '\'' +
                ", productName='" + productName + '\'' +
                ", coupon='" + coupon + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", VAT='" + VAT + '\'' +
                ", description='" + description + '\'' +
                ", CateID='" + CateID + '\'' +
                '}';
    }
}
