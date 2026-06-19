package com.example.k23411t_app;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.models.Subject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

/**
 * Hiển thị danh sách môn học của một ngành (nhận qua intent extra
 * {@link #EXTRA_MAJOR_NAME}). Dữ liệu được đọc từ SQLite ({@link SubjectDbHelper}),
 * vốn được nạp một lần từ {@code assets/chinhquy_courses.json}.
 *
 * <p>Ô tìm kiếm dùng {@link EuclideanSearch} để vừa lọc vừa sắp xếp lại danh sách
 * theo thời gian thực: môn khớp nhất (khoảng cách Euclid nhỏ nhất) lên đầu, và
 * huy hiệu khoảng cách hiện ra bên phải mỗi môn trong lúc tìm kiếm.
 */
public class SubjectActivity extends AppCompatActivity {

    public static final String EXTRA_MAJOR_NAME = "extra_major_name";

    // Chỉ giữ các môn có khoảng cách <= độ dài từ khoá + biên này.
    private static final double DISTANCE_SLACK = 2.0;

    private SearchView searchView;
    private ListView lvSubjects;
    private TextView txtCount;
    private TextView txtEmpty;

    private SubjectAdapter adapter;

    private ArrayList<Subject> fullList = new ArrayList<>();   // toàn bộ môn của ngành
    private final ArrayList<Subject> displayList = new ArrayList<>(); // môn đang hiển thị
    private boolean searching = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_subject);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String majorName = getIntent().getStringExtra(EXTRA_MAJOR_NAME);
        if (TextUtils.isEmpty(majorName)) {
            majorName = "";
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(majorName.isEmpty()
                    ? getString(R.string.str_subject_list_title) : majorName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        searchView = findViewById(R.id.search_view);
        lvSubjects = findViewById(R.id.lv_subjects);
        txtCount = findViewById(R.id.txt_subject_count);
        txtEmpty = findViewById(R.id.txt_empty);

        loadSubjects(majorName);

        adapter = new SubjectAdapter();
        lvSubjects.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                applySearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                applySearch(newText);
                return true;
            }
        });
    }

    private void loadSubjects(String majorName) {
        SubjectDbHelper db = new SubjectDbHelper(this);
        fullList = db.getSubjectsByMajor(majorName);
        displayList.clear();
        displayList.addAll(fullList);
        updateCountLabel();
    }

    /**
     * Lọc và sắp xếp lại danh sách theo khoảng cách Euclid với từ khoá.
     */
    private void applySearch(String query) {
        String q = query == null ? "" : query.trim();
        displayList.clear();

        if (q.isEmpty()) {
            searching = false;
            displayList.addAll(fullList);
        } else {
            searching = true;
            double threshold = q.replaceAll("\\s+", "").length() + DISTANCE_SLACK;
            for (Subject s : fullList) {
                // So khớp cả tên môn lẫn mã môn, lấy khoảng cách nhỏ hơn.
                double dName = EuclideanSearch.distance(q, s.getName());
                double dCode = EuclideanSearch.distance(q, s.getCode());
                double d = Math.min(dName, dCode);
                s.setDistance(d);
                if (d <= threshold) {
                    displayList.add(s);
                }
            }
            Collections.sort(displayList, new Comparator<Subject>() {
                @Override
                public int compare(Subject a, Subject b) {
                    return Double.compare(a.getDistance(), b.getDistance());
                }
            });
        }

        adapter.notifyDataSetChanged();
        updateCountLabel();
    }

    private void updateCountLabel() {
        txtCount.setText(getString(R.string.str_subject_count_format, displayList.size()));
        boolean empty = displayList.isEmpty();
        txtEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        lvSubjects.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Adapter tuỳ biến hiển thị mỗi môn học: tên + mã, số tín chỉ + loại môn, và
     * huy hiệu khoảng cách Euclid (chỉ hiện khi đang tìm kiếm).
     */
    private class SubjectAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return displayList.size();
        }

        @Override
        public Object getItem(int position) {
            return displayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(SubjectActivity.this)
                        .inflate(R.layout.item_subject, parent, false);
            }
            Subject s = displayList.get(position);

            TextView txtName = convertView.findViewById(R.id.txt_subject_name);
            TextView txtCode = convertView.findViewById(R.id.txt_subject_code);
            TextView txtCredits = convertView.findViewById(R.id.txt_subject_credits);
            TextView txtType = convertView.findViewById(R.id.txt_subject_type);
            View layoutDistance = convertView.findViewById(R.id.layout_distance);
            TextView txtDistance = convertView.findViewById(R.id.txt_distance_value);

            txtName.setText(s.getName());
            txtCode.setText(getString(R.string.str_subject_code_prefix, s.getCode()));
            txtCredits.setText(getString(R.string.str_subject_credits_format, formatCredits(s.getCredits())));

            String typeLabel = s.isCompulsory()
                    ? getString(R.string.str_subject_compulsory)
                    : getString(R.string.str_subject_elective);
            txtType.setText(typeLabel);

            if (searching) {
                layoutDistance.setVisibility(View.VISIBLE);
                txtDistance.setText(String.format(Locale.US, "%.2f", s.getDistance()));
            } else {
                layoutDistance.setVisibility(View.GONE);
            }

            return convertView;
        }
    }

    /**
     * Bỏ phần thập phân thừa của số tín chỉ ("3.00" → "3", "1.50" → "1.5").
     */
    private static String formatCredits(String credits) {
        if (credits == null || credits.isEmpty()) {
            return "?";
        }
        try {
            double v = Double.parseDouble(credits);
            if (v == Math.floor(v)) {
                return String.valueOf((int) v);
            }
            return String.valueOf(v);
        } catch (NumberFormatException e) {
            return credits;
        }
    }
}
