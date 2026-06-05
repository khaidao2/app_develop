package com.example.k23411t_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;



public class MainActivity extends AppCompatActivity {
    TextView txt_welcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        addViews();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void addViews(){
        txt_welcome=findViewById(R.id.txt_welcome);
        Intent intent =getIntent();
        String displayName = intent.getStringExtra("displayName");
        if (displayName != null)
        {
            txt_welcome.setText(getString(R.string.str_welcome_user, displayName));
        }
    }

    public void say_hello(View view) {
        Toast.makeText(this, getString(R.string.str_hello_class), Toast.LENGTH_LONG).show();
    }

    public void close_app(View view) {
        finish();
    }

    public void show_major(View view) {
        String my_major = getString(R.string.str_major);
        Toast.makeText(this, my_major, Toast.LENGTH_LONG).show();
    }

    public void open_cal(View view) {
        Intent intent = new Intent(MainActivity.this, CalculatorActivity.class);
        startActivity(intent);
    }

    public void open_order_management(View view) {
        Intent intent = new Intent(MainActivity.this, OrderMangementActivity.class);
        startActivity(intent);
    }

    public void open_category_management(View view) {
        Intent intent = new Intent(MainActivity.this, CategoryManagementActivity.class);
        startActivity(intent);
    }
}