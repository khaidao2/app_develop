package com.example.k23411t_app;

import android.os.Bundle;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Locale;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.models.DataWarehouse;
import com.example.models.Order;
import com.example.models.OrderStatus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class OrderMangementActivity extends AppCompatActivity {

    EditText edt_from_date, edt_to_date;
    ImageView img_from_date, img_to_date, img_filter, img_clear_filter;
    ListView lv_results;

    ArrayList<Order> orderList;
    OrderAdapter adapter;

    Calendar cal, todate;
    SimpleDateFormat sdf;
    DatePickerDialog.OnDateSetListener fromDateListener, toDateListener;

    SharedPreferences preferences;
    String name_share_ref;
    OrderStatus currentStatusFilter = OrderStatus.ALL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_mangement);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addViews();
        name_share_ref = "ORDER_FILTER_PREFS";
        preferences = getSharedPreferences(name_share_ref, MODE_PRIVATE);
        restoreSelectionState();
        addEvents();
    }

    private void addViews() {
        edt_from_date = findViewById(R.id.edt_from_date);
        edt_to_date = findViewById(R.id.edt_to_date);
        img_from_date = findViewById(R.id.img_from_date);
        img_to_date = findViewById(R.id.img_to_date);
        img_filter = findViewById(R.id.img_filter);
        img_clear_filter = findViewById(R.id.img_clear_filter);
        lv_results = findViewById(R.id.lv_results);

        cal = Calendar.getInstance();
        todate = Calendar.getInstance();
        sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        fromDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                edt_from_date.setText(sdf.format(cal.getTime()));
                saveSelectionState();
                filter();
            }
        };

        toDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
                todate.set(Calendar.YEAR, year);
                todate.set(Calendar.MONTH, month);
                todate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                edt_to_date.setText(sdf.format(todate.getTime()));
                saveSelectionState();
                filter();
            }
        };

        orderList = new ArrayList<>();
        orderList.addAll(DataWarehouse.getOrders());
        adapter = new OrderAdapter(
                this,
                R.layout.item_order,
                orderList
        );
        lv_results.setAdapter(adapter);
    }

    public double calculateTotal() {
        double sum = 0.0;
        for (Order order : orderList) {
            sum += order.getOrderSum();
        }
        return sum;
    }

    private void saveSelectionState() {
        if (preferences == null) return;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("FROM_DATE", edt_from_date.getText().toString());
        editor.putString("TO_DATE", edt_to_date.getText().toString());
        editor.putLong("FROM_TIME", cal.getTimeInMillis());
        editor.putLong("TO_TIME", todate.getTimeInMillis());
        editor.putString("STATUS_FILTER", currentStatusFilter.name());
        editor.apply();
    }

    private void restoreSelectionState() {
        if (preferences == null) return;
        String fromDate = preferences.getString("FROM_DATE", "");
        String toDate = preferences.getString("TO_DATE", "");
        long fromTime = preferences.getLong("FROM_TIME", -1);
        long toTime = preferences.getLong("TO_TIME", -1);
        String statusName = preferences.getString("STATUS_FILTER", OrderStatus.ALL.name());

        try {
            currentStatusFilter = OrderStatus.valueOf(statusName);
        } catch (Exception e) {
            currentStatusFilter = OrderStatus.ALL;
        }

        if (!fromDate.isEmpty()) {
            edt_from_date.setText(fromDate);
            if (fromTime != -1) {
                cal.setTimeInMillis(fromTime);
            }
        }
        if (!toDate.isEmpty()) {
            edt_to_date.setText(toDate);
            if (toTime != -1) {
                todate.setTimeInMillis(toTime);
            }
        }

        // Auto filter if states were restored
        filter();
    }

    private void addEvents() {
        View.OnClickListener showFromDatePicker = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(
                        OrderMangementActivity.this,
                        fromDateListener,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        };
        img_from_date.setOnClickListener(showFromDatePicker);
        edt_from_date.setOnClickListener(showFromDatePicker);

        View.OnClickListener showToDatePicker = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(
                        OrderMangementActivity.this,
                        toDateListener,
                        todate.get(Calendar.YEAR),
                        todate.get(Calendar.MONTH),
                        todate.get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        };
        img_to_date.setOnClickListener(showToDatePicker);
        edt_to_date.setOnClickListener(showToDatePicker);

        img_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter();
            }
        });

        img_clear_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFilter();
            }
        });

        lv_results.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Order selectedOrder = adapter.getItem(position);
                showOrderDetailDialog(selectedOrder);
            }
        });
    }

    private void filter() {
        String fromDateStr = edt_from_date.getText().toString().trim();
        String toDateStr = edt_to_date.getText().toString().trim();

        Calendar startCal = null;
        Calendar endCal = null;

        if (!fromDateStr.isEmpty() && !toDateStr.isEmpty()) {
            startCal = Calendar.getInstance();
            startCal.setTime(cal.getTime());
            startCal.set(Calendar.HOUR_OF_DAY, 0);
            startCal.set(Calendar.MINUTE, 0);
            startCal.set(Calendar.SECOND, 0);
            startCal.set(Calendar.MILLISECOND, 0);

            endCal = Calendar.getInstance();
            endCal.setTime(todate.getTime());
            endCal.set(Calendar.HOUR_OF_DAY, 23);
            endCal.set(Calendar.MINUTE, 59);
            endCal.set(Calendar.SECOND, 59);
            endCal.set(Calendar.MILLISECOND, 999);

            if (startCal.after(endCal)) {
                Toast.makeText(this, "From Date must be before or equal to To Date", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        ArrayList<Order> allOrders = DataWarehouse.getOrders();
        orderList.clear();
        for (Order order : allOrders) {
            boolean matchesDate = true;
            if (startCal != null && endCal != null) {
                Date orderDate = order.getOrderDate();
                if (orderDate == null || orderDate.before(startCal.getTime()) || orderDate.after(endCal.getTime())) {
                    matchesDate = false;
                }
            }

            boolean matchesStatus = true;
            if (currentStatusFilter != OrderStatus.ALL) {
                if (order.getOrderStatus() != currentStatusFilter) {
                    matchesStatus = false;
                }
            }

            if (matchesDate && matchesStatus) {
                orderList.add(order);
            }
        }
        adapter.notifyDataSetChanged();

        String msg = "Filter: " + currentStatusFilter.name();
        if (startCal != null && endCal != null) {
            msg += " | Dates: " + fromDateStr + " - " + toDateStr;
        }
        Toast.makeText(this, msg + "\nFound " + orderList.size() + " orders. Total sum: " + calculateTotal(), Toast.LENGTH_LONG).show();
    }

    private void clearFilter() {
        edt_from_date.setText("");
        edt_to_date.setText("");

        cal = Calendar.getInstance();
        todate = Calendar.getInstance();
        currentStatusFilter = OrderStatus.ALL;

        orderList.clear();
        orderList.addAll(DataWarehouse.getOrders());
        adapter.notifyDataSetChanged();

        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove("FROM_DATE");
            editor.remove("TO_DATE");
            editor.remove("FROM_TIME");
            editor.remove("TO_TIME");
            editor.remove("STATUS_FILTER");
            editor.apply();
        }

        Toast.makeText(this, "Filters cleared", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveSelectionState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.order_menu_status, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.str_order_status_all) {
            currentStatusFilter = OrderStatus.ALL;
            filter();
            return true;
        } else if (id == R.id.str_order_status_completed) {
            currentStatusFilter = OrderStatus.COMPLETED;
            filter();
            return true;
        } else if (id == R.id.str_order_status_not_payment) {
            currentStatusFilter = OrderStatus.NOT_PAYMENT;
            filter();
            return true;
        } else if (id == R.id.str_order_status_ongoing_logistics) {
            currentStatusFilter = OrderStatus.ONGOING_LOGISTICS;
            filter();
            return true;
        } else if (id == R.id.str_order_status_customer_complain) {
            currentStatusFilter = OrderStatus.CUSTOMER_COMPLAIN;
            filter();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showOrderDetailDialog(Order order) {
        if (order == null) return;

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_order_detail, null);
        builder.setView(dialogView);

        TextView txtOrderId = dialogView.findViewById(R.id.dialog_txt_order_id);
        TextView txtOrderDate = dialogView.findViewById(R.id.dialog_txt_order_date);
        TextView txtOrderStatus = dialogView.findViewById(R.id.dialog_txt_order_status);
        TextView txtCustomerName = dialogView.findViewById(R.id.dialog_txt_customer_name);
        TextView txtEmployeeName = dialogView.findViewById(R.id.dialog_txt_employee_name);
        TextView txtProducts = dialogView.findViewById(R.id.dialog_txt_products);
        TextView txtOrderSum = dialogView.findViewById(R.id.dialog_txt_order_sum);
        com.google.android.material.button.MaterialButton btnClose = dialogView.findViewById(R.id.dialog_btn_close);

        txtOrderId.setText(order.getOrderID());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        if (order.getOrderDate() != null) {
            txtOrderDate.setText(dateFormat.format(order.getOrderDate()));
        } else {
            txtOrderDate.setText("N/A");
        }

        txtOrderSum.setText(String.format(Locale.getDefault(), "$%,.2f", order.getOrderSum()));

        OrderStatus status = order.getOrderStatus();
        if (status == null) status = OrderStatus.ALL;

        int statusResId;
        switch (status) {
            case ALL: statusResId = R.string.str_order_status_all; break;
            case COMPLETED: statusResId = R.string.str_order_status_completed; break;
            case NOT_PAYMENT: statusResId = R.string.str_order_status_not_payment; break;
            case ONGOING_LOGISTICS: statusResId = R.string.str_order_status_ongoing_logistics; break;
            case CUSTOMER_COMPLAIN: statusResId = R.string.str_order_status_customer_complain; break;
            default: statusResId = -1;
        }
        if (statusResId != -1) {
            txtOrderStatus.setText(getString(statusResId));
        } else {
            txtOrderStatus.setText(status.name());
        }

        int bgCol, txtCol;
        switch (status) {
            case COMPLETED:
                bgCol = getResources().getColor(R.color.status_completed_bg, null);
                txtCol = getResources().getColor(R.color.status_completed_txt, null);
                break;
            case NOT_PAYMENT:
                bgCol = getResources().getColor(R.color.status_not_paid_bg, null);
                txtCol = getResources().getColor(R.color.status_not_paid_txt, null);
                break;
            case ONGOING_LOGISTICS:
                bgCol = getResources().getColor(R.color.status_logistics_bg, null);
                txtCol = getResources().getColor(R.color.status_logistics_txt, null);
                break;
            case CUSTOMER_COMPLAIN:
                bgCol = getResources().getColor(R.color.status_complain_bg, null);
                txtCol = getResources().getColor(R.color.status_complain_txt, null);
                break;
            default:
                bgCol = getResources().getColor(R.color.gray, null);
                txtCol = getResources().getColor(R.color.black, null);
                break;
        }
        android.graphics.drawable.GradientDrawable shape = new android.graphics.drawable.GradientDrawable();
        float density = getResources().getDisplayMetrics().density;
        shape.setCornerRadius(12 * density);
        shape.setColor(bgCol);
        txtOrderStatus.setBackground(shape);
        txtOrderStatus.setTextColor(txtCol);

        String customerName = "N/A";
        ArrayList<com.example.models.Customer> customers = DataWarehouse.getCustomers();
        if (customers != null) {
            for (com.example.models.Customer c : customers) {
                if (c.getCusID() != null && c.getCusID().equals(order.getCustomerID())) {
                    customerName = c.getCusName();
                    break;
                }
            }
        }
        txtCustomerName.setText(customerName);

        String employeeName = "N/A";
        ArrayList<com.example.models.Employee> employees = DataWarehouse.getEmployee();
        if (employees != null) {
            for (com.example.models.Employee e : employees) {
                if (e.getId() != null && e.getId().equals(order.getEmployeeID())) {
                    employeeName = e.getName();
                    break;
                }
            }
        }
        txtEmployeeName.setText(employeeName);

        StringBuilder itemsSummary = new StringBuilder();
        ArrayList<com.example.models.OrderDetail> details = DataWarehouse.getOrderDetail();
        ArrayList<com.example.models.Product> products = DataWarehouse.getProducts();
        if (details != null && products != null) {
            for (com.example.models.OrderDetail detail : details) {
                if (detail.getOrderID() != null && detail.getOrderID().equals(order.getOrderID())) {
                    String prodName = "Product";
                    for (com.example.models.Product p : products) {
                        if (p.getProductID() != null && p.getProductID().equals(detail.getProductID())) {
                            prodName = p.getProductName();
                            break;
                        }
                    }
                    if (itemsSummary.length() > 0) {
                        itemsSummary.append("\n");
                    }
                    itemsSummary.append("• ").append(prodName).append(" (x").append(detail.getQuantity()).append(")");
                }
            }
        }
        if (itemsSummary.length() == 0) {
            itemsSummary.append("Không có sản phẩm");
        }
        txtProducts.setText(itemsSummary.toString());

        final androidx.appcompat.app.AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}