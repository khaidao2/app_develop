package com.example.models;

public class OrderDetail {
    private String orderDetailId;
    private String orderID;
    private String productID;
    private int quantity;
    private double price;
    private double coupon;
    private double VAT;

    public OrderDetail() {
    }

    public OrderDetail(String orderDetailId, String orderID, String productID, int quantity, double price, double coupon, double VAT) {
        this.orderDetailId = orderDetailId;
        this.orderID = orderID;
        this.productID = productID;
        this.quantity = quantity;
        this.price = price;
        this.coupon = coupon;
        this.VAT = VAT;
    }

    public String getOrderDetailId() {
        return orderDetailId;
    }

    public String getOrderID() {
        return orderID;
    }

    public String getProductID() {
        return productID;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public double getCoupon() {
        return coupon;
    }

    public double getVAT() {
        return VAT;
    }

    public void setOrderDetailId(String orderDetailId) {
        this.orderDetailId = orderDetailId;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setCoupon(double coupon) {
        this.coupon = coupon;
    }

    public void setVAT(double VAT) {
        this.VAT = VAT;
    }

    @Override
    public String toString() {
        return "OrderDetail{" +
                "orderDetailId='" + orderDetailId + '\'' +
                ", orderID='" + orderID + '\'' +
                ", productID='" + productID + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", coupon=" + coupon +
                ", VAT=" + VAT +
                '}';
    }
}
