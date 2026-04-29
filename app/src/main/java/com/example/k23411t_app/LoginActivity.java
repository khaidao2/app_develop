package com.example.k23411t_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {
    EditText edt_username;
    EditText edt_pwd;
    TextView txt_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        addViews();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void addViews() {
        edt_username=findViewById(R.id.edt_username);
        edt_pwd=findViewById(R.id.edt_pwd);
        txt_message=findViewById(R.id.txt_message);
    }

    public void loginSystem(View view) {
        String username=edt_username.getText().toString();
        String pwd=edt_pwd.getText().toString();
        if(username.equalsIgnoreCase("admin") && pwd.equals("123"))
        {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            txt_message.setText(getString(R.string.login_success));
        }
        else
        {
            txt_message.setText(getString(R.string.login_failed));
        }
    }

    public void exitSystem(View view) {
        finish();
    }
}