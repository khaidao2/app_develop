package com.example.models;

import java.io.Serializable;
import java.util.Date;

public class Customer implements Serializable {
    private String cusID;
    private String cusName;
    private String phone;
    private String email;
    private Date birthday;
    private String address;
    private String VAT;

    public Customer() {
    }

    public Customer(String cusID, String cusName, String phone, String email, Date birthday, String address) {
        this.cusID = cusID;
        this.cusName = cusName;
        this.phone = phone;
        this.email = email;
        this.birthday = birthday;
        this.address = address;
    }

    public String getCusID() {
        return cusID;
    }

    public String getCusName() {
        return cusName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public Date getBirthday() {
        return birthday;
    }

    public String getAddress() {
        return address;
    }

    public void setCusID(String cusID) {
        this.cusID = cusID;
    }

    public void setCusName(String cusName) {
        this.cusName = cusName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getVAT() {
        return VAT;
    }

    public void setVAT(String VAT) {
        this.VAT = VAT;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "cusID='" + cusID + '\'' +
                ", cusName='" + cusName + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", birthday=" + birthday +
                ", address='" + address + '\'' +
                '}';
    }
}
