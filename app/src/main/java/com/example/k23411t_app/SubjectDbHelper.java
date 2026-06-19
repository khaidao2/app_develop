package com.example.k23411t_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.models.Subject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Quản lý CSDL SQLite chứa danh sách môn học theo từng ngành.
 *
 * <p>Lần đầu chạy, dữ liệu được nạp ("đưa vào SQLite") từ file
 * {@code assets/chinhquy_courses.json} vào bảng {@link #TABLE}. Các lần sau,
 * {@link SubjectActivity} đọc trực tiếp từ SQLite, không cần phân tích lại JSON.
 */
public class SubjectDbHelper extends SQLiteOpenHelper {

    private static final String TAG = "SubjectDbHelper";

    private static final String DB_NAME = "curriculum.db";
    private static final int DB_VERSION = 1;

    private static final String ASSET_FILE = "chinhquy_courses.json";

    public static final String TABLE = "subjects";
    public static final String COL_ID = "_id";
    public static final String COL_MAJOR = "major";
    public static final String COL_CODE = "code";
    public static final String COL_NAME = "name";
    public static final String COL_CREDITS = "credits";
    public static final String COL_TYPE = "type";

    private final Context context;

    public SubjectDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context.getApplicationContext();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_MAJOR + " TEXT, " +
                COL_CODE + " TEXT, " +
                COL_NAME + " TEXT, " +
                COL_CREDITS + " TEXT, " +
                COL_TYPE + " TEXT)");
        db.execSQL("CREATE INDEX idx_major ON " + TABLE + "(" + COL_MAJOR + ")");

        // Nạp dữ liệu từ assets ngay khi tạo bảng.
        seedFromAssets(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    /**
     * Đọc file JSON trong assets và chèn toàn bộ môn học vào bảng.
     */
    private void seedFromAssets(SQLiteDatabase db) {
        try {
            InputStream is = context.getAssets().open(ASSET_FILE);
            int size = is.available();
            byte[] buffer = new byte[size];
            //noinspection ResultOfMethodCallIgnored
            is.read(buffer);
            is.close();
            String jsonStr = new String(buffer, "UTF-8");

            JSONArray majors = new JSONArray(jsonStr);

            db.beginTransaction();
            try {
                for (int i = 0; i < majors.length(); i++) {
                    JSONObject majorObj = majors.getJSONObject(i);
                    String major = majorObj.optString("major", "");
                    JSONArray subjects = majorObj.optJSONArray("subjects");
                    if (subjects == null) {
                        continue;
                    }
                    for (int j = 0; j < subjects.length(); j++) {
                        JSONObject s = subjects.getJSONObject(j);
                        ContentValues cv = new ContentValues();
                        cv.put(COL_MAJOR, major);
                        cv.put(COL_CODE, s.optString("code", ""));
                        cv.put(COL_NAME, s.optString("name", ""));
                        cv.put(COL_CREDITS, s.optString("credits", ""));
                        cv.put(COL_TYPE, s.optString("type", ""));
                        db.insert(TABLE, null, cv);
                    }
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
            Log.d(TAG, "Đã nạp dữ liệu môn học từ assets vào SQLite.");
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi nạp dữ liệu môn học: " + e.getMessage(), e);
        }
    }

    /**
     * Lấy danh sách môn học của một ngành (đã loại trùng theo mã môn).
     *
     * @param major tên ngành (khớp đúng với trường "major" trong JSON)
     */
    public ArrayList<Subject> getSubjectsByMajor(String major) {
        ArrayList<Subject> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(
                true, // distinct
                TABLE,
                new String[]{COL_CODE, COL_NAME, COL_CREDITS, COL_TYPE},
                COL_MAJOR + " = ?",
                new String[]{major},
                null, null, COL_CODE + " ASC", null);
        try {
            while (c.moveToNext()) {
                list.add(new Subject(
                        c.getString(0),
                        c.getString(1),
                        c.getString(2),
                        c.getString(3)));
            }
        } finally {
            c.close();
        }
        return list;
    }
}
