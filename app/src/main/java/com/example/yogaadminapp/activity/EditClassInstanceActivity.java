package com.example.yogaadminapp.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.yogaadminapp.R;
import com.example.yogaadminapp.db.YogaDBHelper;
import java.util.Calendar;

public class EditClassInstanceActivity extends AppCompatActivity {

    private TextView tvPickedDate;
    private EditText editTeacher, editComment;
    private Button btnPickDate, btnUpdate, btnCancel;
    private YogaDBHelper dbHelper;

    private int instanceId;
    private int courseId;
    private String selectedDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_class_instance);

        tvPickedDate = findViewById(R.id.tvPickedDate);
        btnPickDate = findViewById(R.id.btnPickDate);
        editTeacher = findViewById(R.id.editTeacher);
        editComment = findViewById(R.id.editComments);
        btnUpdate = findViewById(R.id.btnUpdateInstance);
        btnCancel = findViewById(R.id.btnCancel);

        dbHelper = new YogaDBHelper(this);

        instanceId = getIntent().getIntExtra("id", -1);
        courseId = getIntent().getIntExtra("course_id", -1);
        String date = getIntent().getStringExtra("date");
        String teacher = getIntent().getStringExtra("teacher");
        String comment = getIntent().getStringExtra("comment");

        if (instanceId == -1 || courseId == -1) {
            Toast.makeText(this, "Invalid instance data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        selectedDate = date;
        tvPickedDate.setText(selectedDate);
        editTeacher.setText(teacher);
        editComment.setText(comment);

        btnPickDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                tvPickedDate.setText(selectedDate);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        btnUpdate.setOnClickListener(v -> saveChanges());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void saveChanges() {
        String newTeacher = editTeacher.getText().toString().trim();
        String newComment = editComment.getText().toString().trim();

        if (TextUtils.isEmpty(selectedDate)) {
            Toast.makeText(this, "Please pick a date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(newTeacher)) {
            editTeacher.setError("Teacher name is required");
            return;
        }

        boolean updated = dbHelper.updateClassInstance(instanceId, courseId, selectedDate, newTeacher, newComment);
        if (updated) {
            Toast.makeText(this, "Updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
        }
    }
}
