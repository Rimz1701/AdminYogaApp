package com.example.yogaadminapp.models;

public class ClassInstanceModel {
    private int id;
    private int courseId;
    private String date;
    private String teacher;
    private String comment;

    public ClassInstanceModel(int id, int courseId, String date, String teacher, String comment) {
        this.id = id;
        this.courseId = courseId;
        this.date = date;
        this.teacher = teacher;
        this.comment = comment;
    }

    public int getId() { return id; }
    public int getCourseId() { return courseId; }
    public String getDate() { return date; }
    public String getTeacher() { return teacher; }
    public String getComment() { return comment; }

    public void setId(int id) { this.id = id; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public void setDate(String date) { this.date = date; }
    public void setTeacher(String teacher) { this.teacher = teacher; }
    public void setComment(String comment) { this.comment = comment; }
}