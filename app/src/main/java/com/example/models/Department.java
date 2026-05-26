package com.example.models;

import java.util.ArrayList;

public class Department {
    private String departmentID;

    private String departmentName;
    private ArrayList<Employee> ListofEmployee;

    public Department() {
        this.ListofEmployee = new ArrayList<>();
    }

    public String getDepartmentID() {
        return departmentID;
    }

    public void setDepartmentID(String departmentID) {
        this.departmentID = departmentID;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    @Override
    public String toString() {
        return this.departmentName;
    }

    public void addEmployee(Employee emp) {
        this.ListofEmployee.add(emp);
    }

    public void addListEmployee(ArrayList<Employee> List) {
        this.ListofEmployee.addAll(List);
    }

    public ArrayList<Employee> getListofEmployee() {
        return ListofEmployee;
    }
}