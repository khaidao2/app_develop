package com.example.fbmodels;

/**
 * Map node "employees/{id}": { fullName, email, department, position }.
 */
public class Employee {
    private String fullName;
    private String email;
    private String department;
    private String position;

    public Employee() {
    }

    public Employee(String fullName, String email, String department, String position) {
        this.fullName = fullName;
        this.email = email;
        this.department = department;
        this.position = position;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String summary() {
        return fullName + "\n" + position + " - " + department;
    }
}
