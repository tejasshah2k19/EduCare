package com.royal.educare.data;

public class Course {
    public int id;
    public String title;
    public String description;
    public int topicsCount;
    public boolean isEnrolled;
    public int progressPercent; // 0-100

    public Course(int id, String title, String description, int topicsCount) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.topicsCount = topicsCount;
        this.isEnrolled = false;
        this.progressPercent = 0;
    }
}
