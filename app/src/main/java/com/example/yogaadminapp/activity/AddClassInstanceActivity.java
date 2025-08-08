package com.example.yogaadminapp.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.yogaadminapp.R;
import com.example.yogaadminapp.db.YogaDBHelper;
import java.util.Calendar;

public class AddClassInstanceActivity extends AppCompatActivity {

    private EditText editTeacher, editComments;
    private TextView tvPickedDate;
    private Button btnSaveInstance, btnClearInstance, btnPickDate;
    private YogaDBHelper dbHelper;
    private int courseId = -1;
    private String selectedDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class_instance);

        tvPickedDate = findViewById(R.id.tvPickedDate);
        btnPickDate = findViewById(R.id.btnPickDate);
        editTeacher = findViewById(R.id.editTeacher);
        editComments = findViewById(R.id.editComments);
        btnSaveInstance = findViewById(R.id.btnSaveInstance);
        btnClearInstance = findViewById(R.id.btnClearInstance);
        dbHelper = new YogaDBHelper(this);

        if (getIntent().hasExtra("course_id")) {
            courseId = getIntent().getIntExtra("course_id", -1);
        }

        if (courseId == -1) {
            Toast.makeText(this, "Invalid Course ID", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnPickDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, day) -> {
                selectedDate = String.format("%04d-%02d-%02d", year, month + 1, day);
                tvPickedDate.setText(selectedDate);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        btnSaveInstance.setOnClickListener(v -> saveInstance());
        btnClearInstance.setOnClickListener(v -> clearFields());
    }

    private void saveInstance() {
        String teacher = editTeacher.getText().toString().trim();
        String comments = editComments.getText().toString().trim();

        if (TextUtils.isEmpty(selectedDate)) {
            Toast.makeText(this, "Please pick a date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(teacher)) {
            editTeacher.setError("Teacher name is required");
            return;
        }

        boolean inserted = dbHelper.insertClassInstance(courseId, selectedDate, teacher, comments);
        if (inserted) {
            Toast.makeText(this, "Class instance added", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to add class instance", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearFields() {
        tvPickedDate.setText("No date selected");
        selectedDate = "";
        editTeacher.setText("");
        editComments.setText("");
    }
}
