package com.example.dals;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.k23411t_app.DatabaseHelper;
import com.example.models.Employee;
import java.util.ArrayList;

public class EmployeeDAO {
    private DatabaseHelper dbHelper;

    public EmployeeDAO(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    private String getTableName(SQLiteDatabase db) {
        String[] possibleNames = {"Employee", "Employees", "employee", "employees"};
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
        return "[Employee]";
    }

    public ArrayList<Employee> getAllEmployees() {
        ArrayList<Employee> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.openDatabase();
        String tableName = getTableName(db);
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + tableName, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int idxId = getColumnIndex(cursor, "id", "EmployeeID", "ID", "EmployeeId", "employeeID");
                    int idxName = getColumnIndex(cursor, "name", "EmployeeName", "Name", "employeeName");
                    int idxPhone = getColumnIndex(cursor, "phone", "Phone", "phoneNumber");
                    int idxYear = getColumnIndex(cursor, "birthYear", "BirthYear", "year", "Year", "birth_year");
                    int idxPlace = getColumnIndex(cursor, "birthPlace", "BirthPlace", "address", "Address", "birth_place");

                    String id = idxId != -1 ? cursor.getString(idxId) : "";
                    String name = idxName != -1 ? cursor.getString(idxName) : "";
                    String phone = idxPhone != -1 ? cursor.getString(idxPhone) : "";
                    int year = idxYear != -1 ? cursor.getInt(idxYear) : 2000;
                    String place = idxPlace != -1 ? cursor.getString(idxPlace) : "";

                    Employee employee = new Employee(id, name, phone, year, place);
                    list.add(employee);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return list;
    }

    public Employee getEmployeeById(String id) {
        SQLiteDatabase db = dbHelper.openDatabase();
        String tableName = getTableName(db);
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + tableName + " LIMIT 1", null);
            if (cursor == null) return null;
            
            String colId = "EmployeeID";
            if (cursor.getColumnIndex("id") != -1) colId = "id";
            else if (cursor.getColumnIndex("EmployeeId") != -1) colId = "EmployeeId";
            else if (cursor.getColumnIndex("employeeID") != -1) colId = "employeeID";
            
            cursor.close();
            
            cursor = db.rawQuery("SELECT * FROM " + tableName + " WHERE " + colId + " = ?", new String[]{id});
            if (cursor != null && cursor.moveToFirst()) {
                int idxId = getColumnIndex(cursor, "id", "EmployeeID", "ID", "EmployeeId", "employeeID");
                int idxName = getColumnIndex(cursor, "name", "EmployeeName", "Name", "employeeName");
                int idxPhone = getColumnIndex(cursor, "phone", "Phone", "phoneNumber");
                int idxYear = getColumnIndex(cursor, "birthYear", "BirthYear", "year", "Year", "birth_year");
                int idxPlace = getColumnIndex(cursor, "birthPlace", "BirthPlace", "address", "Address", "birth_place");

                String empId = idxId != -1 ? cursor.getString(idxId) : "";
                String name = idxName != -1 ? cursor.getString(idxName) : "";
                String phone = idxPhone != -1 ? cursor.getString(idxPhone) : "";
                int year = idxYear != -1 ? cursor.getInt(idxYear) : 2000;
                String place = idxPlace != -1 ? cursor.getString(idxPlace) : "";

                return new Employee(empId, name, phone, year, place);
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return null;
    }

    public long insertEmployee(Employee employee) {
        SQLiteDatabase db = dbHelper.openDatabase();
        String tableName = getTableName(db);
        String cleanTableName = tableName.replace("[", "").replace("]", "");
        ContentValues values = new ContentValues();
        values.put("EmployeeID", employee.getId());
        values.put("EmployeeName", employee.getName());
        values.put("Phone", employee.getPhone());
        values.put("BirthYear", employee.getBirthYear());
        values.put("BirthPlace", employee.getBirthPlace());
        
        long result = db.insert(cleanTableName, null, values);
        db.close();
        return result;
    }

    public int updateEmployee(Employee employee) {
        SQLiteDatabase db = dbHelper.openDatabase();
        String tableName = getTableName(db);
        String cleanTableName = tableName.replace("[", "").replace("]", "");
        ContentValues values = new ContentValues();
        values.put("EmployeeName", employee.getName());
        values.put("Phone", employee.getPhone());
        values.put("BirthYear", employee.getBirthYear());
        values.put("BirthPlace", employee.getBirthPlace());
        
        int result = db.update(cleanTableName, values, "EmployeeID = ? OR id = ?", new String[]{employee.getId(), employee.getId()});
        db.close();
        return result;
    }

    public int deleteEmployee(String employeeId) {
        SQLiteDatabase db = dbHelper.openDatabase();
        String tableName = getTableName(db);
        String cleanTableName = tableName.replace("[", "").replace("]", "");
        int result = db.delete(cleanTableName, "EmployeeID = ? OR id = ?", new String[]{employeeId, employeeId});
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
