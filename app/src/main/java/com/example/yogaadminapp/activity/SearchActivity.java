package com.example.yogaadminapp.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.yogaadminapp.R;
import com.example.yogaadminapp.adapters.ClassInstanceAdapter;
import com.example.yogaadminapp.db.YogaDBHelper;
import com.example.yogaadminapp.models.ClassInstanceModel;

import java.util.*;

public class SearchActivity extends AppCompatActivity {

    private EditText edtTeacher;
    private Spinner spinnerDay;
    private Button btnSearchByTeacher, btnSearchByDay, btnSearchByDate, btnPickDate;
    private ListView resultList;
    private TextView tvPickedDate;

    private YogaDBHelper dbHelper;
    private ClassInstanceAdapter adapter;
    private List<ClassInstanceModel> result;
    private String selectedDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        dbHelper = new YogaDBHelper(this);

        edtTeacher = findViewById(R.id.edtTeacherName);
        spinnerDay = findViewById(R.id.spinnerSearchDay);
        btnSearchByTeacher = findViewById(R.id.btnSearchByTeacher);
        btnSearchByDay = findViewById(R.id.btnSearchByDay);
        btnSearchByDate = findViewById(R.id.btnSearchByDate);
        btnPickDate = findViewById(R.id.btnPickDate);
        tvPickedDate = findViewById(R.id.tvSelectedDate);
        resultList = findViewById(R.id.listSearchResults);

        // Populate spinner
        ArrayAdapter<CharSequence> adapterDays = ArrayAdapter.createFromResource(this,
                R.array.days_of_week, android.R.layout.simple_spinner_item);
        adapterDays.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(adapterDays);

        // Search by teacher
        btnSearchByTeacher.setOnClickListener(v -> {
            String name = edtTeacher.getText().toString().trim();
            result = dbHelper.searchClassByTeacherName(name);
            showResults();
        });

        // Search by day
        btnSearchByDay.setOnClickListener(v -> {
            String day = spinnerDay.getSelectedItem().toString();
            result = dbHelper.searchClassByCourseDay(day);
            showResults();
        });

        // Pick date
        btnPickDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                tvPickedDate.setText(selectedDate);
            },
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Search by selected date
        btnSearchByDate.setOnClickListener(v -> {
            if (!selectedDate.isEmpty()) {
                result = dbHelper.searchClassByDate(selectedDate);
                showResults();
            } else {
                Toast.makeText(this, "Please pick a date", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showResults() {
        adapter = new ClassInstanceAdapter(this, result);
        resultList.setAdapter(adapter);
    }
}
