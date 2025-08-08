package com.example.yogaadminapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.yogaadminapp.R;
import com.example.yogaadminapp.activity.ClassInstanceListActivity;
import com.example.yogaadminapp.activity.EditCourseActivity;
import com.example.yogaadminapp.db.YogaDBHelper;
import com.example.yogaadminapp.models.CourseModel;

import java.util.List;

public class CourseAdapter extends BaseAdapter {

    private Context context;
    private List<CourseModel> courseList;
    private LayoutInflater inflater;
    private YogaDBHelper dbHelper;

    public CourseAdapter(Context context, List<CourseModel> courseList) {
        this.context = context;
        this.courseList = courseList;
        this.inflater = LayoutInflater.from(context);
        this.dbHelper = new YogaDBHelper(context);
    }

    @Override
    public int getCount() {
        return courseList.size();
    }

    @Override
    public Object getItem(int position) {
        return courseList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return courseList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.course_item, parent, false);
            holder = new ViewHolder();
            holder.tvType = convertView.findViewById(R.id.tvType);
            holder.tvTime = convertView.findViewById(R.id.tvTime);
            holder.tvDay = convertView.findViewById(R.id.tvDay);
            holder.tvCapacity = convertView.findViewById(R.id.tvCapacity);
            holder.tvDuration = convertView.findViewById(R.id.tvDuration);
            holder.tvPrice = convertView.findViewById(R.id.tvPrice);
            holder.tvDescription = convertView.findViewById(R.id.tvDescription);
            holder.btnEdit = convertView.findViewById(R.id.btnEditCourse);
            holder.btnDelete = convertView.findViewById(R.id.btnDeleteCourse);
            holder.rootView = convertView;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CourseModel course = courseList.get(position);

        holder.tvType.setText(course.getType());
        holder.tvTime.setText(course.getTime());
        holder.tvDay.setText(course.getDayOfWeek());
        holder.tvCapacity.setText("Capacity: " + course.getCapacity());
        holder.tvDuration.setText(course.getDuration() + " mins");
        holder.tvPrice.setText("£" + course.getPrice());
        holder.tvDescription.setText("Detail: " + (course.getDescription().isEmpty() ? "N/A" : course.getDescription()));

        // Sự kiện Edit
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditCourseActivity.class);
            intent.putExtra("course_id", course.getId());
            intent.putExtra("day", course.getDayOfWeek());
            intent.putExtra("time", course.getTime());
            intent.putExtra("capacity", course.getCapacity());
            intent.putExtra("duration", course.getDuration());
            intent.putExtra("price", course.getPrice());
            intent.putExtra("type", course.getType());
            intent.putExtra("description", course.getDescription());
            context.startActivity(intent);
        });

        // Sự kiện Delete
        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Course")
                    .setMessage("Are you sure you want to delete this course and all its class instances?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        boolean deleted = dbHelper.deleteCourseById(course.getId());
                        if (deleted) {
                            courseList.remove(position);
                            notifyDataSetChanged();
                            Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Delete failed", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        holder.rootView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ClassInstanceListActivity.class);
            intent.putExtra("course_id", course.getId());
            context.startActivity(intent);
        });

        return convertView;
    }

    static class ViewHolder {
        TextView tvType, tvTime, tvDay, tvCapacity, tvDuration, tvPrice, tvDescription;
        Button btnEdit, btnDelete;
        View rootView;
    }
}
