package com.example.k23411t_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.models.Major;
import com.example.models.StudyTypeGroup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

public class MajorActivity extends AppCompatActivity {

    private SearchView searchView;
    private ExpandableListView elvMajors;

    private ArrayList<StudyTypeGroup> originalGroups;
    private ArrayList<StudyTypeGroup> filteredGroups;
    private MajorExpandableAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_major);

        // Configure padding for system window insets (status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configure ActionBar back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.str_major_explorer_title));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initViews();
    }

    private void initViews() {
        searchView = findViewById(R.id.search_view);
        elvMajors = findViewById(R.id.elv_majors);

        // Load data from assets/majors.json
        originalGroups = loadMajorsFromJson();
        
        // Deep copy into filtered groups list initially
        filteredGroups = new ArrayList<>();
        for (StudyTypeGroup group : originalGroups) {
            filteredGroups.add(new StudyTypeGroup(group.getStudyType(), new ArrayList<>(group.getMajors())));
        }

        adapter = new MajorExpandableAdapter(filteredGroups);
        elvMajors.setAdapter(adapter);

        // Expand first group by default
        if (!filteredGroups.isEmpty()) {
            elvMajors.expandGroup(0);
        }

        // Set up list item click listener
        elvMajors.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Major selectedMajor = (Major) adapter.getChild(groupPosition, childPosition);
                if (selectedMajor != null) {
                    // Mở màn hình danh sách môn học, truyền tên ngành đã chọn.
                    Intent intent = new Intent(MajorActivity.this, SubjectActivity.class);
                    intent.putExtra(SubjectActivity.EXTRA_MAJOR_NAME, selectedMajor.getName());
                    startActivity(intent);
                }
                return true;
            }
        });

        // Set up search filter listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterMajors(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterMajors(newText);
                return true;
            }
        });
    }

    private ArrayList<StudyTypeGroup> loadMajorsFromJson() {
        ArrayList<StudyTypeGroup> list = new ArrayList<>();
        try {
            InputStream is = getAssets().open("majors.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonStr = new String(buffer, "UTF-8");

            JSONArray jsonArray = new JSONArray(jsonStr);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject groupObj = jsonArray.getJSONObject(i);
                String studyType = groupObj.getString("studyType");
                JSONArray majorsArray = groupObj.getJSONArray("majors");

                ArrayList<Major> majors = new ArrayList<>();
                for (int j = 0; j < majorsArray.length(); j++) {
                    JSONObject majorObj = majorsArray.getJSONObject(j);
                    String name = majorObj.getString("name");
                    String ologyId = majorObj.getString("ologyId");
                    String departmentId = majorObj.getString("departmentId");
                    String graduateLevelId = majorObj.getString("graduateLevelId");
                    String studyTypeId = majorObj.getString("studyTypeId");

                    majors.add(new Major(name, ologyId, departmentId, graduateLevelId, studyTypeId));
                }
                list.add(new StudyTypeGroup(studyType, majors));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to load majors database", Toast.LENGTH_SHORT).show();
        }
        return list;
    }

    private void filterMajors(String query) {
        filteredGroups.clear();
        String q = query.trim().toLowerCase();

        if (q.isEmpty()) {
            // Restore original entries
            for (StudyTypeGroup original : originalGroups) {
                filteredGroups.add(new StudyTypeGroup(original.getStudyType(), new ArrayList<>(original.getMajors())));
            }
        } else {
            for (StudyTypeGroup original : originalGroups) {
                ArrayList<Major> matchingMajors = new ArrayList<>();
                for (Major m : original.getMajors()) {
                    if (m.getName().toLowerCase().contains(q) || m.getOlogyId().toLowerCase().contains(q)) {
                        matchingMajors.add(m);
                    }
                }
                // If the group header matches the query, or it contains matching children, add it
                if (!matchingMajors.isEmpty() || original.getStudyType().toLowerCase().contains(q)) {
                    // If group matches but children list is empty because of query filtering, include all original children
                    if (matchingMajors.isEmpty() && original.getStudyType().toLowerCase().contains(q)) {
                        matchingMajors.addAll(original.getMajors());
                    }
                    filteredGroups.add(new StudyTypeGroup(original.getStudyType(), matchingMajors));
                }
            }
        }

        adapter.notifyDataSetChanged();

        // Auto-expand all matching groups during search to show matching items instantly
        if (!q.isEmpty()) {
            for (int i = 0; i < filteredGroups.size(); i++) {
                elvMajors.expandGroup(i);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ExpandableListAdapter class
    private class MajorExpandableAdapter extends BaseExpandableListAdapter {
        private final ArrayList<StudyTypeGroup> groupsList;

        public MajorExpandableAdapter(ArrayList<StudyTypeGroup> groupsList) {
            this.groupsList = groupsList;
        }

        @Override
        public int getGroupCount() {
            return groupsList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return groupsList.get(groupPosition).getMajors().size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groupsList.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return groupsList.get(groupPosition).getMajors().get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            StudyTypeGroup group = (StudyTypeGroup) getGroup(groupPosition);
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_major_group, parent, false);
            }
            TextView txtTitle = convertView.findViewById(R.id.txt_group_title);
            ImageView imgIndicator = convertView.findViewById(R.id.img_indicator);

            txtTitle.setText(group.getStudyType());

            if (imgIndicator != null) {
                if (isExpanded) {
                    imgIndicator.setImageResource(android.R.drawable.arrow_up_float);
                } else {
                    imgIndicator.setImageResource(android.R.drawable.arrow_down_float);
                }
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            Major child = (Major) getChild(groupPosition, childPosition);
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_major_child, parent, false);
            }
            TextView txtName = convertView.findViewById(R.id.txt_major_name);
            TextView txtOlogy = convertView.findViewById(R.id.txt_ology_id);

            txtName.setText(child.getName());
            txtOlogy.setText("Mã ngành (Ology ID): " + child.getOlogyId());

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
