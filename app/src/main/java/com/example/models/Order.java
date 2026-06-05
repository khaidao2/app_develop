package com.example.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Order implements Serializable {
    private String  orderID;
    private String customerID;
    private String employeeID;
    private Date orderDate;
    private OrderStatus orderStatus;

    public Order() {
    }

    public Order(String orderID, String customerID, String employeeID, Date orderDate) {
        this.orderID = orderID;
        this.customerID = customerID;
        this.employeeID = employeeID;
        this.orderDate = orderDate;
    }

    public Order(String orderID, String customerID, String employeeID, Date orderDate, OrderStatus orderStatus) {
        this(orderID, customerID, employeeID, orderDate);
        this.orderStatus = orderStatus;
    }

    public String getOrderID() {
        return orderID;
    }

    public String getCustomerID() {
        return customerID;
    }

    public String getEmployeeID() {
        return employeeID;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    private Double cachedSum = null;

    public double getOrderSum() {
        if (cachedSum != null) {
            return cachedSum;
        }
        double sum = 0.0;
        ArrayList<OrderDetail> details = DataWarehouse.getOrderDetail();
        if (details != null) {
            for (OrderDetail detail : details) {
                if (detail.getOrderID() != null && detail.getOrderID().equals(this.orderID)) {
                    sum += detail.getQuantity() * detail.getPrice() * (1 - detail.getCoupon()) * (1 + detail.getVAT());
                }
            }
        }
        cachedSum = sum;
        return sum;
    }

    @Override
    public String toString() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
        String dateStr = (orderDate != null) ? sdf.format(orderDate) : "N/A";
        String statusStr = (orderStatus != null) ? orderStatus.name() : "N/A";
        return orderID + ";" + customerID + ";" + employeeID + ";" + dateStr + ";" + getOrderSum() + ";" + statusStr;
    }
}
