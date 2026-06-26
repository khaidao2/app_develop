package com.example.fbmodels;

/**
 * Map node "orderDetails/{id}": { orderId, productId, quantity, unitPrice }.
 */
public class OrderDetail {
    private String orderId;
    private String productId;
    private long quantity;
    private double unitPrice;

    public OrderDetail() {
    }

    public OrderDetail(String orderId, String productId, long quantity, double unitPrice) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
}
