package com.example.k23411t_app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    // Tên file database trong thư mục assets
    private static final String DATABASE_NAME = "K23411T_sales.sqlite";

    // Phiên bản database
    private static final int DATABASE_VERSION = 1;

    // Context của ứng dụng
    private final Context context;

    // Đường dẫn lưu database trên thiết bị
    // Android lưu DB tại: /data/data/<package>/databases/
    private final String databasePath;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        // Lấy đường dẫn thư mục databases của app
        this.databasePath = context.getDatabasePath(DATABASE_NAME).getPath();
    }

    /**
     * Kiểm tra database đã tồn tại trên thiết bị chưa.
     *
     * @return true nếu đã tồn tại, false nếu chưa
     */
    private boolean isDatabaseExists() {
        File dbFile = new File(databasePath);
        return dbFile.exists() && dbFile.length() > 0;
    }

    /**
     * Copy database từ thư mục assets vào bộ nhớ thiết bị.
     * Gọi hàm này trước khi mở database lần đầu.
     *
     * @throws IOException nếu xảy ra lỗi khi đọc/ghi file
     */
    public void copyDatabaseFromAssets() throws IOException {
        if (isDatabaseExists()) {
            Log.d(TAG, "Database đã tồn tại, bỏ qua bước copy.");
            return;
        }

        // Đảm bảo thư mục databases tồn tại
        File dbFolder = new File(databasePath).getParentFile();
        if (dbFolder != null && !dbFolder.exists()) {
            boolean created = dbFolder.mkdirs();
            Log.d(TAG, "Tạo thư mục databases: " + created);
        }

        Log.d(TAG, "Bắt đầu copy database từ assets...");

        // Mở file từ assets
        try (InputStream inputStream = context.getAssets().open(DATABASE_NAME);
             OutputStream outputStream = new FileOutputStream(databasePath)) {

            byte[] buffer = new byte[4096];
            int length;

            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.flush();
            Log.d(TAG, "Copy database thành công vào: " + databasePath);

        } catch (IOException e) {
            Log.e(TAG, "Lỗi khi copy database: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Mở database để đọc/ghi.
     * Tự động copy từ assets nếu chưa tồn tại.
     *
     * @return SQLiteDatabase instance
     */
    public SQLiteDatabase openDatabase() {
        try {
            copyDatabaseFromAssets();
        } catch (IOException e) {
            Log.e(TAG, "Không thể copy database: " + e.getMessage());
            throw new RuntimeException("Lỗi khởi tạo database!", e);
        }
        return SQLiteDatabase.openDatabase(databasePath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    /**
     * Xóa database trên thiết bị (dùng khi muốn cập nhật database mới từ assets).
     *
     * @return true nếu xóa thành công
     */
    public boolean resetDatabase() {
        File dbFile = new File(databasePath);
        if (dbFile.exists()) {
            boolean deleted = dbFile.delete();
            Log.d(TAG, "Xóa database cũ: " + deleted);
            return deleted;
        }
        return false;
    }

    // ─── Bắt buộc override khi extends SQLiteOpenHelper ───────────────────

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Không cần tạo bảng vì database đã được copy từ assets
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Khi muốn cập nhật database: xóa bản cũ rồi copy bản mới từ assets
        Log.d(TAG, "Nâng cấp database từ v" + oldVersion + " lên v" + newVersion);
        resetDatabase();
        try {
            copyDatabaseFromAssets();
        } catch (IOException e) {
            Log.e(TAG, "Lỗi khi nâng cấp database: " + e.getMessage());
        }
    }
}
