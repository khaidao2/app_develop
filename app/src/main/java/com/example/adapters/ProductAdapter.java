package com.example.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.k23411t_app.R;
import com.example.models.Product;

import java.util.Locale;

public class ProductAdapter extends ArrayAdapter<Product> {
    private Activity context;
    private int resource;

    // Danh sách màu sắc khác nhau cho vòng tròn chữ cái đầu tiên và các textview
    private static final int[] COLORS = {
        Color.parseColor("#E91E63"), // Pink
        Color.parseColor("#9C27B0"), // Purple
        Color.parseColor("#3F51B5"), // Indigo
        Color.parseColor("#009688"), // Teal
        Color.parseColor("#4CAF50"), // Green
        Color.parseColor("#FF9800"), // Orange
        Color.parseColor("#795548"), // Brown
        Color.parseColor("#0D47A1")  // Dark Blue
    };

    public ProductAdapter(@NonNull Activity context, int resource) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(this.resource, parent, false);
        }

        Product product = getItem(position);
        TextView txt_initials = convertView.findViewById(R.id.txt_initials);
        TextView txt_name = convertView.findViewById(R.id.txt_name);
        TextView txt_id = convertView.findViewById(R.id.txt_id);
        TextView txt_desc = convertView.findViewById(R.id.txt_desc);
        TextView txt_qty = convertView.findViewById(R.id.txt_qty);
        TextView txt_coupon = convertView.findViewById(R.id.txt_coupon);
        TextView txt_vat = convertView.findViewById(R.id.txt_vat);
        TextView txt_price = convertView.findViewById(R.id.txt_price);

        // Lấy tiền tố từ file strings.xml để không hardcode
        String idPrefix = context.getString(R.string.str_category_id_prefix);
        String descPrefix = context.getString(R.string.str_category_desc_prefix);
        String qtyPrefix = context.getString(R.string.str_product_qty_prefix);
        String couponPrefix = context.getString(R.string.str_product_coupon_prefix);
        String vatPrefix = context.getString(R.string.str_product_vat_prefix);

        if (product != null) {
            // Thiết lập thông tin text
            String name = product.getProductName() != null ? product.getProductName() : "";
            txt_name.setText(name);
            txt_id.setText(idPrefix + (product.getProductID() != null ? product.getProductID() : ""));
            txt_desc.setText(descPrefix + (product.getDescription() != null ? product.getDescription() : ""));
            txt_price.setText(String.format(Locale.getDefault(), "$%,.2f", product.getPrice()));
            
            // Cài đặt số lượng
            txt_qty.setText(qtyPrefix + String.format(Locale.getDefault(), "%,.0f", product.getQuantity()));
            
            // Cài đặt Coupon
            String coupon = product.getCoupon();
            if (coupon != null && !coupon.isEmpty() && !coupon.equalsIgnoreCase("0%") && !coupon.equalsIgnoreCase("0")) {
                txt_coupon.setText(couponPrefix + coupon);
                txt_coupon.setVisibility(View.VISIBLE);
            } else {
                txt_coupon.setVisibility(View.GONE);
            }

            // Cài đặt VAT
            String vat = product.getVAT();
            if (vat != null && !vat.isEmpty()) {
                txt_vat.setText(vatPrefix + vat);
                txt_vat.setVisibility(View.VISIBLE);
            } else {
                txt_vat.setVisibility(View.GONE);
            }

            // Thiết lập chữ cái đầu tiên của sản phẩm
            if (!name.isEmpty()) {
                txt_initials.setText(name.substring(0, 1).toUpperCase());
            } else {
                txt_initials.setText("P");
            }
        } else {
            txt_initials.setText("");
            txt_name.setText("");
            txt_id.setText("");
            txt_desc.setText("");
            txt_price.setText("");
            txt_qty.setText("");
            txt_coupon.setVisibility(View.GONE);
            txt_vat.setVisibility(View.GONE);
        }

        // Chọn 3 màu sắc khác nhau cho các TextView chính của mục này để giao diện đẹp mắt
        int colorId = COLORS[position % COLORS.length];
        int colorName = COLORS[(position + 1) % COLORS.length];
        int colorPrice = COLORS[(position + 2) % COLORS.length];

        txt_id.setTextColor(colorId);
        txt_name.setTextColor(colorName);
        txt_price.setTextColor(colorPrice);

        // Đổi màu nền vòng tròn chữ cái đầu tiên theo vị trí
        GradientDrawable bg = (GradientDrawable) txt_initials.getBackground();
        if (bg != null) {
            bg.setColor(colorName);
        }

        return convertView;
    }
}
