package com.example.dals;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.k23411t_app.DatabaseHelper;
import com.example.models.UserAccount;
import java.util.ArrayList;

public class UserAccountDAO {
    private DatabaseHelper dbHelper;

    public UserAccountDAO(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    private String getTableName(SQLiteDatabase db) {
        String[] possibleNames = {"UserAccount", "UserAccounts", "User", "Users", "Account", "Accounts"};
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
        return "[UserAccount]";
    }

    public ArrayList<UserAccount> getAllUserAccounts() {
        ArrayList<UserAccount> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.openDatabase();
        String tableName = getTableName(db);
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + tableName, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int idxUsername = getColumnIndex(cursor, "username", "Username");
                    int idxPassword = getColumnIndex(cursor, "password", "Password");
                    int idxDisplayName = getColumnIndex(cursor, "displayName", "DisplayName", "display_name", "name");

                    String username = idxUsername != -1 ? cursor.getString(idxUsername) : "";
                    String password = idxPassword != -1 ? cursor.getString(idxPassword) : "";
                    String displayName = idxDisplayName != -1 ? cursor.getString(idxDisplayName) : "";

                    UserAccount acc = new UserAccount(username, password, displayName);
                    list.add(acc);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return list;
    }

    public UserAccount login(String username, String password) {
        SQLiteDatabase db = dbHelper.openDatabase();
        String tableName = getTableName(db);
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + tableName + " LIMIT 1", null);
            if (cursor == null) return null;
            
            String colUsername = "username";
            String colPassword = "password";
            
            if (cursor.getColumnIndex("Username") != -1) colUsername = "Username";
            if (cursor.getColumnIndex("Password") != -1) colPassword = "Password";
            
            cursor.close();
            
            cursor = db.rawQuery("SELECT * FROM " + tableName + " WHERE " + colUsername + " = ? AND " + colPassword + " = ?", 
                    new String[]{username, password});
            if (cursor != null && cursor.moveToFirst()) {
                int idxUsername = getColumnIndex(cursor, "username", "Username");
                int idxPassword = getColumnIndex(cursor, "password", "Password");
                int idxDisplayName = getColumnIndex(cursor, "displayName", "DisplayName", "display_name", "name");

                String uname = idxUsername != -1 ? cursor.getString(idxUsername) : "";
                String pwd = idxPassword != -1 ? cursor.getString(idxPassword) : "";
                String displayName = idxDisplayName != -1 ? cursor.getString(idxDisplayName) : "";

                return new UserAccount(uname, pwd, displayName);
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return null;
    }

    public long insertUserAccount(UserAccount account) {
        SQLiteDatabase db = dbHelper.openDatabase();
        String tableName = getTableName(db);
        String cleanTableName = tableName.replace("[", "").replace("]", "");
        ContentValues values = new ContentValues();
        values.put("Username", account.getUsername());
        values.put("Password", account.getPassword());
        values.put("DisplayName", account.getDisplayName());
        
        long result = db.insert(cleanTableName, null, values);
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
