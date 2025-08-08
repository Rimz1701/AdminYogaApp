package com.example.yogaadminapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.yogaadminapp.models.ClassInstanceModel;
import com.example.yogaadminapp.models.CourseModel;

import java.util.ArrayList;
import java.util.List;

public class YogaDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "yoga.db";
    private static final int DATABASE_VERSION = 1;

    // Table: courses
    private static final String TABLE_COURSE = "courses";
    private static final String COL_COURSE_ID = "id";
    private static final String COL_COURSE_DAY = "day";
    private static final String COL_COURSE_TIME = "time";
    private static final String COL_COURSE_CAPACITY = "capacity";
    private static final String COL_COURSE_DURATION = "duration";
    private static final String COL_COURSE_PRICE = "price";
    private static final String COL_COURSE_TYPE = "type";
    private static final String COL_COURSE_DESCRIPTION = "description";

    // Table: class_instances
    private static final String TABLE_CLASS = "class_instances";
    private static final String COL_CLASS_ID = "id";
    private static final String COL_CLASS_DATE = "date";
    private static final String COL_CLASS_TEACHER = "teacher";
    private static final String COL_CLASS_COMMENTS = "comments";
    private static final String COL_CLASS_COURSE_ID = "course_id";

    public YogaDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createCourses = "CREATE TABLE " + TABLE_COURSE + " ("
                + COL_COURSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_COURSE_DAY + " TEXT NOT NULL, "
                + COL_COURSE_TIME + " TEXT NOT NULL, "
                + COL_COURSE_CAPACITY + " INTEGER NOT NULL, "
                + COL_COURSE_DURATION + " TEXT NOT NULL, "
                + COL_COURSE_PRICE + " REAL NOT NULL, "
                + COL_COURSE_TYPE + " TEXT NOT NULL, "
                + COL_COURSE_DESCRIPTION + " TEXT);";

        String createClassInstances = "CREATE TABLE " + TABLE_CLASS + " ("
                + COL_CLASS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_CLASS_DATE + " TEXT NOT NULL, "
                + COL_CLASS_TEACHER + " TEXT NOT NULL, "
                + COL_CLASS_COMMENTS + " TEXT, "
                + COL_CLASS_COURSE_ID + " INTEGER NOT NULL, "
                + "FOREIGN KEY(" + COL_CLASS_COURSE_ID + ") REFERENCES " + TABLE_COURSE + "(" + COL_COURSE_ID + ") ON DELETE CASCADE);";

        db.execSQL(createCourses);
        db.execSQL(createClassInstances);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSE);
        onCreate(db);
    }

    // ===== COURSE CRUD =====

    public boolean insertCourse(String day, String time, int capacity, String duration, double price, String type, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_COURSE_DAY, day);
        values.put(COL_COURSE_TIME, time);
        values.put(COL_COURSE_CAPACITY, capacity);
        values.put(COL_COURSE_DURATION, duration);
        values.put(COL_COURSE_PRICE, price);
        values.put(COL_COURSE_TYPE, type);
        values.put(COL_COURSE_DESCRIPTION, description);
        long result = db.insert(TABLE_COURSE, null, values);
        return result != -1;
    }

    public boolean updateCourse(int id, String day, String time, int capacity, String duration, double price, String type, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_COURSE_DAY, day);
        values.put(COL_COURSE_TIME, time);
        values.put(COL_COURSE_CAPACITY, capacity);
        values.put(COL_COURSE_DURATION, duration);
        values.put(COL_COURSE_PRICE, price);
        values.put(COL_COURSE_TYPE, type);
        values.put(COL_COURSE_DESCRIPTION, description);
        int rows = db.update(TABLE_COURSE, values, COL_COURSE_ID + "=?", new String[]{String.valueOf(id)});
        return rows > 0;
    }

    public boolean deleteCourseById(int courseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_COURSE, COL_COURSE_ID + "=?", new String[]{String.valueOf(courseId)});
        return rows > 0;
    }

    public List<CourseModel> getAllCourseModels() {
        List<CourseModel> courseList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_COURSE, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_COURSE_ID));
                String day = cursor.getString(cursor.getColumnIndexOrThrow(COL_COURSE_DAY));
                String time = cursor.getString(cursor.getColumnIndexOrThrow(COL_COURSE_TIME));
                int capacity = cursor.getInt(cursor.getColumnIndexOrThrow(COL_COURSE_CAPACITY));
                String duration = cursor.getString(cursor.getColumnIndexOrThrow(COL_COURSE_DURATION));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_COURSE_PRICE));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(COL_COURSE_TYPE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COL_COURSE_DESCRIPTION));

                courseList.add(new CourseModel(id, day, time, capacity, duration, price, type, description));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return courseList;
    }

    public List<CourseModel> getAllCoursesWithInstances() {
        List<CourseModel> courseList = getAllCourseModels();

        for (CourseModel course : courseList) {
            List<ClassInstanceModel> classList = getClassInstancesByCourse(course.getId());
            course.setClassInstances(classList);
        }

        return courseList;
    }

    // ===== CLASS INSTANCE CRUD =====

    public boolean insertClassInstance(int courseId, String date, String teacher, String comments) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_CLASS_DATE, date);
        values.put(COL_CLASS_TEACHER, teacher);
        values.put(COL_CLASS_COMMENTS, comments);
        values.put(COL_CLASS_COURSE_ID, courseId);
        long result = db.insert(TABLE_CLASS, null, values);
        return result != -1;
    }

    public boolean updateClassInstance(int id, int courseId, String date, String teacher, String comment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_CLASS_DATE, date);
        values.put(COL_CLASS_TEACHER, teacher);
        values.put(COL_CLASS_COMMENTS, comment);
        values.put(COL_CLASS_COURSE_ID, courseId);
        int rows = db.update(TABLE_CLASS, values, COL_CLASS_ID + "=?", new String[]{String.valueOf(id)});
        return rows > 0;
    }

    public boolean deleteClassInstanceById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_CLASS, COL_CLASS_ID + "=?", new String[]{String.valueOf(id)});
        return rows > 0;
    }

    public List<ClassInstanceModel> getClassInstancesByCourse(int courseId) {
        List<ClassInstanceModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_CLASS,
                null,
                COL_CLASS_COURSE_ID + "=?",
                new String[]{String.valueOf(courseId)},
                null, null,
                COL_CLASS_DATE + " DESC"
        );

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_CLASS_ID));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COL_CLASS_DATE));
                String teacher = cursor.getString(cursor.getColumnIndexOrThrow(COL_CLASS_TEACHER));
                String comment = cursor.getString(cursor.getColumnIndexOrThrow(COL_CLASS_COMMENTS));

                list.add(new ClassInstanceModel(id, courseId, date, teacher, comment));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }

    // ===== SEARCH FUNCTIONS =====

    public List<ClassInstanceModel> searchClassByTeacherName(String teacherPrefix) {
        List<ClassInstanceModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_CLASS,
                null,
                COL_CLASS_TEACHER + " LIKE ?",
                new String[]{"%" + teacherPrefix + "%"},
                null, null,
                COL_CLASS_DATE + " DESC"
        );

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_CLASS_ID));
                int courseId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_CLASS_COURSE_ID));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COL_CLASS_DATE));
                String teacher = cursor.getString(cursor.getColumnIndexOrThrow(COL_CLASS_TEACHER));
                String comment = cursor.getString(cursor.getColumnIndexOrThrow(COL_CLASS_COMMENTS));

                list.add(new ClassInstanceModel(id, courseId, date, teacher, comment));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }

    public List<ClassInstanceModel> searchClassByCourseDay(String dayOfWeek) {
        List<ClassInstanceModel> resultList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT ci.* FROM " + TABLE_CLASS + " ci " +
                "JOIN " + TABLE_COURSE + " c ON ci." + COL_CLASS_COURSE_ID + " = c." + COL_COURSE_ID +
                " WHERE c." + COL_COURSE_DAY + " = ? " +
                "ORDER BY ci." + COL_CLASS_DATE + " DESC";

        Cursor cursor = db.rawQuery(query, new String[]{dayOfWeek});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_CLASS_ID));
                int courseId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_CLASS_COURSE_ID));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COL_CLASS_DATE));
                String teacher = cursor.getString(cursor.getColumnIndexOrThrow(COL_CLASS_TEACHER));
                String comment = cursor.getString(cursor.getColumnIndexOrThrow(COL_CLASS_COMMENTS));

                resultList.add(new ClassInstanceModel(id, courseId, date, teacher, comment));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return resultList;
    }

    public List<ClassInstanceModel> searchClassByDate(String date) {
        List<ClassInstanceModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_CLASS,
                null,
                COL_CLASS_DATE + " = ?",
                new String[]{date},
                null, null,
                COL_CLASS_DATE + " DESC"
        );

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_CLASS_ID));
                int courseId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_CLASS_COURSE_ID));
                String teacher = cursor.getString(cursor.getColumnIndexOrThrow(COL_CLASS_TEACHER));
                String comment = cursor.getString(cursor.getColumnIndexOrThrow(COL_CLASS_COMMENTS));

                list.add(new ClassInstanceModel(id, courseId, date, teacher, comment));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }
}
