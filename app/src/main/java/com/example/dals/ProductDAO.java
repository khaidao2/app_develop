package com.example.dals;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.k23411t_app.DatabaseHelper;
import com.example.models.Product;
import java.util.ArrayList;

public class ProductDAO {
    private DatabaseHelper dbHelper;

    public ProductDAO(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    private String getTableName(SQLiteDatabase db) {
        String[] possibleNames = {"Product", "Products", "product", "products"};
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
        return "[Product]";
    }

    public ArrayList<Product> getAllProducts() {
        ArrayList<Product> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.openDatabase();
        String tableName = getTableName(db);
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + tableName, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int idxId = getColumnIndex(cursor, "productID", "ProductID", "id");
                    int idxName = getColumnIndex(cursor, "productName", "ProductName", "name");
                    int idxCoupon = getColumnIndex(cursor, "coupon", "Coupon");
                    int idxPrice = getColumnIndex(cursor, "price", "Price");
                    int idxQty = getColumnIndex(cursor, "quantity", "Quantity");
                    int idxVat = getColumnIndex(cursor, "VAT", "vat");
                    int idxDesc = getColumnIndex(cursor, "description", "Description");
                    int idxCateId = getColumnIndex(cursor, "CateID", "cateID", "CateId", "CategoryID", "categoryID");

                    String id = idxId != -1 ? cursor.getString(idxId) : "";
                    String name = idxName != -1 ? cursor.getString(idxName) : "";
                    String coupon = idxCoupon != -1 ? cursor.getString(idxCoupon) : "";
                    double price = idxPrice != -1 ? cursor.getDouble(idxPrice) : 0.0;
                    double quantity = idxQty != -1 ? cursor.getDouble(idxQty) : 0.0;
                    String vat = idxVat != -1 ? cursor.getString(idxVat) : "";
                    String desc = idxDesc != -1 ? cursor.getString(idxDesc) : "";
                    String cateId = idxCateId != -1 ? cursor.getString(idxCateId) : "";

                    Product product = new Product(id, name, coupon, price, quantity, vat, desc, cateId);
                    list.add(product);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return list;
    }

    public ArrayList<Product> getProductsByCategory(String categoryId) {
        ArrayList<Product> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.openDatabase();
        String tableName = getTableName(db);
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + tableName + " WHERE CateID = ? OR cateID = ? OR CateId = ? OR CategoryID = ?", 
                    new String[]{categoryId, categoryId, categoryId, categoryId});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int idxId = getColumnIndex(cursor, "productID", "ProductID", "id");
                    int idxName = getColumnIndex(cursor, "productName", "ProductName", "name");
                    int idxCoupon = getColumnIndex(cursor, "coupon", "Coupon");
                    int idxPrice = getColumnIndex(cursor, "price", "Price");
                    int idxQty = getColumnIndex(cursor, "quantity", "Quantity");
                    int idxVat = getColumnIndex(cursor, "VAT", "vat");
                    int idxDesc = getColumnIndex(cursor, "description", "Description");
                    int idxCateId = getColumnIndex(cursor, "CateID", "cateID", "CateId", "CategoryID", "categoryID");

                    String id = idxId != -1 ? cursor.getString(idxId) : "";
                    String name = idxName != -1 ? cursor.getString(idxName) : "";
                    String coupon = idxCoupon != -1 ? cursor.getString(idxCoupon) : "";
                    double price = idxPrice != -1 ? cursor.getDouble(idxPrice) : 0.0;
                    double quantity = idxQty != -1 ? cursor.getDouble(idxQty) : 0.0;
                    String vat = idxVat != -1 ? cursor.getString(idxVat) : "";
                    String desc = idxDesc != -1 ? cursor.getString(idxDesc) : "";
                    String cateId = idxCateId != -1 ? cursor.getString(idxCateId) : "";

                    Product product = new Product(id, name, coupon, price, quantity, vat, desc, cateId);
                    list.add(product);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return list;
    }

