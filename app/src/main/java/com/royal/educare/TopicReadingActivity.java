package com.royal.educare;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.royal.educare.data.AppDatabase;
import com.royal.educare.data.Topic;
import com.royal.educare.data.TopicProgress;

import java.util.concurrent.Executors;

public class TopicReadingActivity extends AppCompatActivity {

    private TextView tvReadingTitle, tvReadingContent;
    private MaterialButton btnMarkComplete;
    
    private int currentUserId = -1;
    private Topic currentTopic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_reading);

        tvReadingTitle = findViewById(R.id.tvReadingTitle);
        tvReadingContent = findViewById(R.id.tvReadingContent);
        btnMarkComplete = findViewById(R.id.btnMarkComplete);

        SharedPreferences prefs = getSharedPreferences("EduCarePrefs", MODE_PRIVATE);
        currentUserId = prefs.getInt("userId", -1);

        currentTopic = (Topic) getIntent().getSerializableExtra("topic");

        if (currentUserId == -1 || currentTopic == null) {
            finish();
            return;
        }

        tvReadingTitle.setText(currentTopic.title);
        tvReadingContent.setText(currentTopic.content);

        checkIfAlreadyCompleted();

        btnMarkComplete.setOnClickListener(v -> markComplete());
    }

    private void checkIfAlreadyCompleted() {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
            TopicProgress progress = db.topicProgressDao().getProgressForTopic(currentUserId, currentTopic.id);

            if (progress != null && progress.isCompleted) {
                runOnUiThread(() -> {
                    btnMarkComplete.setText("Completed");
                    btnMarkComplete.setEnabled(false);
                });
            }
        });
    }

    private void markComplete() {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
            TopicProgress progress = new TopicProgress(currentUserId, currentTopic.courseId, currentTopic.id, true);
            db.topicProgressDao().insertOrUpdateProgress(progress);

            runOnUiThread(() -> {
                Toast.makeText(this, "Topic Marked as Complete!", Toast.LENGTH_SHORT).show();
                finish(); // Go back to course topics list
            });
        });
    }
}
