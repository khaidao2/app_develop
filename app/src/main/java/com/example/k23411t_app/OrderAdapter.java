package com.example.k23411t_app;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.models.Customer;
import com.example.models.DataWarehouse;
import com.example.models.Employee;
import com.example.models.Order;
import com.example.models.OrderDetail;
import com.example.models.OrderStatus;
import com.example.models.Product;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends ArrayAdapter<Order> {

    private final int resourceLayout;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public OrderAdapter(@NonNull Context context, int resource, @NonNull List<Order> objects) {
        super(context, resource, objects);
        this.resourceLayout = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(resourceLayout, parent, false);
        }

        Order order = getItem(position);
        if (order != null) {
            TextView txtOrderId = convertView.findViewById(R.id.txt_item_order_id);
            TextView txtOrderDate = convertView.findViewById(R.id.txt_item_order_date);
            TextView txtOrderDetails = convertView.findViewById(R.id.txt_item_order_details);
            TextView txtOrderSum = convertView.findViewById(R.id.txt_item_order_sum);
            TextView txtOrderStatus = convertView.findViewById(R.id.txt_item_order_status);

            // 1. Mã đơn hàng (Blue)
            txtOrderId.setText(order.getOrderID());

            // 2. Thời gian tạo đơn hàng
            if (order.getOrderDate() != null) {
                txtOrderDate.setText(dateFormat.format(order.getOrderDate()));
            } else {
                txtOrderDate.setText("N/A");
            }

            // 3. Nội dung đơn hàng (Tím) - Tra cứu Tên khách hàng, Nhân viên, và Chi tiết sản phẩm
            String customerName = "N/A";
            ArrayList<Customer> customers = DataWarehouse.getCustomers();
            if (customers != null) {
                for (Customer c : customers) {
                    if (c.getCusID() != null && c.getCusID().equals(order.getCustomerID())) {
                        customerName = c.getCusName();
                        break;
                    }
                }
            }

            String employeeName = "N/A";
            ArrayList<Employee> employees = DataWarehouse.getEmployee();
            if (employees != null) {
                for (Employee e : employees) {
                    if (e.getId() != null && e.getId().equals(order.getEmployeeID())) {
                        employeeName = e.getName();
                        break;
                    }
                }
            }

            StringBuilder itemsSummary = new StringBuilder();
            ArrayList<OrderDetail> details = DataWarehouse.getOrderDetail();
            ArrayList<Product> products = DataWarehouse.getProducts();
            if (details != null && products != null) {
                for (OrderDetail detail : details) {
                    if (detail.getOrderID() != null && detail.getOrderID().equals(order.getOrderID())) {
                        String prodName = "Product";
                        for (Product p : products) {
                            if (p.getProductID() != null && p.getProductID().equals(detail.getProductID())) {
                                prodName = p.getProductName();
                                break;
                            }
                        }
                        if (itemsSummary.length() > 0) {
                            itemsSummary.append(", ");
                        }
                        itemsSummary.append(prodName).append(" (x").append(detail.getQuantity()).append(")");
                    }
                }
            }
            if (itemsSummary.length() == 0) {
                itemsSummary.append("Không có sản phẩm");
            }

            String contentText = "Khách hàng: " + customerName + "\nNhân viên bán: " + employeeName + "\nChi tiết: " + itemsSummary.toString();
            txtOrderDetails.setText(contentText);

            // 4. Số tiền (Red)
            txtOrderSum.setText(String.format(Locale.getDefault(), "$%,.2f", order.getOrderSum()));

            // 5. Trạng thái (Đổi màu badge động theo nội dung)
            OrderStatus status = order.getOrderStatus();
            if (status == null) {
                status = OrderStatus.ALL;
            }
            txtOrderStatus.setText(getStatusDisplayName(status));

            // Cấu hình màu sắc nền và chữ của badge theo trạng thái
            int bgCol, txtCol;
            switch (status) {
                case COMPLETED:
                    bgCol = getContext().getResources().getColor(R.color.status_completed_bg, null);
                    txtCol = getContext().getResources().getColor(R.color.status_completed_txt, null);
                    break;
                case NOT_PAYMENT:
                    bgCol = getContext().getResources().getColor(R.color.status_not_paid_bg, null);
                    txtCol = getContext().getResources().getColor(R.color.status_not_paid_txt, null);
                    break;
                case ONGOING_LOGISTICS:
                    bgCol = getContext().getResources().getColor(R.color.status_logistics_bg, null);
                    txtCol = getContext().getResources().getColor(R.color.status_logistics_txt, null);
                    break;
                case CUSTOMER_COMPLAIN:
                    bgCol = getContext().getResources().getColor(R.color.status_complain_bg, null);
                    txtCol = getContext().getResources().getColor(R.color.status_complain_txt, null);
                    break;
                default:
                    bgCol = getContext().getResources().getColor(R.color.gray, null);
                    txtCol = getContext().getResources().getColor(R.color.black, null);
                    break;
            }

            // Tạo hình dạng badge bo góc động
            GradientDrawable shape = new GradientDrawable();
            float density = getContext().getResources().getDisplayMetrics().density;
            int radius = (int) (12 * density); // Bo góc 12dp
            shape.setCornerRadius(radius);
            shape.setColor(bgCol);

            txtOrderStatus.setBackground(shape);
            txtOrderStatus.setTextColor(txtCol);
        }

        return convertView;
    }

    private String getStatusDisplayName(OrderStatus status) {
        if (status == null) return "N/A";
        int resId;
        switch (status) {
            case ALL:
                resId = R.string.str_order_status_all;
                break;
            case COMPLETED:
                resId = R.string.str_order_status_completed;
                break;
            case NOT_PAYMENT:
                resId = R.string.str_order_status_not_payment;
                break;
            case ONGOING_LOGISTICS:
                resId = R.string.str_order_status_ongoing_logistics;
                break;
            case CUSTOMER_COMPLAIN:
                resId = R.string.str_order_status_customer_complain;
                break;
            default:
                return status.name();
        }
        return getContext().getString(resId);
    }
}
