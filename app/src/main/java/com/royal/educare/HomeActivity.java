package com.royal.educare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.royal.educare.data.AppDatabase;
import com.royal.educare.data.Course;
import com.royal.educare.data.Enrollment;
import com.royal.educare.data.TopicProgress;
import com.royal.educare.data.TopicProvider;
import com.royal.educare.data.User;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class HomeActivity extends AppCompatActivity {

    private TextView tvWelcomeMessage;
    private ImageView ivHomeProfilePic;
    private RecyclerView rvCourses;
    private CourseAdapter adapter;
    private List<Course> courseList;
    private int currentUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tvWelcomeMessage = findViewById(R.id.tvWelcomeMessage);
        ivHomeProfilePic = findViewById(R.id.ivHomeProfilePic);
        rvCourses = findViewById(R.id.rvCourses);
        FrameLayout flProfileContainer = findViewById(R.id.flProfileContainer);

        SharedPreferences prefs = getSharedPreferences("EduCarePrefs", MODE_PRIVATE);
        currentUserId = prefs.getInt("userId", -1);

        if (currentUserId == -1) {
            Toast.makeText(this, "Session invalid. Please login.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        flProfileContainer.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        initializeDummyCourses();

        adapter = new CourseAdapter(courseList, (course, position) -> handleEnrollment(course, position));
        rvCourses.setAdapter(adapter);

        loadUserData();
        loadSavedPhoto();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh progress and profile photo whenever returning from a course or profile
        if (currentUserId != -1 && courseList != null) {
            loadUserData();
            loadSavedPhoto();
        }
    }

    private void loadSavedPhoto() {
        SharedPreferences prefs = getSharedPreferences("EduCarePrefs", MODE_PRIVATE);
        String photoPath = prefs.getString("profile_photo_path_" + currentUserId, null);
        if (photoPath != null) {
            File file = new File(photoPath);
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                if (bitmap != null) {
                    ivHomeProfilePic.setImageBitmap(getRoundedBitmap(bitmap));
                }
            }
        }
    }

    private Bitmap getRoundedBitmap(Bitmap src) {
        int size = Math.min(src.getWidth(), src.getHeight());
        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        android.graphics.Canvas canvas = new android.graphics.Canvas(output);
        android.graphics.Paint paint = new android.graphics.Paint();
        paint.setAntiAlias(true);
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint);
        paint.setXfermode(new android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(src, (size - src.getWidth()) / 2f, (size - src.getHeight()) / 2f, paint);
        return output;
    }

    private void loadUserData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
            User user = db.userDao().getUserById(currentUserId);
            List<Enrollment> enrollments = db.enrollmentDao().getEnrollmentsForUser(currentUserId);

            if (user != null) {
                runOnUiThread(() -> tvWelcomeMessage.setText("Welcome, " + user.firstName + "!"));
            }

            // Mark enrolled courses and calculate progress
            for (Enrollment enrollment : enrollments) {
                for (Course course : courseList) {
                    if (course.id == enrollment.courseId) {
                        course.isEnrolled = true;
                        // Load topic progress for this course
                        List<TopicProgress> progressList =
                                db.topicProgressDao().getProgressForCourse(currentUserId, course.id);
                        int totalTopics = TopicProvider.getTopicsForCourse(course.id).size();
                        int completedTopics = 0;
                        for (TopicProgress p : progressList) {
                            if (p.isCompleted) completedTopics++;
                        }
                        course.progressPercent = totalTopics == 0 ? 0
                                : (completedTopics * 100) / totalTopics;
                        break;
                    }
                }
            }

            runOnUiThread(() -> adapter.notifyDataSetChanged());
        });
    }

    private void handleEnrollment(Course course, int position) {
        if (course.isEnrolled) {
            Intent intent = new Intent(HomeActivity.this, CourseTopicsActivity.class);
            intent.putExtra("courseId", course.id);
            intent.putExtra("courseTitle", course.title);
            startActivity(intent);
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
            Enrollment enrollment = new Enrollment(currentUserId, course.id, System.currentTimeMillis());
            db.enrollmentDao().insertEnrollment(enrollment);

            runOnUiThread(() -> {
                course.isEnrolled = true;
                adapter.notifyItemChanged(position);
                Toast.makeText(HomeActivity.this, "Successfully Enrolled!", Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void initializeDummyCourses() {
        courseList = new ArrayList<>();
        courseList.add(new Course(1, "Basic Mathematics", "Learn operations, fractions, and algebra.", 10));
        courseList.add(new Course(2, "English Fundamentals", "Grammar, writing, and reading comprehension.", 12));
        courseList.add(new Course(3, "Science Explorer", "Introduction to biology, chemistry, and physics.", 15));
        courseList.add(new Course(4, "History 101", "A basic overview of world and regional history.", 8));
        courseList.add(new Course(5, "Programming for Beginners", "Learn the basics of logic and computers.", 20));
    }
}
