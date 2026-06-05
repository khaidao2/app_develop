package com.example.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.k23411t_app.R;
import com.example.models.Category;

public class CategoryAdapter extends ArrayAdapter<Category> {
    private Activity context;
    private int resource;

    // Danh sách màu sắc khác nhau cho mỗi mục để các TextView của từng mục có màu khác nhau
    private static final int[] COLORS = {
        Color.parseColor("#E91E63"), // Pink
        Color.parseColor("#9C27B0"), // Purple
        Color.parseColor("#3F51B5"), // Indigo
        Color.parseColor("#009688"), // Teal
        Color.parseColor("#4CAF50"), // Green
        Color.parseColor("#FF9800"), // Orange
        Color.parseColor("#795548"), // Brown
        Color.parseColor("#E65100"), // Dark Orange
        Color.parseColor("#1B5E20"), // Dark Green
        Color.parseColor("#0D47A1")  // Dark Blue
    };

    public CategoryAdapter(@NonNull Activity context, int resource) {
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

        Category category = getItem(position);
        TextView txt_id = convertView.findViewById(R.id.txt_id);
        TextView txt_name = convertView.findViewById(R.id.txt_name);
        TextView txt_desc = convertView.findViewById(R.id.txt_desc);

        // Lấy tiền tố từ file strings.xml để không hardcode
        String idPrefix = context.getString(R.string.str_category_id_prefix);
        String namePrefix = context.getString(R.string.str_category_name_prefix);
        String descPrefix = context.getString(R.string.str_category_desc_prefix);

        if (category != null) {
            txt_id.setText(idPrefix + (category.getCategory() != null ? category.getCategory() : ""));
            txt_name.setText(namePrefix + (category.getCategoryName() != null ? category.getCategoryName() : ""));
            txt_desc.setText(descPrefix + (category.getDescription() != null ? category.getDescription() : ""));
        } else {
            txt_id.setText("");
            txt_name.setText("");
            txt_desc.setText("");
        }

        // Chọn 3 màu khác nhau cho 3 TextView trong cùng một mục dựa vào vị trí
        int colorId = COLORS[position % COLORS.length];
        int colorName = COLORS[(position + 1) % COLORS.length];
        int colorDesc = COLORS[(position + 2) % COLORS.length];
        
        txt_id.setTextColor(colorId);
        txt_name.setTextColor(colorName);
        txt_desc.setTextColor(colorDesc);

        return convertView;
    }
}
