package com.royal.educare.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "enrollments")
public class Enrollment {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int userId;
    public int courseId;
    public long enrolledAt;

    public Enrollment(int userId, int courseId, long enrolledAt) {
        this.userId = userId;
        this.courseId = courseId;
        this.enrolledAt = enrolledAt;
    }
}
