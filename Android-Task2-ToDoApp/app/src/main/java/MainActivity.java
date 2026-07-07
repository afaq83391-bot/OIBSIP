package com.example.todoapp;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.adapters.TaskAdapter;
import com.example.todoapp.database.DatabaseHelper;
import com.example.todoapp.dialogs.AddTaskDialog;
import com.example.todoapp.models.Task;
import com.example.todoapp.utils.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements TaskAdapter.OnTaskActionListener, AddTaskDialog.OnTaskCreatedListener {

    private RecyclerView recyclerView;
    private LinearLayout emptyState;
    private FloatingActionButton fabAdd;
    private TextView tvGreeting, tvTaskSummary;
    private TextView tvCountAll, tvCountCompleted, tvCountPending, tvCountNotes;
    private MaterialToolbar toolbar;

    private TaskAdapter adapter;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private long currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_main);

            sessionManager = new SessionManager(this);
            if (!sessionManager.isLoggedIn()) {
                forceLogout();
                return;
            }
            currentUserId = sessionManager.getUserId();

            dbHelper = new DatabaseHelper(this);

            toolbar = findViewById(R.id.toolbar);
            recyclerView = findViewById(R.id.rv_tasks);
            emptyState = findViewById(R.id.empty_state);
            fabAdd = findViewById(R.id.fab_add_task);
            tvGreeting = findViewById(R.id.tv_greeting);
            tvTaskSummary = findViewById(R.id.tv_task_summary);
            tvCountAll = findViewById(R.id.tv_count_all);
            tvCountCompleted = findViewById(R.id.tv_count_completed);
            tvCountPending = findViewById(R.id.tv_count_pending);
            tvCountNotes = findViewById(R.id.tv_count_notes);

            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(true);
            }

            adapter = new TaskAdapter(loadTasks(), this);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
            recyclerView.setHasFixedSize(false);

            new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                @Override
                public boolean onMove(@NonNull RecyclerView rv, @NonNull RecyclerView.ViewHolder vh, @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    int position = viewHolder.getAdapterPosition();
                    Task task = adapter.getTasks().get(position);
                    showDeleteConfirmation(task, position);
                }
            }).attachToRecyclerView(recyclerView);

            fabAdd.setOnClickListener(v -> {
                AddTaskDialog dialog = new AddTaskDialog();
                dialog.show(getSupportFragmentManager(), "AddTaskDialog");
            });

            updateEmptyState();
            updateStats();

        } catch (Exception e) {
            Toast.makeText(this, "Main Crash: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private List<Task> loadTasks() {
        return dbHelper.getAllTasks(currentUserId);
    }

    private void refreshTaskList() {
        try {
            adapter.updateTasks(loadTasks());
            updateEmptyState();
            updateStats();
        } catch (Exception e) {
            Toast.makeText(this, "Refresh Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void updateStats() {
        int total = dbHelper.getTaskCount(currentUserId);
        int completed = dbHelper.getCompletedTaskCount(currentUserId);
        int pending = dbHelper.getPendingTaskCount(currentUserId);
        int notes = dbHelper.getNotesTaskCount(currentUserId);

        tvCountAll.setText(String.valueOf(total));
        tvCountCompleted.setText(String.valueOf(completed));
        tvCountPending.setText(String.valueOf(pending));
        tvCountNotes.setText(String.valueOf(notes));

        String name = sessionManager.getUserName();
        tvGreeting.setText("Hello, " + name);

        if (pending == 0 && total > 0) {
            tvTaskSummary.setText("You're all caught up!");
        } else if (total == 0) {
            tvTaskSummary.setText("Start by adding your first task");
        } else {
            tvTaskSummary.setText("You have " + pending + " task" + (pending == 1 ? "" : "s") + " pending");
        }
    }

    private void updateEmptyState() {
        if (adapter.getItemCount() == 0) {
            emptyState.setVisibility(android.view.View.VISIBLE);
            recyclerView.setVisibility(android.view.View.GONE);
        } else {
            emptyState.setVisibility(android.view.View.GONE);
            recyclerView.setVisibility(android.view.View.VISIBLE);
        }
    }

    @Override
    public void onToggleComplete(Task task, boolean isNowCompleted) {
        dbHelper.updateTaskCompletion(task.getId(), isNowCompleted);
        task.setCompleted(isNowCompleted);
        refreshTaskList();
        if (isNowCompleted) {
            Toast.makeText(this, R.string.toast_task_completed, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.toast_task_uncompleted, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDeleteClick(Task task) {
        List<Task> tasks = adapter.getTasks();
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId() == task.getId()) {
                showDeleteConfirmation(task, i);
                break;
            }
        }
    }

    private void showDeleteConfirmation(Task task, int position) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_confirm_title)
                .setMessage(R.string.delete_confirm_message)
                .setPositiveButton(R.string.btn_delete, (dialog, which) -> {
                    dbHelper.deleteTask(task.getId());
                    adapter.removeTask(position);
                    updateEmptyState();
                    updateStats();
                    Toast.makeText(this, R.string.toast_task_deleted, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.btn_keep, (dialog, which) -> refreshTaskList())
                .setOnCancelListener(dialog -> refreshTaskList())
                .show();
    }

    @Override
    public void onTaskCreated(String title, String notes) {
        try {
            dbHelper.addTask(currentUserId, title, notes);
            refreshTaskList();
            Toast.makeText(this, R.string.toast_task_added, Toast.LENGTH_SHORT).show();
            recyclerView.scrollToPosition(0);
        } catch (Exception e) {
            Toast.makeText(this, "Save Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_logout) {
            forceLogout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void forceLogout() {
        sessionManager.clearSession();
        android.content.Intent intent = new android.content.Intent(this, LoginActivity.class);
        intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!sessionManager.isLoggedIn()) {
            forceLogout();
        }
    }
}