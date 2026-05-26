package com.example.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.k23411t_app.R;
import com.example.models.Employee;

public class EmployeeAdapter extends ArrayAdapter<Employee> {
    Activity context;
    int resource;
    public EmployeeAdapter(@NonNull Activity context, int resource) {
        super(context, resource);
        this.context=context;
        this.resource=resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater=context.getLayoutInflater();
            convertView=inflater.inflate(this.resource,parent,false);
        }
        Employee emp = getItem(position);
        TextView txt_id=convertView.findViewById(R.id.txt_id);
        TextView txt_name=convertView.findViewById(R.id.txt_name);
        TextView txt_phone=convertView.findViewById(R.id.txt_phone);

        if (emp != null) {
            txt_id.setText(emp.getId() != null ? emp.getId() : "");
            txt_name.setText(emp.getName() != null ? emp.getName() : "");
            txt_phone.setText(emp.getPhone() != null ? emp.getPhone() : "");
        } else {
            txt_id.setText("");
            txt_name.setText("");
            txt_phone.setText("");
        }
        ImageView img_call=convertView.findViewById(R.id.img_call);
        ImageView img_sms=convertView.findViewById(R.id.img_sms);
        img_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (emp != null && emp.getPhone() != null) {
                    Intent intentCall = new Intent(Intent.ACTION_DIAL);
                    Uri uri = Uri.parse("tel:" + emp.getPhone());
                    intentCall.setData(uri);
                    context.startActivity(intentCall);
                }
            }
        });

        return convertView;
    }
}
