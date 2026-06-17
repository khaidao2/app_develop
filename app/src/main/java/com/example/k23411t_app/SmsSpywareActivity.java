package com.example.k23411t_app;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SmsSpywareActivity extends AppCompatActivity {

    private static final int REQUEST_SMS_PERMISSIONS = 456;
    private static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";

    private ListView lv_sms;
    private SimpleAdapter adapter;
    private ArrayList<Map<String, String>> smsList = new ArrayList<>();

    private final BroadcastReceiver smsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (SMS_RECEIVED_ACTION.equals(intent.getAction())) {
                // Tải lại danh sách tin nhắn khi có tin nhắn mới gửi tới
                loadSms();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sms_spyware);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        lv_sms = findViewById(R.id.lv_sms);

        // Khởi tạo adapter trống trước
        adapter = new SimpleAdapter(
                this,
                smsList,
                R.layout.item_sms,
                new String[]{"phone", "date", "body"},
                new int[]{R.id.txt_sms_phone, R.id.txt_sms_date, R.id.txt_sms_body}
        );
        lv_sms.setAdapter(adapter);

        checkPermissionsAndLoadSms();
    }

    private void checkPermissionsAndLoadSms() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS},
                    REQUEST_SMS_PERMISSIONS
            );
        } else {
            loadSms();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_SMS_PERMISSIONS) {
            boolean granted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    granted = false;
                    break;
                }
            }
            if (granted) {
                loadSms();
            } else {
                Toast.makeText(this, getString(R.string.str_sms_spyware_permission_denied), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void loadSms() {
        smsList.clear();
        ContentResolver cr = getContentResolver();
        
        Cursor cursor = cr.query(
                Uri.parse("content://sms/inbox"),
                new String[]{"address", "date", "body"},
                null,
                null,
                "date DESC"
        );

        if (cursor != null) {
            int addressIndex = cursor.getColumnIndex("address");
            int dateIndex = cursor.getColumnIndex("date");
            int bodyIndex = cursor.getColumnIndex("body");

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

            while (cursor.moveToNext()) {
                String phone = addressIndex != -1 ? cursor.getString(addressIndex) : "Unknown";
                long dateMillis = dateIndex != -1 ? cursor.getLong(dateIndex) : 0;
                String body = bodyIndex != -1 ? cursor.getString(bodyIndex) : "";

                String formattedDate = dateMillis != 0 ? sdf.format(new Date(dateMillis)) : "";

                HashMap<String, String> smsMap = new HashMap<>();
                smsMap.put("phone", phone);
                smsMap.put("date", formattedDate);
                smsMap.put("body", body);
                
                smsList.add(smsMap);
            }
            cursor.close();
        }

        if (smsList.isEmpty()) {
            Toast.makeText(this, getString(R.string.str_sms_spyware_no_messages), Toast.LENGTH_SHORT).show();
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(smsReceiver, new IntentFilter(SMS_RECEIVED_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(smsReceiver);
        } catch (IllegalArgumentException e) {
            // Receiver was already unregistered
        }
    }

    public void closeActivity(View view) {
        finish();
    }
}
