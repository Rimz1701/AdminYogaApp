package com.example.yogaadminapp.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yogaadminapp.R;
import com.example.yogaadminapp.db.YogaDBHelper;

public class EditCourseActivity extends AppCompatActivity {

    private Spinner spinnerDay, spinnerTime, spinnerType;
    private EditText edtCapacity, edtDuration, edtPrice, edtDescription;
    private Button btnSave, btnCancel;
    private YogaDBHelper dbHelper;
    private int courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_course);

        dbHelper = new YogaDBHelper(this);

        spinnerDay = findViewById(R.id.spinnerDay);
        spinnerTime = findViewById(R.id.spinnerTime);
        spinnerType = findViewById(R.id.spinnerType);
        edtCapacity = findViewById(R.id.edtCapacity);
        edtDuration = findViewById(R.id.edtDuration);
        edtPrice = findViewById(R.id.edtPrice);
        edtDescription = findViewById(R.id.edtDescription);
        btnSave = findViewById(R.id.btnSaveCourse);
        btnCancel = findViewById(R.id.btnCancel);

        // Set adapter from strings.xml
        spinnerDay.setAdapter(ArrayAdapter.createFromResource(this, R.array.days_of_week, android.R.layout.simple_spinner_dropdown_item));
        spinnerTime.setAdapter(ArrayAdapter.createFromResource(this, R.array.class_times, android.R.layout.simple_spinner_dropdown_item));
        spinnerType.setAdapter(ArrayAdapter.createFromResource(this, R.array.class_types, android.R.layout.simple_spinner_dropdown_item));

        // Get data from intent
        courseId = getIntent().getIntExtra("course_id", -1);
        String day = getIntent().getStringExtra("day");
        String time = getIntent().getStringExtra("time");
        String type = getIntent().getStringExtra("type");

        spinnerDay.setSelection(getIndexFromResource(R.array.days_of_week, day));
        spinnerTime.setSelection(getIndexFromResource(R.array.class_times, time));
        spinnerType.setSelection(getIndexFromResource(R.array.class_types, type));

        edtCapacity.setText(String.valueOf(getIntent().getIntExtra("capacity", 0)));
        edtDuration.setText(getIntent().getStringExtra("duration"));
        edtPrice.setText(String.valueOf(getIntent().getDoubleExtra("price", 0)));
        edtDescription.setText(getIntent().getStringExtra("description"));

        // Save
        btnSave.setOnClickListener(v -> {
            String selectedDay = spinnerDay.getSelectedItem().toString();
            String selectedTime = spinnerTime.getSelectedItem().toString();
            String selectedType = spinnerType.getSelectedItem().toString();

            int capacity = Integer.parseInt(edtCapacity.getText().toString().trim());
            String duration = edtDuration.getText().toString().trim();
            double price = Double.parseDouble(edtPrice.getText().toString().trim());
            String description = edtDescription.getText().toString().trim();

            boolean updated = dbHelper.updateCourse(courseId, selectedDay, selectedTime, capacity, duration, price, selectedType, description);
            if (updated) {
                Toast.makeText(this, getString(R.string.course_updated), Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, getString(R.string.course_update_failed), Toast.LENGTH_SHORT).show();
            }
        });

        // Cancel
        btnCancel.setOnClickListener(v -> finish());
    }

    private int getIndexFromResource(int arrayResId, String value) {
        String[] array = getResources().getStringArray(arrayResId);
        for (int i = 0; i < array.length; i++) {
            if (array[i].equalsIgnoreCase(value)) return i;
        }
        return 0;
    }
}
