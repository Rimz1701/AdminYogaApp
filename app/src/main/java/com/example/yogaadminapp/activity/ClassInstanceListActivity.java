package com.example.yogaadminapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yogaadminapp.R;
import com.example.yogaadminapp.adapters.ClassInstanceAdapter;
import com.example.yogaadminapp.db.YogaDBHelper;
import com.example.yogaadminapp.models.ClassInstanceModel;

import java.util.List;

public class ClassInstanceListActivity extends AppCompatActivity {

    private int courseId;
    private YogaDBHelper dbHelper;
    private ListView listClassInstances;
    private Button btnAddInstance;
    private List<ClassInstanceModel> instanceList;
    private ClassInstanceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_instance_list);

        courseId = getIntent().getIntExtra("course_id", -1);
        dbHelper = new YogaDBHelper(this);
        listClassInstances = findViewById(R.id.listClassInstances);
        btnAddInstance = findViewById(R.id.btnAddInstance);

        loadInstances();

        btnAddInstance.setOnClickListener(v -> {
            Intent intent = new Intent(ClassInstanceListActivity.this, AddClassInstanceActivity.class);
            intent.putExtra("course_id", courseId);
            startActivity(intent);
        });

        listClassInstances.setOnItemLongClickListener((parent, view, position, id) -> {
            ClassInstanceModel instance = instanceList.get(position);

            new AlertDialog.Builder(ClassInstanceListActivity.this)
                    .setTitle("Delete Instance")
                    .setMessage("Are you sure you want to delete this class instance?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        boolean deleted = dbHelper.deleteClassInstanceById(instance.getId());
                        if (deleted) {
                            Toast.makeText(this, "Instance deleted", Toast.LENGTH_SHORT).show();
                            loadInstances();
                        } else {
                            Toast.makeText(this, "Failed to delete", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();

            return true;
        });
    }

    private void loadInstances() {
        instanceList = dbHelper.getClassInstancesByCourse(courseId);
        adapter = new ClassInstanceAdapter(this, instanceList);
        listClassInstances.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadInstances();
    }
}
