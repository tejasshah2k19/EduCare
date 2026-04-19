package com.royal.educare.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TopicProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdateProgress(TopicProgress progress);

    @Query("SELECT * FROM topic_progress WHERE userId = :userId AND courseId = :courseId")
    List<TopicProgress> getProgressForCourse(int userId, int courseId);

    @Query("SELECT * FROM topic_progress WHERE userId = :userId AND topicId = :topicId LIMIT 1")
    TopicProgress getProgressForTopic(int userId, int topicId);
}
