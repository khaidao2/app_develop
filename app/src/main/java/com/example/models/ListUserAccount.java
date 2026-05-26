package com.example.models;

import java.util.ArrayList;

public class ListUserAccount {

    public static ArrayList<UserAccount> getUserAccount() {
        ArrayList<UserAccount> database = new ArrayList<>();

        database.add(new UserAccount("admin", "123456", "Administrator"));
        database.add(new UserAccount("kha", "123456", "Khải"));
        database.add(new UserAccount("user01", "123456", "User One"));

        return database;
    }

    public static UserAccount login(String username, String password) {
        ArrayList<UserAccount> database = getUserAccount();

        for (UserAccount acc : database) {
            if (acc.getUsername().equalsIgnoreCase(username)
                    && acc.getPassword().equals(password)) {
                return acc;
            }
        }

        return null;
    }
}