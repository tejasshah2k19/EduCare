package com.royal.educare.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "topic_progress")
public class TopicProgress {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int userId;
    public int courseId;
    public int topicId;
    public boolean isCompleted;

    public TopicProgress(int userId, int courseId, int topicId, boolean isCompleted) {
        this.userId = userId;
        this.courseId = courseId;
        this.topicId = topicId;
        this.isCompleted = isCompleted;
    }
}
