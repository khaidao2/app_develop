package com.example.k23411t_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.adapters.CategoryAdapter;
import com.example.dals.CategoryDAO;
import com.example.models.Category;

import java.util.ArrayList;

public class CategoryManagementActivity extends AppCompatActivity {

    private ListView lv_category;
    private CategoryAdapter categoryAdapter;
    private CategoryDAO categoryDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category_management);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        loadCategories();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCategories();
    }

    private void initViews() {
        lv_category = findViewById(R.id.lv_category);
        categoryDAO = new CategoryDAO(this);
        categoryAdapter = new CategoryAdapter(this, R.layout.item_custom_category);
        lv_category.setAdapter(categoryAdapter);

        // Khi click chọn Category, mở danh sách Product tương ứng
        lv_category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Category selectedCategory = categoryAdapter.getItem(position);
                if (selectedCategory != null) {
                    Intent intent = new Intent(CategoryManagementActivity.this, ProductListActivity.class);
                    intent.putExtra("CateID", selectedCategory.getCategory());
                    intent.putExtra("CategoryName", selectedCategory.getCategoryName());
                    startActivity(intent);
                }
            }
        });
    }

    private void loadCategories() {
        try {
            ArrayList<Category> categories = categoryDAO.getAllCategories();
            categoryAdapter.clear();
            if (categories != null) {
                categoryAdapter.addAll(categories);
            }
            categoryAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.str_load_category_failed), Toast.LENGTH_SHORT).show();
        }
    }

    public void closeActivity(View view) {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.category_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_new_category) {
            Intent intent = new Intent(CategoryManagementActivity.this, CategoryNewActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_statistic) {
            Toast.makeText(this, getString(R.string.str_statistic), Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
