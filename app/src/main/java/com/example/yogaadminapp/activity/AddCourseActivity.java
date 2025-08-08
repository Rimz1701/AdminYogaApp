package com.example.yogaadminapp.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yogaadminapp.R;
import com.example.yogaadminapp.db.YogaDBHelper;

public class AddCourseActivity extends AppCompatActivity {

    Spinner spinnerDay, spinnerTime, spinnerType;
    EditText editCapacity, editDuration, editPrice, editDescription;
    Button btnSaveCourse, btnClearCourse;
    YogaDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        // Init DB
        dbHelper = new YogaDBHelper(this);

        // Find Views
        spinnerDay = findViewById(R.id.spinnerDay);
        spinnerTime = findViewById(R.id.spinnerTime);
        spinnerType = findViewById(R.id.spinnerType);
        editCapacity = findViewById(R.id.editCapacity);
        editDuration = findViewById(R.id.editDuration);
        editPrice = findViewById(R.id.editPrice);
        editDescription = findViewById(R.id.editDescription);
        btnSaveCourse = findViewById(R.id.btnSaveCourse);
        btnClearCourse = findViewById(R.id.btnClearCourse);

        // Load spinner data
        setupSpinner(spinnerDay, R.array.days_of_week);
        setupSpinner(spinnerTime, R.array.class_times);
        setupSpinner(spinnerType, R.array.class_types);

        // Save Course
        btnSaveCourse.setOnClickListener(view -> saveCourse());

        // Clear Form
        btnClearCourse.setOnClickListener(view -> clearForm());
    }

    private void setupSpinner(Spinner spinner, int arrayResId) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, arrayResId, android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void saveCourse() {
        String day = spinnerDay.getSelectedItem().toString();
        String time = spinnerTime.getSelectedItem().toString();
        String type = spinnerType.getSelectedItem().toString();
        String capacityStr = editCapacity.getText().toString().trim();
        String duration = editDuration.getText().toString().trim();
        String priceStr = editPrice.getText().toString().trim();
        String description = editDescription.getText().toString().trim();

        // Validate required fields
        if (TextUtils.isEmpty(capacityStr) || TextUtils.isEmpty(duration) || TextUtils.isEmpty(priceStr)) {
            Toast.makeText(this, "Please fill all required fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        int capacity;
        double price;

        try {
            capacity = Integer.parseInt(capacityStr);
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Capacity and Price must be valid numbers.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean inserted = dbHelper.insertCourse(day, time, capacity, duration, price, type, description);

        if (inserted) {
            Toast.makeText(this, "Course added successfully!", Toast.LENGTH_SHORT).show();
            finish(); // Close activity
        } else {
            Toast.makeText(this, "Error saving course.", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearForm() {
        spinnerDay.setSelection(0);
        spinnerTime.setSelection(0);
        spinnerType.setSelection(0);
        editCapacity.setText("");
        editDuration.setText("");
        editPrice.setText("");
        editDescription.setText("");
    }
}
