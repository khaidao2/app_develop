package com.example.dals;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.k23411t_app.DatabaseHelper;
import com.example.models.OrderDetail;
import java.util.ArrayList;

public class OrderDetailDAO {
    private DatabaseHelper dbHelper;

    public OrderDetailDAO(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    private String getTableName(SQLiteDatabase db) {
        String[] possibleNames = {"OrderDetail", "OrderDetails", "order_detail", "order_details"};
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
        return "[OrderDetail]";
    }

    public ArrayList<OrderDetail> getAllOrderDetails() {
        ArrayList<OrderDetail> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.openDatabase();
        String tableName = getTableName(db);
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + tableName, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int idxId = getColumnIndex(cursor, "orderDetailId", "OrderDetailId", "OrderDetailID", "id");
                    int idxOrderId = getColumnIndex(cursor, "orderID", "OrderID", "orderId");
                    int idxProductId = getColumnIndex(cursor, "productID", "ProductID", "productId");
                    int idxQty = getColumnIndex(cursor, "quantity", "Quantity");
                    int idxPrice = getColumnIndex(cursor, "price", "Price");
                    int idxCoupon = getColumnIndex(cursor, "coupon", "Coupon");
                    int idxVat = getColumnIndex(cursor, "VAT", "vat");

                    String id = idxId != -1 ? cursor.getString(idxId) : "";
                    String orderId = idxOrderId != -1 ? cursor.getString(idxOrderId) : "";
                    String productId = idxProductId != -1 ? cursor.getString(idxProductId) : "";
                    int qty = idxQty != -1 ? cursor.getInt(idxQty) : 0;
                    double price = idxPrice != -1 ? cursor.getDouble(idxPrice) : 0.0;
                    double coupon = idxCoupon != -1 ? cursor.getDouble(idxCoupon) : 0.0;
                    double vat = idxVat != -1 ? cursor.getDouble(idxVat) : 0.0;

                    OrderDetail detail = new OrderDetail(id, orderId, productId, qty, price, coupon, vat);
                    list.add(detail);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return list;
    }

    public ArrayList<OrderDetail> getOrderDetailsByOrderId(String orderId) {
        ArrayList<OrderDetail> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.openDatabase();
        String tableName = getTableName(db);
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + tableName + " WHERE orderID = ? OR OrderID = ? OR orderId = ?", 
                    new String[]{orderId, orderId, orderId});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int idxId = getColumnIndex(cursor, "orderDetailId", "OrderDetailId", "OrderDetailID", "id");
                    int idxOrderId = getColumnIndex(cursor, "orderID", "OrderID", "orderId");
                    int idxProductId = getColumnIndex(cursor, "productID", "ProductID", "productId");
                    int idxQty = getColumnIndex(cursor, "quantity", "Quantity");
                    int idxPrice = getColumnIndex(cursor, "price", "Price");
                    int idxCoupon = getColumnIndex(cursor, "coupon", "Coupon");
                    int idxVat = getColumnIndex(cursor, "VAT", "vat");

                    String id = idxId != -1 ? cursor.getString(idxId) : "";
                    String ordId = idxOrderId != -1 ? cursor.getString(idxOrderId) : "";
                    String productId = idxProductId != -1 ? cursor.getString(idxProductId) : "";
                    int qty = idxQty != -1 ? cursor.getInt(idxQty) : 0;
                    double price = idxPrice != -1 ? cursor.getDouble(idxPrice) : 0.0;
                    double coupon = idxCoupon != -1 ? cursor.getDouble(idxCoupon) : 0.0;
                    double vat = idxVat != -1 ? cursor.getDouble(idxVat) : 0.0;

                    OrderDetail detail = new OrderDetail(id, ordId, productId, qty, price, coupon, vat);
                    list.add(detail);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return list;
    }

    public long insertOrderDetail(OrderDetail detail) {
        SQLiteDatabase db = dbHelper.openDatabase();
        String tableName = getTableName(db);
        String cleanTableName = tableName.replace("[", "").replace("]", "");
        ContentValues values = new ContentValues();
        values.put("OrderDetailID", detail.getOrderDetailId());
        values.put("OrderID", detail.getOrderID());
        values.put("ProductID", detail.getProductID());
        values.put("Quantity", detail.getQuantity());
        values.put("Price", detail.getPrice());
        values.put("Coupon", detail.getCoupon());
        values.put("VAT", detail.getVAT());
        
        long result = db.insert(cleanTableName, null, values);
        db.close();
        return result;
    }

    public int updateOrderDetail(OrderDetail detail) {
        SQLiteDatabase db = dbHelper.openDatabase();
        String tableName = getTableName(db);
        String cleanTableName = tableName.replace("[", "").replace("]", "");
        ContentValues values = new ContentValues();
        values.put("OrderID", detail.getOrderID());
        values.put("ProductID", detail.getProductID());
        values.put("Quantity", detail.getQuantity());
        values.put("Price", detail.getPrice());
        values.put("Coupon", detail.getCoupon());
        values.put("VAT", detail.getVAT());
        
        int result = db.update(cleanTableName, values, "OrderDetailID = ? OR orderDetailId = ? OR id = ?", new String[]{detail.getOrderDetailId(), detail.getOrderDetailId(), detail.getOrderDetailId()});
        db.close();
        return result;
    }

    public int deleteOrderDetail(String orderDetailId) {
        SQLiteDatabase db = dbHelper.openDatabase();
        String tableName = getTableName(db);
        String cleanTableName = tableName.replace("[", "").replace("]", "");
        int result = db.delete(cleanTableName, "OrderDetailID = ? OR orderDetailId = ? OR id = ?", new String[]{orderDetailId, orderDetailId, orderDetailId});
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
}
