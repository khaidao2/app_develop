package com.example.k23411t_app;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MultiThreadActivity extends AppCompatActivity {

    private EditText edtNumButtons;
    private TextView txtPercent;
    private Button btnCreate, btnReset;
    private LinearLayout layoutButtonContainer;

    private Thread backgroundThread;
    private volatile boolean isRunning = false;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_multi_thread);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
    }

    private void initViews() {
        edtNumButtons = findViewById(R.id.edt_num_buttons);
        txtPercent = findViewById(R.id.txt_percent);
        btnCreate = findViewById(R.id.btn_create);
        btnReset = findViewById(R.id.btn_reset);
        layoutButtonContainer = findViewById(R.id.layout_button_container);
    }

    public void startCreatingButtons(View view) {
        if (isRunning) return;

        String input = edtNumButtons.getText().toString().trim();
        if (TextUtils.isEmpty(input)) {
            Toast.makeText(this, getString(R.string.str_hint_number), Toast.LENGTH_SHORT).show();
            return;
        }

        final int numButtons;
        try {
            numButtons = Integer.parseInt(input);
            if (numButtons <= 0) {
                Toast.makeText(this, getString(R.string.str_hint_number), Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, getString(R.string.str_hint_number), Toast.LENGTH_SHORT).show();
            return;
        }

        isRunning = true;
        btnCreate.setEnabled(false);
        btnReset.setEnabled(false);
        layoutButtonContainer.removeAllViews();
        txtPercent.setText(getString(R.string.str_percent_label, "0%"));

        backgroundThread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i <= numButtons && isRunning; i++) {
                    final int buttonNumber = i;
                    final int progressPercent = (i * 100) / numButtons;

                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            // Create button dynamically
                            Button dynamicBtn = new Button(MultiThreadActivity.this);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );
                            // Set margins around the buttons
                            int marginInPx = (int) (8 * getResources().getDisplayMetrics().density);
                            params.setMargins(0, marginInPx, 0, marginInPx);
                            dynamicBtn.setLayoutParams(params);
                            dynamicBtn.setText("Button " + buttonNumber);
                            dynamicBtn.setAllCaps(false);

                            // Optional: Click action for dynamic button
                            dynamicBtn.setOnClickListener(v -> {
                                Toast.makeText(MultiThreadActivity.this, "Clicked Button " + buttonNumber, Toast.LENGTH_SHORT).show();
                            });

                            // Add to vertical layout inside ScrollView
                            layoutButtonContainer.addView(dynamicBtn);

                            // Update percentage label
                            txtPercent.setText(getString(R.string.str_percent_label, progressPercent + "%"));
                        }
                    });

                    try {
                        // Delay of 150ms per button to visualize creation flow
                        Thread.sleep(150);
                    } catch (InterruptedException e) {
                        break;
                    }
                }

                // Restore button states
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        isRunning = false;
                        btnCreate.setEnabled(true);
                        btnReset.setEnabled(true);
                    }
                });
            }
        });
        backgroundThread.start();
    }

    public void resetContainer(View view) {
        isRunning = false;
        stopBackgroundThread();

        layoutButtonContainer.removeAllViews();
        txtPercent.setText(getString(R.string.str_percent_label, "0%"));
        edtNumButtons.setText("");
        btnCreate.setEnabled(true);
        btnReset.setEnabled(true);
    }

    private void stopBackgroundThread() {
        if (backgroundThread != null && backgroundThread.isAlive()) {
            backgroundThread.interrupt();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRunning = false;
        stopBackgroundThread();
    }

    public void closeActivity(View view) {
        finish();
    }
}
