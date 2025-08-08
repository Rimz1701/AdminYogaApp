package com.example.yogaadminapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yogaadminapp.R;
import com.example.yogaadminapp.adapters.CourseAdapter;
import com.example.yogaadminapp.db.YogaDBHelper;
import com.example.yogaadminapp.models.CourseModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private YogaDBHelper dbHelper;
    private List<CourseModel> courseList;
    private CourseAdapter adapter;
    private ImageButton btnAddCourse, btnSearch, btnUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new YogaDBHelper(this);
        listView = findViewById(R.id.courseListView);
        btnAddCourse = findViewById(R.id.btnAddCourse);
        btnSearch = findViewById(R.id.btnSearch);
        btnUpload = findViewById(R.id.btnUpload);

        courseList = dbHelper.getAllCourseModels();
        adapter = new CourseAdapter(this, courseList);
        listView.setAdapter(adapter);

        btnAddCourse.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddCourseActivity.class);
            startActivity(intent);
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            CourseModel selectedCourse = courseList.get(position);
            Intent intent = new Intent(MainActivity.this, ClassInstanceListActivity.class);
            intent.putExtra("course_id", selectedCourse.getId());
            startActivity(intent);
        });

        btnSearch.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        });

        btnUpload.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UploadToCloudActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        courseList = dbHelper.getAllCourseModels();
        adapter = new CourseAdapter(this, courseList);
        listView.setAdapter(adapter);
    }
}
