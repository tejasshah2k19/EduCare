package com.royal.educare;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.royal.educare.data.Topic;

import java.util.List;
import java.util.Map;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicViewHolder> {

    private List<Topic> topicList;
    private Map<Integer, Boolean> completionMap; // topicId -> isCompleted
    private OnTopicClickListener listener;

    public interface OnTopicClickListener {
        void onTopicClick(Topic topic);
    }

    public TopicAdapter(List<Topic> topicList, Map<Integer, Boolean> completionMap, OnTopicClickListener listener) {
        this.topicList = topicList;
        this.completionMap = completionMap;
        this.listener = listener;
    }

    public void setCompletionMap(Map<Integer, Boolean> newMap) {
        this.completionMap = newMap;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_topic, parent, false);
        return new TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
        Topic topic = topicList.get(position);
        holder.tvTopicTitle.setText(topic.title);

        boolean isCompleted = completionMap != null && completionMap.getOrDefault(topic.id, false);
        if (isCompleted) {
            holder.ivStatus.setImageResource(android.R.drawable.checkbox_on_background);
        } else {
            holder.ivStatus.setImageResource(android.R.drawable.checkbox_off_background);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTopicClick(topic);
            }
        });
    }

    @Override
    public int getItemCount() {
        return topicList == null ? 0 : topicList.size();
    }

    static class TopicViewHolder extends RecyclerView.ViewHolder {
        ImageView ivStatus;
        TextView tvTopicTitle;

        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            ivStatus = itemView.findViewById(R.id.ivStatus);
            tvTopicTitle = itemView.findViewById(R.id.tvTopicTitle);
        }
    }
}
