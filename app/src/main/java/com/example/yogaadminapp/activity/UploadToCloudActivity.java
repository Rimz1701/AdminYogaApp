package com.example.yogaadminapp.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yogaadminapp.R;
import com.example.yogaadminapp.db.YogaDBHelper;
import com.example.yogaadminapp.models.ClassInstanceModel;
import com.example.yogaadminapp.models.CourseModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadToCloudActivity extends AppCompatActivity {

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
                Toast.makeText(this, "Không có kết nối Internet!", Toast.LENGTH_SHORT).show();
                return;
            }
            uploadDataToFirestore();
        });
    }

    private void uploadDataToFirestore() {
        List<CourseModel> courses = dbHelper.getAllCoursesWithInstances();

        for (CourseModel course : courses) {
            Map<String, Object> courseMap = new HashMap<>();
            courseMap.put("id", course.getId());
            courseMap.put("dayOfWeek", course.getDayOfWeek());
            courseMap.put("time", course.getTime());
            courseMap.put("capacity", course.getCapacity());
            courseMap.put("duration", course.getDuration());
            courseMap.put("price", course.getPrice());
            courseMap.put("type", course.getType());
            courseMap.put("description", course.getDescription());

            // Upload course
            firestore.collection("courses")
                    .document(String.valueOf(course.getId()))
                    .set(courseMap)
                    .addOnSuccessListener(aVoid -> {
                        List<ClassInstanceModel> instances = course.getClassInstances();
                        if (instances != null) {
                            for (ClassInstanceModel instance : instances) {
                                Map<String, Object> instanceMap = new HashMap<>();
                                instanceMap.put("id", instance.getId());
                                instanceMap.put("courseId", instance.getCourseId());
                                instanceMap.put("date", instance.getDate());
                                instanceMap.put("teacher", instance.getTeacher());
                                instanceMap.put("comment", instance.getComment());

                                // Add instance as subcollection
                                firestore.collection("courses")
                                        .document(String.valueOf(course.getId()))
                                        .collection("instances")
                                        .add(instanceMap);
                            }
                        }
                        runOnUiThread(() -> tvStatus.setText("✅ Upload thành công!"));
                    })
                    .addOnFailureListener(e -> runOnUiThread(() ->
                            tvStatus.setText("❌ Upload thất bại: " + e.getMessage())));
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo active = cm.getActiveNetworkInfo();
        return active != null && active.isConnected();
    }
}