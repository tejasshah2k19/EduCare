package com.royal.educare;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.royal.educare.data.Course;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private List<Course> courseList;
    private OnEnrollClickListener listener;

    public interface OnEnrollClickListener {
        void onEnrollClick(Course course, int position);
    }

    public CourseAdapter(List<Course> courseList, OnEnrollClickListener listener) {
        this.courseList = courseList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.tvCourseTitle.setText(course.title);
        holder.tvCourseDesc.setText(course.description);
        holder.tvCourseTopics.setText(course.topicsCount + " Topics");

        if (course.isEnrolled) {
            // Show the progress row
            holder.layoutProgress.setVisibility(View.VISIBLE);
            holder.pbCourseProgress.setProgress(course.progressPercent, true);
            holder.tvProgressPercent.setText(course.progressPercent + "%");

            if (course.progressPercent >= 100) {
                // Course fully completed — still navigable for re-reading
                holder.btnEnroll.setText("✓  Completed");
                holder.btnEnroll.setEnabled(true);
                holder.btnEnroll.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(Color.parseColor("#10B981")));
                holder.pbCourseProgress.setIndicatorColor(Color.parseColor("#10B981"));
                holder.tvProgressPercent.setTextColor(Color.parseColor("#10B981"));
                holder.btnEnroll.setOnClickListener(v -> {
                    if (listener != null) listener.onEnrollClick(course, position);
                });
            } else {
                // In-progress: allow opening
                holder.btnEnroll.setText("Continue Learning");
                holder.btnEnroll.setEnabled(true);
                holder.btnEnroll.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(Color.parseColor("#4F46E5")));
                holder.pbCourseProgress.setIndicatorColor(Color.parseColor("#4F46E5"));
                holder.tvProgressPercent.setTextColor(Color.parseColor("#4F46E5"));
                holder.btnEnroll.setOnClickListener(v -> {
                    if (listener != null) listener.onEnrollClick(course, position);
                });
            }
        } else {
            // Not enrolled yet
            holder.layoutProgress.setVisibility(View.GONE);
            holder.btnEnroll.setText("Enroll");
            holder.btnEnroll.setEnabled(true);
            holder.btnEnroll.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(Color.parseColor("#4F46E5")));
            holder.btnEnroll.setOnClickListener(v -> {
                if (listener != null) listener.onEnrollClick(course, position);
            });
        }
    }

    @Override
    public int getItemCount() {
        return courseList == null ? 0 : courseList.size();
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourseTitle, tvCourseDesc, tvCourseTopics, tvProgressPercent;
        MaterialButton btnEnroll;
        LinearLayout layoutProgress;
        LinearProgressIndicator pbCourseProgress;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourseTitle = itemView.findViewById(R.id.tvCourseTitle);
            tvCourseDesc = itemView.findViewById(R.id.tvCourseDesc);
            tvCourseTopics = itemView.findViewById(R.id.tvCourseTopics);
            tvProgressPercent = itemView.findViewById(R.id.tvProgressPercent);
            btnEnroll = itemView.findViewById(R.id.btnEnroll);
            layoutProgress = itemView.findViewById(R.id.layoutProgress);
            pbCourseProgress = itemView.findViewById(R.id.pbCourseProgress);
        }
    }
}
