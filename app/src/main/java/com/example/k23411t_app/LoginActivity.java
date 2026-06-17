package com.example.k23411t_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.util.Log;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;
import java.io.IOException;

import com.example.models.ListUserAccount;
import com.example.models.UserAccount;

public class LoginActivity extends AppCompatActivity {

    EditText edt_username;
    EditText edt_pwd;

    TextView txt_message;

    CheckBox chkSaveInfo;

    RadioButton rad_admin, rad_user;

    Button btn_login, btn_exit;

    String name_share_ref;

    private BroadcastReceiver internetStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
                if (isConnected) {
                    boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
                    if (isWiFi) {
                        Toast.makeText(context, context.getString(R.string.str_wifi_connected), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, context.getString(R.string.str_internet_connected_no_wifi), Toast.LENGTH_LONG).show();
                    }
                    if (btn_login != null) {
                        btn_login.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(context, context.getString(R.string.str_network_disconnected), Toast.LENGTH_LONG).show();
                    if (btn_login != null) {
                        btn_login.setVisibility(View.GONE);
                    }
                }
            }
        }
    };

    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_PASSWORD = "123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_login);

        addViews();

        // Sao chép database từ assets sang bộ nhớ thiết bị
        copyDatabase();

        name_share_ref = getString(R.string.str_login_info);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars =
                            insets.getInsets(WindowInsetsCompat.Type.systemBars());

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                });
    }

    private void addViews() {

        edt_username = findViewById(R.id.edt_username);

        edt_pwd = findViewById(R.id.edt_pwd);

        txt_message = findViewById(R.id.txt_message);

        chkSaveInfo = findViewById(R.id.chkSaveInfo);

        rad_admin = findViewById(R.id.rad_admin);

        rad_user = findViewById(R.id.rad_user);

        btn_login = findViewById(R.id.btn_login);

        btn_exit = findViewById(R.id.btn_exit);
    }

    public void loginSystem(View view) {

        String username = edt_username.getText().toString().trim();
        String pwd = edt_pwd.getText().toString().trim();

        // 1. check rỗng
        if (username.isEmpty() || pwd.isEmpty()) {
            txt_message.setText(getString(R.string.login_failed));
            return;
        }

        // 2. login system
        UserAccount account = ListUserAccount.login(username, pwd);

        // 3. xử lý kết quả
        if (account != null) {

            txt_message.setText(getString(R.string.login_success));

            // save SharedPreferences
            SharedPreferences preferences =
                    getSharedPreferences(name_share_ref, MODE_PRIVATE);

            SharedPreferences.Editor editor = preferences.edit();

            editor.putString("Username", username);
            editor.putString("password", pwd);
            editor.putBoolean("SAVED", chkSaveInfo.isChecked());

            editor.apply();

            Intent intent;

            if (rad_admin.isChecked()) {

                intent = new Intent(
                        LoginActivity.this,
                        MainActivity.class
                );

            } else {

                intent = new Intent(
                        LoginActivity.this,
                        EmployeeAdvancedManagementActivity.class
                );
            }

            // pass data
            intent.putExtra("displayName", account.getDisplayName());
            intent.putExtra("username", account.getUsername());

            startActivity(intent);

        } else {

            txt_message.setText(getString(R.string.login_failed));
            edt_pwd.setText("");
        }
    }
    public void loginSystemOld(View view) {

        String username =
                edt_username.getText().toString().trim();

        String pwd =
                edt_pwd.getText().toString().trim();

        if (username.isEmpty() || pwd.isEmpty()) {

            txt_message.setText(
                    getString(R.string.login_failed));

            return;
        }

        if (username.equalsIgnoreCase(DEFAULT_USERNAME)
                && pwd.equals(DEFAULT_PASSWORD)) {

            SharedPreferences preferences =
                    getSharedPreferences(
                            name_share_ref,
                            MODE_PRIVATE);

            SharedPreferences.Editor editor =
                    preferences.edit();

            editor.putString("Username", username);

            editor.putString("password", pwd);

            editor.putBoolean(
                    "SAVED",
                    chkSaveInfo.isChecked());

            editor.apply();

            txt_message.setText(
                    getString(R.string.login_success));

            Intent intent;

            if (rad_admin.isChecked()) {

                intent = new Intent(
                        LoginActivity.this,
                        EmployeeAdvancedManagementActivity.class);

            } else {

                intent = new Intent(
                        LoginActivity.this,
                        MainActivity.class);
            }

            startActivity(intent);

        } else {

            txt_message.setText(
                    getString(R.string.login_failed));
        }
    }

    public void exitSystem(View view) {

        AlertDialog.Builder builder =
                new AlertDialog.Builder(LoginActivity.this);

        builder.setTitle(
                getString(R.string.str_exit_title));

        builder.setMessage(
                getString(R.string.str_exit_message));

        builder.setIcon(
                android.R.drawable.ic_dialog_alert);

        builder.setPositiveButton(
                getString(R.string.str_yes),

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(
                            DialogInterface dialogInterface,
                            int i) {

                        finish();
                    }
                });

        builder.setNegativeButton(
                getString(R.string.str_no),

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(
                            DialogInterface dialogInterface,
                            int i) {

                        dialogInterface.cancel();
                    }
                });

        AlertDialog dialog = builder.create();

        dialog.setCanceledOnTouchOutside(false);

        dialog.show();
    }

    @Override
    protected void onResume() {

        super.onResume();

        SharedPreferences preferences =
                getSharedPreferences(
                        name_share_ref,
                        MODE_PRIVATE);

        String username =
                preferences.getString(
                        "Username",
                        "");

        String password =
                preferences.getString(
                        "password",
                        "");

        boolean saved =
                preferences.getBoolean(
                        "SAVED",
                        false);

        if (saved) {

            edt_username.setText(username);

            edt_pwd.setText(password);
        }
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(internetStateReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences preferences = getSharedPreferences(name_share_ref, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        
        String username = edt_username.getText().toString().trim();
        String pwd = edt_pwd.getText().toString().trim();
        boolean saved = chkSaveInfo.isChecked();
        
        editor.putString("Username", username);
        editor.putString("password", pwd);
        editor.putBoolean("SAVED", saved);
        editor.apply();
        try {
            unregisterReceiver(internetStateReceiver);
        } catch (IllegalArgumentException e) {
            Log.e("LoginActivity", "Receiver not registered", e);
        }
    }

    private void copyDatabase() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        try {
            dbHelper.copyDatabaseFromAssets();
            Log.d("LoginActivity", "Copy database thành công hoặc đã tồn tại.");
        } catch (IOException e) {
            Log.e("LoginActivity", "Lỗi sao chép database từ assets: " + e.getMessage());
        }
    }
}