package com.example.yogaadminapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yogaadminapp.R;
import com.example.yogaadminapp.activity.EditClassInstanceActivity;
import com.example.yogaadminapp.db.YogaDBHelper;
import com.example.yogaadminapp.models.ClassInstanceModel;

import java.util.List;

public class ClassInstanceAdapter extends BaseAdapter {
    private Context context;
    private List<ClassInstanceModel> instances;
    private LayoutInflater inflater;
    private YogaDBHelper dbHelper;

    public ClassInstanceAdapter(Context context, List<ClassInstanceModel> instances) {
        this.context = context;
        this.instances = instances;
        this.inflater = LayoutInflater.from(context);
        this.dbHelper = new YogaDBHelper(context);
    }

    @Override
    public int getCount() {
        return instances.size();
    }

    @Override
    public Object getItem(int position) {
        return instances.get(position);
    }

    @Override
    public long getItemId(int position) {
        return instances.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.class_instance_item, parent, false);
            holder = new ViewHolder();
            holder.tvDate = convertView.findViewById(R.id.tvDate);
            holder.tvTeacher = convertView.findViewById(R.id.tvTeacher);
            holder.tvComment = convertView.findViewById(R.id.tvComment);
            holder.btnDelete = convertView.findViewById(R.id.btnDelete);
            holder.btnEdit = convertView.findViewById(R.id.btnEdit);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ClassInstanceModel model = instances.get(position);

        holder.tvDate.setText("Date: " + model.getDate());
        holder.tvTeacher.setText("Teacher: " + model.getTeacher());
        holder.tvComment.setText("Comment: " + (model.getComment().isEmpty() ? "No comment" : model.getComment()));

        // Delete handler
        holder.btnDelete.setOnClickListener(v -> {
            boolean deleted = dbHelper.deleteClassInstanceById(model.getId());
            if (deleted) {
                instances.remove(position);
                notifyDataSetChanged();
                Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Delete failed", Toast.LENGTH_SHORT).show();
            }
        });

        // Edit handler
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditClassInstanceActivity.class);
            intent.putExtra("id", model.getId());
            intent.putExtra("course_id", model.getCourseId());
            intent.putExtra("date", model.getDate());
            intent.putExtra("teacher", model.getTeacher());
            intent.putExtra("comment", model.getComment());

            // Ensure context is an Activity
            if (context instanceof Activity) {
                ((Activity) context).startActivity(intent);
            } else {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // fallback
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    static class ViewHolder {
        TextView tvDate, tvTeacher, tvComment;
        Button btnDelete;
        Button btnEdit;
    }
}
