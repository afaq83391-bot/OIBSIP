package com.example.todoapp.adapters;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.R;
import com.example.todoapp.models.Task;

import java.util.List;

/**
 * RecyclerView adapter for the task list.
 * Handles display, completion toggling (with strikethrough), and delete callbacks.
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    public interface OnTaskActionListener {
        void onToggleComplete(Task task, boolean isNowCompleted);
        void onDeleteClick(Task task);
    }

    private final List<Task> tasks;
    private final OnTaskActionListener listener;

    public TaskAdapter(List<Task> tasks, OnTaskActionListener listener) {
        this.tasks = tasks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);

        holder.title.setText(task.getTitle());

        // Show notes if they exist
        if (task.getNotes() != null && !task.getNotes().trim().isEmpty()) {
            holder.notes.setText(task.getNotes());
            holder.notes.setVisibility(View.VISIBLE);
        } else {
            holder.notes.setVisibility(View.GONE);
        }

        // Set completion state visually
        holder.checkBox.setOnCheckedChangeListener(null); // Prevent recursive calls
        holder.checkBox.setChecked(task.isCompleted());
        // Change accent bar color based on status
        holder.accentBar.setBackgroundColor(task.isCompleted() ? 0xFF10B981 : 0xFF4F46E5);


        if (task.isCompleted()) {
            holder.title.setPaintFlags(holder.title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.title.setAlpha(0.5f);
            holder.notes.setAlpha(0.5f);
            holder.container.setActivated(true);
        } else {
            holder.title.setPaintFlags(holder.title.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.title.setAlpha(1.0f);
            holder.notes.setAlpha(1.0f);
            holder.container.setActivated(false);
        }

        // Checkbox listener
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) {
                listener.onToggleComplete(task, isChecked);
            }
        });

        // Delete button listener
        holder.deleteBtn.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(task);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    /**
     * Returns the internal task list (needed for swipe-to-delete position lookup).
     */
    public List<Task> getTasks() {
        return tasks;
    }


    /**
     * Replaces the entire task list and refreshes the RecyclerView.
     */
    public void updateTasks(List<Task> newTasks) {
        tasks.clear();
        tasks.addAll(newTasks);
        notifyDataSetChanged();
    }

    /**
     * Removes a specific task from the list at the given position.
     */
    public void removeTask(int position) {
        if (position >= 0 && position < tasks.size()) {
            tasks.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, tasks.size());
        }
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        LinearLayout container;
        CheckBox checkBox;
        TextView title;
        TextView notes;

        View accentBar;
        ImageButton deleteBtn;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.task_container);
            checkBox = itemView.findViewById(R.id.cb_completed);
            title = itemView.findViewById(R.id.tv_task_title);
            notes = itemView.findViewById(R.id.tv_task_notes);
            accentBar = itemView.findViewById(R.id.accent_bar);
            deleteBtn = itemView.findViewById(R.id.btn_delete);
        }
    }
}