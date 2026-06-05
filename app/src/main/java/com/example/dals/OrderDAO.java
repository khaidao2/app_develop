package com.example.dals;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.k23411t_app.DatabaseHelper;
import com.example.models.Order;
import com.example.models.OrderStatus;
import java.util.ArrayList;
import java.util.Date;

public class OrderDAO {
    private DatabaseHelper dbHelper;

    public OrderDAO(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    private String getTableName(SQLiteDatabase db) {
        String[] possibleNames = {"Order", "Orders", "orders", "order"};
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
        return "[Order]";
    }

    public ArrayList<Order> getAllOrders() {
        ArrayList<Order> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.openDatabase();
        String tableName = getTableName(db);
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + tableName, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int idxId = getColumnIndex(cursor, "orderID", "OrderID", "id");
                    int idxCustId = getColumnIndex(cursor, "customerID", "CustomerID", "customerId", "cusID");
                    int idxEmpId = getColumnIndex(cursor, "employeeID", "EmployeeID", "employeeId", "empID");
                    int idxDate = getColumnIndex(cursor, "orderDate", "OrderDate", "date");
                    int idxStatus = getColumnIndex(cursor, "orderStatus", "OrderStatus", "status");

                    String id = idxId != -1 ? cursor.getString(idxId) : "";
                    String custId = idxCustId != -1 ? cursor.getString(idxCustId) : "";
                    String empId = idxEmpId != -1 ? cursor.getString(idxEmpId) : "";
                    
                    Date date = new Date();
                    if (idxDate != -1) {
                        date = parseDate(cursor.getString(idxDate));
                    }
                    
                    OrderStatus status = OrderStatus.ALL;
                    if (idxStatus != -1) {
                        status = parseStatus(cursor.getString(idxStatus));
                    }

                    Order order = new Order(id, custId, empId, date, status);
                    list.add(order);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return list;
    }

    public Order getOrderById(String id) {
        SQLiteDatabase db = dbHelper.openDatabase();
        String tableName = getTableName(db);
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + tableName + " LIMIT 1", null);
            if (cursor == null) return null;
            
            String colId = "OrderID";
            if (cursor.getColumnIndex("orderID") != -1) colId = "orderID";
            else if (cursor.getColumnIndex("id") != -1) colId = "id";
            
            cursor.close();
            
            cursor = db.rawQuery("SELECT * FROM " + tableName + " WHERE " + colId + " = ?", new String[]{id});
            if (cursor != null && cursor.moveToFirst()) {
                int idxId = getColumnIndex(cursor, "orderID", "OrderID", "id");
                int idxCustId = getColumnIndex(cursor, "customerID", "CustomerID", "customerId", "cusID");
                int idxEmpId = getColumnIndex(cursor, "employeeID", "EmployeeID", "employeeId", "empID");
                int idxDate = getColumnIndex(cursor, "orderDate", "OrderDate", "date");
                int idxStatus = getColumnIndex(cursor, "orderStatus", "OrderStatus", "status");

                String ordId = idxId != -1 ? cursor.getString(idxId) : "";
                String custId = idxCustId != -1 ? cursor.getString(idxCustId) : "";
                String empId = idxEmpId != -1 ? cursor.getString(idxEmpId) : "";
                
                Date date = new Date();
                if (idxDate != -1) {
                    date = parseDate(cursor.getString(idxDate));
                }
                
                OrderStatus status = OrderStatus.ALL;
                if (idxStatus != -1) {
                    status = parseStatus(cursor.getString(idxStatus));
                }

                return new Order(ordId, custId, empId, date, status);
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return null;
    }

    public long insertOrder(Order order) {
        SQLiteDatabase db = dbHelper.openDatabase();
        String tableName = getTableName(db);
        String cleanTableName = tableName.replace("[", "").replace("]", "");
        ContentValues values = new ContentValues();
        values.put("OrderID", order.getOrderID());
        values.put("CustomerID", order.getCustomerID());
        values.put("EmployeeID", order.getEmployeeID());
        if (order.getOrderDate() != null) {
            values.put("OrderDate", order.getOrderDate().getTime());
        }
        if (order.getOrderStatus() != null) {
            values.put("OrderStatus", order.getOrderStatus().name());
        }
        
        long result = db.insert(cleanTableName, null, values);
        db.close();
        return result;
    }

    public int updateOrder(Order order) {
        SQLiteDatabase db = dbHelper.openDatabase();
        String tableName = getTableName(db);
        String cleanTableName = tableName.replace("[", "").replace("]", "");
        ContentValues values = new ContentValues();
        values.put("CustomerID", order.getCustomerID());
        values.put("EmployeeID", order.getEmployeeID());
        if (order.getOrderDate() != null) {
            values.put("OrderDate", order.getOrderDate().getTime());
        }
        if (order.getOrderStatus() != null) {
            values.put("OrderStatus", order.getOrderStatus().name());
        }
        
        int result = db.update(cleanTableName, values, "OrderID = ? OR orderID = ? OR id = ?", new String[]{order.getOrderID(), order.getOrderID(), order.getOrderID()});
        db.close();
        return result;
    }

    public int deleteOrder(String orderId) {
        SQLiteDatabase db = dbHelper.openDatabase();
        String tableName = getTableName(db);
        String cleanTableName = tableName.replace("[", "").replace("]", "");
        int result = db.delete(cleanTableName, "OrderID = ? OR orderID = ? OR id = ?", new String[]{orderId, orderId, orderId});
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

    private OrderStatus parseStatus(String value) {
        if (value == null || value.trim().isEmpty()) return OrderStatus.ALL;
        try {
            int ord = Integer.parseInt(value);
            if (ord >= 0 && ord < OrderStatus.values().length) {
                return OrderStatus.values()[ord];
            }
        } catch (NumberFormatException e) {
            try {
                return OrderStatus.valueOf(value.toUpperCase().trim());
            } catch (IllegalArgumentException ex) {
                // Ignore
            }
        }
        return OrderStatus.ALL;
    }
}
