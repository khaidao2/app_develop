package com.example.fbmodels;

/**
 * Map node "orders/{id}": { customerId, employeeId, orderDate, status, totalAmount }.
 */
public class Order {
    private String customerId;
    private String employeeId;
    private String orderDate;
    private String status;
    private double totalAmount;

    public Order() {
    }

    public Order(String customerId, String employeeId, String orderDate,
                 String status, double totalAmount) {
        this.customerId = customerId;
        this.employeeId = employeeId;
        this.orderDate = orderDate;
        this.status = status;
        this.totalAmount = totalAmount;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
