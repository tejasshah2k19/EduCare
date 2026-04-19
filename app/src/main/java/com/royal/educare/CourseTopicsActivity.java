package com.royal.educare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.royal.educare.data.AppDatabase;
import com.royal.educare.data.Topic;
import com.royal.educare.data.TopicProgress;
import com.royal.educare.data.TopicProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

public class CourseTopicsActivity extends AppCompatActivity {

    private TextView tvCourseTitle, tvProgressText;
    private LinearProgressIndicator pbCourseProgress;
    private RecyclerView rvTopics;
    private TopicAdapter adapter;
    private List<Topic> topicList;
    
    private int currentUserId = -1;
    private int courseId;
    private String courseTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_topics);

        tvCourseTitle = findViewById(R.id.tvCourseTitle);
        pbCourseProgress = findViewById(R.id.pbCourseProgress);
        tvProgressText = findViewById(R.id.tvProgressText);
        rvTopics = findViewById(R.id.rvTopics);

        SharedPreferences prefs = getSharedPreferences("EduCarePrefs", MODE_PRIVATE);
        currentUserId = prefs.getInt("userId", -1);

        Intent intent = getIntent();
        courseId = intent.getIntExtra("courseId", -1);
        courseTitle = intent.getStringExtra("courseTitle");

        if (currentUserId == -1 || courseId == -1) {
            finish();
            return;
        }

        tvCourseTitle.setText(courseTitle);

        topicList = TopicProvider.getTopicsForCourse(courseId);
        adapter = new TopicAdapter(topicList, new HashMap<>(), topic -> {
            Intent readIntent = new Intent(CourseTopicsActivity.this, TopicReadingActivity.class);
            readIntent.putExtra("topic", topic);
            startActivity(readIntent);
        });
        rvTopics.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTopicProgress();
    }

    private void loadTopicProgress() {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
            List<TopicProgress> progressList = db.topicProgressDao().getProgressForCourse(currentUserId, courseId);

            Map<Integer, Boolean> progressMap = new HashMap<>();
            int completedCount = 0;
            for (TopicProgress p : progressList) {
                progressMap.put(p.topicId, p.isCompleted);
                if (p.isCompleted) completedCount++;
            }

            int totalCount = topicList.size();
            int percentage = totalCount == 0 ? 0 : (completedCount * 100) / totalCount;

            runOnUiThread(() -> {
                pbCourseProgress.setProgress(percentage);
                tvProgressText.setText(percentage + "%");
                adapter.setCompletionMap(progressMap);
            });
        });
    }
}
