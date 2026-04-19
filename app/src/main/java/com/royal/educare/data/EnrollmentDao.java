package com.royal.educare.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface EnrollmentDao {
    @Insert
    long insertEnrollment(Enrollment enrollment);

    @Query("SELECT * FROM enrollments WHERE userId = :userId")
    List<Enrollment> getEnrollmentsForUser(int userId);

    @Query("SELECT * FROM enrollments WHERE userId = :userId AND courseId = :courseId LIMIT 1")
    Enrollment getEnrollment(int userId, int courseId);
}
