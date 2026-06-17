package com.example.dals;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.k23411t_app.DatabaseHelper;
import com.example.models.Category;
import java.util.ArrayList;

public class CategoryDAO {
    private DatabaseHelper dbHelper;

    public CategoryDAO(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    private String getTableName(SQLiteDatabase db) {
        String[] possibleNames = {"Category", "Categories", "category", "categories"};
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
        return "[Category]";
    }

    // Lấy toàn bộ danh mục
    public ArrayList<Category> getAllCategories() {
        ArrayList<Category> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.openDatabase();
        String tableName = getTableName(db);
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + tableName, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int idxId = getColumnIndex(cursor, "Category", "category", "CategoryID", "categoryID");
                    int idxName = getColumnIndex(cursor, "CategoryName", "categoryName", "name", "Name");
                    int idxDesc = getColumnIndex(cursor, "Description", "description");
                    int idxImg = getColumnIndex(cursor, "ImageUrl", "imageUrl", "image", "Image");

                    String id = idxId != -1 ? cursor.getString(idxId) : "";
                    String name = idxName != -1 ? cursor.getString(idxName) : "";
                    String desc = idxDesc != -1 ? cursor.getString(idxDesc) : "";
                    String imgUrl = idxImg != -1 ? cursor.getString(idxImg) : "";

                    Category category = new Category(id, name, desc, imgUrl);
                    list.add(category);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return list;
    }

    // Lấy danh mục theo ID
    public Category getCategoryById(String id) {
        SQLiteDatabase db = dbHelper.openDatabase();
        String tableName = getTableName(db);
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + tableName + " LIMIT 1", null);
            if (cursor == null) return null;
            
            String colId = "Category";
            if (cursor.getColumnIndex("category") != -1) colId = "category";
            else if (cursor.getColumnIndex("CategoryID") != -1) colId = "CategoryID";
            else if (cursor.getColumnIndex("categoryID") != -1) colId = "categoryID";
            
            cursor.close();
            
            cursor = db.rawQuery("SELECT * FROM " + tableName + " WHERE " + colId + " = ?", new String[]{id});
            if (cursor != null && cursor.moveToFirst()) {
                int idxId = getColumnIndex(cursor, "Category", "category", "CategoryID", "categoryID");
                int idxName = getColumnIndex(cursor, "CategoryName", "categoryName", "name", "Name");
                int idxDesc = getColumnIndex(cursor, "Description", "description");
                int idxImg = getColumnIndex(cursor, "ImageUrl", "imageUrl", "image", "Image");

                String catId = idxId != -1 ? cursor.getString(idxId) : "";
                String name = idxName != -1 ? cursor.getString(idxName) : "";
                String desc = idxDesc != -1 ? cursor.getString(idxDesc) : "";
                String imgUrl = idxImg != -1 ? cursor.getString(idxImg) : "";

                return new Category(catId, name, desc, imgUrl);
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return null;
    }

    // Thêm danh mục
    public long insertCategory(Category category) {
        SQLiteDatabase db = dbHelper.openDatabase();
        String tableName = getTableName(db);
        String cleanTableName = tableName.replace("[", "").replace("]", "");
        ContentValues values = new ContentValues();
        values.put("Category", category.getCategory());
        values.put("CategoryName", category.getCategoryName());
        values.put("Description", category.getDescription());
        values.put("ImageUrl", category.getImageUrl());
        
        long result = db.insert(cleanTableName, null, values);
        db.close();
        return result;
    }

    // Sửa danh mục
    public int updateCategory(Category category) {
        SQLiteDatabase db = dbHelper.openDatabase();
        String tableName = getTableName(db);
        String cleanTableName = tableName.replace("[", "").replace("]", "");
        ContentValues values = new ContentValues();
        values.put("CategoryName", category.getCategoryName());
        values.put("Description", category.getDescription());
        values.put("ImageUrl", category.getImageUrl());
        
        int result = db.update(cleanTableName, values, "Category = ? OR category = ?", new String[]{category.getCategory(), category.getCategory()});
        db.close();
        return result;
    }

    // Xóa danh mục
    public int deleteCategory(String categoryId) {
        SQLiteDatabase db = dbHelper.openDatabase();
        String tableName = getTableName(db);
        String cleanTableName = tableName.replace("[", "").replace("]", "");
        int result = db.delete(cleanTableName, "Category = ? OR category = ?", new String[]{categoryId, categoryId});
        db.close();
        return result;
    }

    // Static CRUD wrappers
    public static ArrayList<Category> getAllCategoriesStatic(Context context) {
        return new CategoryDAO(context).getAllCategories();
    }

    public static Category getCategoryByIdStatic(Context context, String id) {
        return new CategoryDAO(context).getCategoryById(id);
    }

    public static long insertCategoryStatic(Context context, Category category) {
        return new CategoryDAO(context).insertCategory(category);
    }

    public static int updateCategoryStatic(Context context, Category category) {
        return new CategoryDAO(context).updateCategory(category);
    }

    public static int deleteCategoryStatic(Context context, String categoryId) {
        return new CategoryDAO(context).deleteCategory(categoryId);
    }

    private int getColumnIndex(Cursor cursor, String... names) {
        for (String name : names) {
            int idx = cursor.getColumnIndex(name);
            if (idx != -1) return idx;
        }
        return -1;
    }
}