    public Product getProductById(String id) {
        SQLiteDatabase db = dbHelper.openDatabase();
        String tableName = getTableName(db);
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + tableName + " LIMIT 1", null);
            if (cursor == null) return null;
            
            String colId = "ProductID";
            if (cursor.getColumnIndex("productID") != -1) colId = "productID";
            else if (cursor.getColumnIndex("id") != -1) colId = "id";
            
            cursor.close();
            
            cursor = db.rawQuery("SELECT * FROM " + tableName + " WHERE " + colId + " = ?", new String[]{id});
            if (cursor != null && cursor.moveToFirst()) {
                int idxId = getColumnIndex(cursor, "productID", "ProductID", "id");
                int idxName = getColumnIndex(cursor, "productName", "ProductName", "name");
                int idxCoupon = getColumnIndex(cursor, "coupon", "Coupon");
                int idxPrice = getColumnIndex(cursor, "price", "Price");
                int idxQty = getColumnIndex(cursor, "quantity", "Quantity");
                int idxVat = getColumnIndex(cursor, "VAT", "vat");
                int idxDesc = getColumnIndex(cursor, "description", "Description");
                int idxCateId = getColumnIndex(cursor, "CateID", "cateID", "CateId", "CategoryID", "categoryID");

                String pId = idxId != -1 ? cursor.getString(idxId) : "";
                String name = idxName != -1 ? cursor.getString(idxName) : "";
                String coupon = idxCoupon != -1 ? cursor.getString(idxCoupon) : "";
                double price = idxPrice != -1 ? cursor.getDouble(idxPrice) : 0.0;
                double quantity = idxQty != -1 ? cursor.getDouble(idxQty) : 0.0;
                String vat = idxVat != -1 ? cursor.getString(idxVat) : "";
                String desc = idxDesc != -1 ? cursor.getString(idxDesc) : "";
                String cateId = idxCateId != -1 ? cursor.getString(idxCateId) : "";

                return new Product(pId, name, coupon, price, quantity, vat, desc, cateId);
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return null;
    }

    public long insertProduct(Product product) {
        SQLiteDatabase db = dbHelper.openDatabase();
        String tableName = getTableName(db);
        String cleanTableName = tableName.replace("[", "").replace("]", "");
        ContentValues values = new ContentValues();
        values.put("ProductID", product.getProductID());
        values.put("ProductName", product.getProductName());
        values.put("Coupon", product.getCoupon());
        values.put("Price", product.getPrice());
        values.put("Quantity", product.getQuantity());
        values.put("VAT", product.getVAT());
        values.put("Description", product.getDescription());
        values.put("CateID", product.getCateID());
        
        long result = db.insert(cleanTableName, null, values);
        db.close();
        return result;
    }

    public int updateProduct(Product product) {
        SQLiteDatabase db = dbHelper.openDatabase();
        String tableName = getTableName(db);
        String cleanTableName = tableName.replace("[", "").replace("]", "");
        ContentValues values = new ContentValues();
        values.put("ProductName", product.getProductName());
        values.put("Coupon", product.getCoupon());
        values.put("Price", product.getPrice());
        values.put("Quantity", product.getQuantity());
        values.put("VAT", product.getVAT());
        values.put("Description", product.getDescription());
        values.put("CateID", product.getCateID());
        
        int result = db.update(cleanTableName, values, "ProductID = ? OR productID = ? OR id = ?", new String[]{product.getProductID(), product.getProductID(), product.getProductID()});
        db.close();
        return result;
    }

    public int deleteProduct(String productId) {
        SQLiteDatabase db = dbHelper.openDatabase();
        String tableName = getTableName(db);
        String cleanTableName = tableName.replace("[", "").replace("]", "");
        int result = db.delete(cleanTableName, "ProductID = ? OR productID = ? OR id = ?", new String[]{productId, productId, productId});
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
