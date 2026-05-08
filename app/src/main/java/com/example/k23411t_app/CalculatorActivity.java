package com.example.k23411t_app;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CalculatorActivity extends AppCompatActivity {
    EditText edt_input;
    TextView txt_mr, txt_mc, txt_ms, txt_mplus, txt_mminus,txt_extra;
    View.OnClickListener n_click_listener;
    Button btn_del, btn_equal;
    String ongoing_formula="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calculator);
        addViews();
        addEvents();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void addEvents() {
        btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String formular = edt_input.getText().toString();
                String new_formular = "";
                if (formular.length()>1)
                {
                    new_formular=formular.substring(0,formular.length()-1);
                }
                edt_input.setText(new_formular);
            }
        });
        btn_equal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String formular = edt_input.getText().toString();

                try {
                    formular = formular.replaceAll("x", "*").replaceAll("X", "*");
                    Expression expression = new ExpressionBuilder(formular).build();
                    double result = expression.evaluate();

                    edt_input.setText(String.valueOf(result));

                } catch (Exception e) {
                    edt_input.setText("Error");
                }
            }
        });
        n_click_listener= new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.equals(txt_mr)) {
                    Toast.makeText(CalculatorActivity.this, "MR clicked", Toast.LENGTH_SHORT).show();
                }
                else if (view.equals(txt_mc)) {
                    Toast.makeText(CalculatorActivity.this, "MC clicked", Toast.LENGTH_SHORT).show();
                }
                else if (view.equals(txt_ms)) {
                    Toast.makeText(CalculatorActivity.this, "MS clicked", Toast.LENGTH_SHORT).show();
                }
                else if (view.equals(txt_mplus)) {
                    Toast.makeText(CalculatorActivity.this, "M+ clicked", Toast.LENGTH_SHORT).show();
                }
                else if (view.equals(txt_mminus)) {
                    Toast.makeText(CalculatorActivity.this, "M- clicked", Toast.LENGTH_SHORT).show();
                }
                else if (view.equals(txt_extra)) {
                    Toast.makeText(CalculatorActivity.this, "M clicked", Toast.LENGTH_SHORT).show();
                }
            }

        };
        txt_mr.setOnClickListener(n_click_listener);
        txt_mc.setOnClickListener(n_click_listener);
        txt_ms.setOnClickListener(n_click_listener);
        txt_mplus.setOnClickListener(n_click_listener);
        txt_mminus.setOnClickListener(n_click_listener);
    }

    public void processInputData(View view) {
        Button btn = (Button) view;
        String new_value=btn.getText().toString();
        String current_value = edt_input.getText().toString();
        String last_value=current_value+new_value;
        edt_input.setText(last_value);
    }
    private void addViews(){
        edt_input=findViewById(R.id.edt_input);
        btn_del=findViewById(R.id.btn_del);
        btn_equal=findViewById(R.id.btn_equal);
        txt_mr = findViewById(R.id.txt_mr);
        txt_mc = findViewById(R.id.txt_mc);
        txt_ms = findViewById(R.id.txt_ms);
        txt_mplus = findViewById(R.id.txt_mplus);
        txt_mminus = findViewById(R.id.txt_mminus);
        txt_extra=findViewById(R.id.txt_extra);
    }
    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences preferences =
                getSharedPreferences("CalculatorPrefs", MODE_PRIVATE);

        String savedFormula = preferences.getString("formula", "");

        edt_input.setText(savedFormula);
    }
    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences preferences =
                getSharedPreferences("CalculatorPrefs", MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("formula", edt_input.getText().toString());

        editor.apply();
    }
}