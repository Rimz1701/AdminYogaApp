package com.example.yogaadminapp.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log; // Import Log
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yogaadminapp.R;
import com.example.yogaadminapp.db.YogaDBHelper;
import com.example.yogaadminapp.models.ClassInstanceModel;
import com.example.yogaadminapp.models.CourseModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch; // Import WriteBatch

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class UploadToCloudActivity extends AppCompatActivity {

    private static final String TAG = "UploadToCloudActivity"; // Tag để debug
    private YogaDBHelper dbHelper;
    private TextView tvStatus;
    private Button btnUpload;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_to_cloud);

        dbHelper = new YogaDBHelper(this);
        firestore = FirebaseFirestore.getInstance();

        tvStatus = findViewById(R.id.tvUploadStatus);
        btnUpload = findViewById(R.id.btnUploadData);

        btnUpload.setOnClickListener(v -> {
            if (!isNetworkAvailable()) {
                tvStatus.setText("Status: No Internet Connection!");
                Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                return;
            }
            uploadDataToFirestore();
        });
    }

    private void uploadDataToFirestore() {
        // Cập nhật trạng thái ngay khi bắt đầu
        tvStatus.setText("Status: Starting upload...");
        btnUpload.setEnabled(false);

        List<CourseModel> courses = dbHelper.getAllCoursesWithInstances();

        // Xử lý trường hợp không có dữ liệu để upload
        if (courses.isEmpty()) {
            tvStatus.setText("Status: No data to upload.");
            btnUpload.setEnabled(true);
            Toast.makeText(this, "Local database is empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        WriteBatch batch = firestore.batch();

        for (CourseModel course : courses) {
            Map<String, Object> courseMap = new HashMap<>();
            courseMap.put("dayOfWeek", course.getDayOfWeek());
            courseMap.put("time", course.getTime());
            courseMap.put("capacity", course.getCapacity());
            courseMap.put("duration", course.getDuration());
            courseMap.put("price", course.getPrice());
            courseMap.put("type", course.getType());
            courseMap.put("description", course.getDescription());

            // Thêm thao tác ghi course vào batch
            batch.set(firestore.collection("courses").document(String.valueOf(course.getId())), courseMap);

            List<ClassInstanceModel> instances = course.getClassInstances();
            if (instances != null && !instances.isEmpty()) {
                for (ClassInstanceModel instance : instances) {
                    Map<String, Object> instanceMap = new HashMap<>();
                    instanceMap.put("date", instance.getDate());
                    instanceMap.put("teacher", instance.getTeacher());
                    instanceMap.put("comment", instance.getComment());

                    // Thêm thao tác ghi instance vào batch
                    batch.set(firestore.collection("courses").document(String.valueOf(course.getId()))
                            .collection("instances").document(), instanceMap);
                }
            }
        }

        // Thực thi toàn bộ batch
        batch.commit()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Batch commit successful!");
                    // Cập nhật UI trên luồng chính
                    runOnUiThread(() -> {
                        tvStatus.setText("Status: ✅ Upload successful!");
                        Toast.makeText(UploadToCloudActivity.this, "All data has been synchronized.", Toast.LENGTH_LONG).show();
                        btnUpload.setEnabled(true);
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Batch commit failed: ", e);
                    // Cập nhật UI trên luồng chính
                    runOnUiThread(() -> {
                        tvStatus.setText("Status: ❌ Upload failed: " + e.getMessage());
                        btnUpload.setEnabled(true);
                    });
                });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        NetworkInfo active = cm.getActiveNetworkInfo();
        return active != null && active.isConnectedOrConnecting();
    }
}