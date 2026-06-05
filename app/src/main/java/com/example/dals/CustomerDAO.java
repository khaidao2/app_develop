package com.example.dals;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.k23411t_app.DatabaseHelper;
import com.example.models.Customer;
import java.util.ArrayList;
import java.util.Date;

public class CustomerDAO {
    private DatabaseHelper dbHelper;

    public CustomerDAO(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    private String getTableName(SQLiteDatabase db) {
        String[] possibleNames = {"Customer", "Customers", "customer", "customers"};
        for (String name : possibleNames) {
            Cursor cursor = null;
            try {
                cursor = db.rawQuery("SELECT 1 FROM [" + name + "] LIMIT 1", null);
                if (cursor != null) {
                    return "[" + name + "]";
                }
            } catch (Exception e) {
                // Ignore
            } finally {
                if (cursor != null) cursor.close();
            }
        }
        return "[Customer]";
    }

    public ArrayList<Customer> getAllCustomers() {
        ArrayList<Customer> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.openDatabase();
        String tableName = getTableName(db);
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + tableName, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int idxId = getColumnIndex(cursor, "cusID", "CustomerID", "cusId", "id", "customerID");
                    int idxName = getColumnIndex(cursor, "cusName", "CustomerName", "name", "customerName");
                    int idxPhone = getColumnIndex(cursor, "phone", "Phone", "phoneNumber");
                    int idxEmail = getColumnIndex(cursor, "email", "Email");
                    int idxBirthday = getColumnIndex(cursor, "birthday", "Birthday", "birthdate", "BirthDate");
                    int idxAddress = getColumnIndex(cursor, "address", "Address");
                    int idxVat = getColumnIndex(cursor, "VAT", "vat");

                    String id = idxId != -1 ? cursor.getString(idxId) : "";
                    String name = idxName != -1 ? cursor.getString(idxName) : "";
                    String phone = idxPhone != -1 ? cursor.getString(idxPhone) : "";
                    String email = idxEmail != -1 ? cursor.getString(idxEmail) : "";
                    
                    Date birthday = new Date();
                    if (idxBirthday != -1) {
                        birthday = parseDate(cursor.getString(idxBirthday));
                    }
                    
                    String address = idxAddress != -1 ? cursor.getString(idxAddress) : "";
                    String vat = idxVat != -1 ? cursor.getString(idxVat) : "";

                    Customer customer = new Customer(id, name, phone, email, birthday, address);
                    customer.setVAT(vat);
                    list.add(customer);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return list;
    }

    public Customer getCustomerById(String id) {
        SQLiteDatabase db = dbHelper.openDatabase();
        String tableName = getTableName(db);
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + tableName + " LIMIT 1", null);
            if (cursor == null) return null;
            
            String colId = "CustomerID";
            if (cursor.getColumnIndex("cusID") != -1) colId = "cusID";
            else if (cursor.getColumnIndex("cusId") != -1) colId = "cusId";
            else if (cursor.getColumnIndex("id") != -1) colId = "id";
            else if (cursor.getColumnIndex("customerID") != -1) colId = "customerID";
            
            cursor.close();
            
            cursor = db.rawQuery("SELECT * FROM " + tableName + " WHERE " + colId + " = ?", new String[]{id});
            if (cursor != null && cursor.moveToFirst()) {
                int idxId = getColumnIndex(cursor, "cusID", "CustomerID", "cusId", "id", "customerID");
                int idxName = getColumnIndex(cursor, "cusName", "CustomerName", "name", "customerName");
                int idxPhone = getColumnIndex(cursor, "phone", "Phone", "phoneNumber");
                int idxEmail = getColumnIndex(cursor, "email", "Email");
                int idxBirthday = getColumnIndex(cursor, "birthday", "Birthday", "birthdate", "BirthDate");
                int idxAddress = getColumnIndex(cursor, "address", "Address");
                int idxVat = getColumnIndex(cursor, "VAT", "vat");

                String cusId = idxId != -1 ? cursor.getString(idxId) : "";
                String name = idxName != -1 ? cursor.getString(idxName) : "";
                String phone = idxPhone != -1 ? cursor.getString(idxPhone) : "";
                String email = idxEmail != -1 ? cursor.getString(idxEmail) : "";
                
                Date birthday = new Date();
                if (idxBirthday != -1) {
                    birthday = parseDate(cursor.getString(idxBirthday));
                }
                
                String address = idxAddress != -1 ? cursor.getString(idxAddress) : "";
                String vat = idxVat != -1 ? cursor.getString(idxVat) : "";

                Customer customer = new Customer(cusId, name, phone, email, birthday, address);
                customer.setVAT(vat);
                return customer;
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return null;
    }

    public long insertCustomer(Customer customer) {
        SQLiteDatabase db = dbHelper.openDatabase();
        String tableName = getTableName(db);
        String cleanTableName = tableName.replace("[", "").replace("]", "");
        ContentValues values = new ContentValues();
        values.put("CustomerID", customer.getCusID());
        values.put("CustomerName", customer.getCusName());
        values.put("Phone", customer.getPhone());
        values.put("Email", customer.getEmail());
        if (customer.getBirthday() != null) {
            values.put("Birthday", customer.getBirthday().getTime());
        }
        values.put("Address", customer.getAddress());
        values.put("VAT", customer.getVAT());
        
        long result = db.insert(cleanTableName, null, values);
        db.close();
        return result;
    }

    public int updateCustomer(Customer customer) {
        SQLiteDatabase db = dbHelper.openDatabase();
        String tableName = getTableName(db);
        String cleanTableName = tableName.replace("[", "").replace("]", "");
        ContentValues values = new ContentValues();
        values.put("CustomerName", customer.getCusName());
        values.put("Phone", customer.getPhone());
        values.put("Email", customer.getEmail());
        if (customer.getBirthday() != null) {
            values.put("Birthday", customer.getBirthday().getTime());
        }
        values.put("Address", customer.getAddress());
        values.put("VAT", customer.getVAT());
        
        int result = db.update(cleanTableName, values, "CustomerID = ? OR cusID = ? OR id = ?", new String[]{customer.getCusID(), customer.getCusID(), customer.getCusID()});
        db.close();
        return result;
    }

    public int deleteCustomer(String customerId) {
        SQLiteDatabase db = dbHelper.openDatabase();
        String tableName = getTableName(db);
        String cleanTableName = tableName.replace("[", "").replace("]", "");
        int result = db.delete(cleanTableName, "CustomerID = ? OR cusID = ? OR id = ?", new String[]{customerId, customerId, customerId});
        db.close();
        return result;
    }

    private int getColumnIndex(Cursor cursor, String... names) {
        for (String name : names) {
            int idx = cursor.getColumnIndex(name);
            if (idx != -1) return idx;
        }
        return -1;
    }

    private Date parseDate(String value) {
        if (value == null || value.trim().isEmpty()) return new Date();
        try {
            long ms = Long.parseLong(value);
            return new Date(ms);
        } catch (NumberFormatException e) {
            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                return sdf.parse(value);
            } catch (Exception ex) {
                try {
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                    return sdf.parse(value);
                } catch (Exception ex2) {
                    return new Date();
                }
            }
        }
    }
}
