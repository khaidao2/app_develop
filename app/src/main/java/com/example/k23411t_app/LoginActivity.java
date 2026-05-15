package com.example.k23411t_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    EditText edt_username;
    EditText edt_pwd;

    TextView txt_message;

    CheckBox chkSaveInfo;

    RadioButton rad_admin, rad_user;

    String name_share_ref;

    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_PASSWORD = "123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_login);

        addViews();

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
    }

    public void loginSystem(View view) {

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
    }
}