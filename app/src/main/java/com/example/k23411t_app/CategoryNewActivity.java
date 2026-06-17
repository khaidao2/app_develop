package com.example.k23411t_app;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dals.CategoryDAO;
import com.example.models.Category;

public class CategoryNewActivity extends AppCompatActivity {

    private EditText edt_category_id;
    private EditText edt_category_name;
    private EditText edt_category_desc;
    private CategoryDAO categoryDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category_new);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addViews();
    }

    private void addViews() {
        edt_category_id = findViewById(R.id.edt_category_id);
        edt_category_name = findViewById(R.id.edt_category_name);
        edt_category_desc = findViewById(R.id.edt_category_desc);
        categoryDAO = new CategoryDAO(this);
    }

    public void saveCategory(View view) {
        processSaveCategory();
    }

    public void cancelCategory(View view) {
        finish();
    }

    public static long saveNewCategory(Context context, Category category) {
        return CategoryDAO.insertCategoryStatic(context, category);
    }

    private void processSaveCategory() {
        String id = edt_category_id.getText().toString().trim();
        String name = edt_category_name.getText().toString().trim();
        String desc = edt_category_desc.getText().toString().trim();

        if (id.isEmpty() || name.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập Mã và Tên danh mục", Toast.LENGTH_SHORT).show();
            return;
        }

        Category category = new Category(id, name, desc, "");
        long result = -1;
        try {
            result = saveNewCategory(this, category);
            if (result != -1) {
                Toast.makeText(this, "Thêm danh mục thành công!", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Thêm danh mục thất bại (ID có thể đã tồn tại)!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Có lỗi xảy ra: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
