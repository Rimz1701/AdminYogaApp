package com.example.yogaadminapp.models;

import java.util.List;

public class CourseModel {
    private int id;
    private String dayOfWeek;
    private String time;
    private int capacity;
    private String duration;
    private double price;
    private String type;
    private String description;
    private List<ClassInstanceModel> classInstances; // DANH SÁCH BUỔI HỌC

    public CourseModel(int id, String dayOfWeek, String time, int capacity, String duration, double price, String type, String description) {
        this.id = id;
        this.dayOfWeek = dayOfWeek;
        this.time = time;
        this.capacity = capacity;
        this.duration = duration;
        this.price = price;
        this.type = type;
        this.description = description;
    }

    // GETTERS
    public int getId() {
        return id;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public String getTime() {
        return time;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getDuration() {
        return duration;
    }

    public double getPrice() {
        return price;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public List<ClassInstanceModel> getClassInstances() {
        return classInstances;
    }

    // SETTERS
    public void setId(int id) {
        this.id = id;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setClassInstances(List<ClassInstanceModel> classInstances) {
        this.classInstances = classInstances;
    }
}
