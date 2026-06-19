package com.example.k23411t_app;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.adapters.ProductAdapter;
import com.example.models.DataWarehouse;
import com.example.models.Product;

import java.util.ArrayList;
import java.util.Random;

public class MainThreadObjectActivity extends AppCompatActivity {

    private TextView txtNumberOfProducts;
    private EditText edtNumProducts;
    private Button btnDownload;
    private TextView txtDownloadProgress;
    private ListView lvDownloadedProducts;
    private Button btnBack;
    private ProductAdapter productAdapter;

    private Thread downloadThread;
    private volatile boolean isRunning = false;

    private static final int MSG_UPDATE_PROGRESS = 1;
    private static final int MSG_DOWNLOAD_COMPLETE = 2;

    private final Handler handler_mainthread = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UPDATE_PROGRESS:
                    int progress = msg.arg1;
                    txtDownloadProgress.setText(getString(R.string.str_percent_format, progress));
                    if (msg.obj instanceof Product) {
                        Product product = (Product) msg.obj;
                        productAdapter.add(product);
                    }
                    break;
                case MSG_DOWNLOAD_COMPLETE:
                    txtDownloadProgress.setText(getString(R.string.str_percent_format, 100));
                    btnDownload.setEnabled(true);
                    edtNumProducts.setEnabled(true);
                    isRunning = false;
                    Toast.makeText(MainThreadObjectActivity.this, getString(R.string.str_download_completed), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_thread_object);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addViews();
        addEvents();
    }

    private void addViews() {
        txtNumberOfProducts = findViewById(R.id.txt_number_of_products);
        edtNumProducts = findViewById(R.id.edt_num_products);
        btnDownload = findViewById(R.id.btn_download);
        txtDownloadProgress = findViewById(R.id.txt_download_progress);
        lvDownloadedProducts = findViewById(R.id.lv_downloaded_products);
        btnBack = findViewById(R.id.btn_back);

        productAdapter = new ProductAdapter(this, R.layout.item_custom_product);
        lvDownloadedProducts.setAdapter(productAdapter);
    }

    private void addEvents() {
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDownloading();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        lvDownloadedProducts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Product product = productAdapter.getItem(position);
                if (product != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainThreadObjectActivity.this);
                    builder.setTitle(R.string.str_delete_product_confirm_title);
                    builder.setMessage(getString(R.string.str_delete_product_confirm_message, product.getProductName()));
                    builder.setPositiveButton(R.string.str_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            productAdapter.remove(product);
                            productAdapter.notifyDataSetChanged();
                            Toast.makeText(MainThreadObjectActivity.this, getString(R.string.str_deleted_product, product.getProductName()), Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton(R.string.str_no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
                return true;
            }
        });
    }

    private void startDownloading() {
        if (isRunning) return;

        String input = edtNumProducts.getText().toString().trim();
        if (TextUtils.isEmpty(input)) {
            Toast.makeText(this, getString(R.string.str_hint_number), Toast.LENGTH_SHORT).show();
            return;
        }

        final int totalItems;
        try {
            totalItems = Integer.parseInt(input);
            if (totalItems <= 0) {
                Toast.makeText(this, getString(R.string.str_hint_number), Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, getString(R.string.str_hint_number), Toast.LENGTH_SHORT).show();
            return;
        }

        isRunning = true;
        btnDownload.setEnabled(false);
        edtNumProducts.setEnabled(false);
        productAdapter.clear();
        txtDownloadProgress.setText(getString(R.string.str_percent_format, 0));

        final Random random = new Random();

        downloadThread = new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<Product> products = DataWarehouse.getProducts();
                for (int i = 0; i < totalItems && isRunning; i++) {
                    try {
                        // Sleep to simulate network download latency (500ms per item)
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        break;
                    }

                    int randomIndex = -1;
                    if (products != null && !products.isEmpty()) {
                        randomIndex = random.nextInt(products.size());
                    }

                    Product product = download_product(randomIndex);

                    int progress = ((i + 1) * 100) / totalItems;

                    Message msg = handler_mainthread.obtainMessage();
                    msg.what = MSG_UPDATE_PROGRESS;
                    msg.arg1 = progress;
                    msg.obj = product;
                    handler_mainthread.sendMessage(msg);
                }

                if (isRunning) {
                    Message completeMsg = handler_mainthread.obtainMessage();
                    completeMsg.what = MSG_DOWNLOAD_COMPLETE;
                    handler_mainthread.sendMessage(completeMsg);
                }
            }
        });

        downloadThread.start();
    }

    public Product download_product(int i) {
        ArrayList<Product> products = DataWarehouse.getProducts();
        if (products == null || i < 0 || i >= products.size()) {
            return null;
        }
        return products.get(i);
    }

    private void stopDownloadThread() {
        isRunning = false;
        if (downloadThread != null && downloadThread.isAlive()) {
            downloadThread.interrupt();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopDownloadThread();
    }
}
