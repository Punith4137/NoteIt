package com.resource.noteit;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TasksActivity extends AppCompatActivity implements TasksAdapter.OnTaskActionListener, NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FloatingActionButton fabAddTask;
    private SearchView searchView;
    private View completedLabel;
    private View selectionActions;
    private ImageView btnDelete, btnComplete;

    private RecyclerView recyclerViewTasks;
    private RecyclerView recyclerViewCompleted;
    private TasksAdapter tasksAdapter;
    private TasksAdapter completedAdapter;

    private List<Task> taskList = new ArrayList<>();
    private List<Task> completedList = new ArrayList<>();

    private boolean selectionActive = false;

    private static final String PREFS_NAME = "TasksPrefs";
    private static final String KEY_TASKS = "Tasks";
    private static final String KEY_COMPLETED = "CompletedTasks";

    private final int CURRENT_ACTIVITY_ID = R.id.nav_header_title;
    private boolean isUpdatingSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Tasks");

        ConstraintLayout layout4 = findViewById(R.id.taskmain);

        int bgId2 = getSharedPreferences("AppPrefs", MODE_PRIVATE)
                .getInt("background_id", R.drawable.bg1);
        layout4.setBackgroundResource(bgId2);

        ConstraintLayout root = findViewById(R.id.taskmain);
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int bgId = preferences.getInt("selected_bg", -1);
        if (bgId != -1) {
            root.setBackgroundResource(bgId);
        }

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        recyclerViewTasks = findViewById(R.id.recyclerViewTasks);
        recyclerViewCompleted = findViewById(R.id.recyclerViewCompleted);
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCompleted.setLayoutManager(new LinearLayoutManager(this));

        tasksAdapter = new TasksAdapter(this, taskList, this);
        completedAdapter = new TasksAdapter(this, completedList, this);
        recyclerViewTasks.setAdapter(tasksAdapter);
        recyclerViewCompleted.setAdapter(completedAdapter);

        fabAddTask = findViewById(R.id.fabAddTask);
        fabAddTask.setOnClickListener(v -> showAddTaskDialog());

        searchView = findViewById(R.id.searchViewTasks);
        setupSearch();

        completedLabel = findViewById(R.id.completedLabel);

        selectionActions = findViewById(R.id.selectionActions);
        btnDelete = findViewById(R.id.btnDelete);
        btnComplete = findViewById(R.id.btnComplete);
        btnDelete.setOnClickListener(v -> showDeleteConfirmationDialog(getSelectedTasks()));
        btnComplete.setOnClickListener(v -> showCompletionConfirmationDialog(tasksAdapter.getSelectedTasks()));

        loadTasks();
        updateCompletedSectionVisibility();

        selectionActions.setVisibility(View.GONE);
    }

    private boolean handleNavigation(int id) {
        if (id == CURRENT_ACTIVITY_ID) {
            drawerLayout.closeDrawers();
            return true;
        }
        Intent intent = null;
        if (id == R.id.nav_main) intent = new Intent(this, MainActivity.class);
        else if (id == R.id.nav_details) intent = new Intent(this, Details.class);
        else if (id == R.id.nav_change_background) intent = new Intent(this, BackGroundActivity.class);

        if (intent != null) startActivity(intent);
        drawerLayout.closeDrawer(GravityCompat.START);
        finish();
        return true;
    }

    private void loadTasks() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Gson gson = new Gson();
        Type type = new TypeToken<List<Task>>() {}.getType();
        String tasksJson = prefs.getString(KEY_TASKS, "[]");
        String completedJson = prefs.getString(KEY_COMPLETED, "[]");
        taskList = gson.fromJson(tasksJson, type);
        completedList = gson.fromJson(completedJson, type);
        tasksAdapter.updateTasks(taskList);
        completedAdapter.updateTasks(completedList);
    }

    private void saveTasks() {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        Gson gson = new Gson();
        editor.putString(KEY_TASKS, gson.toJson(taskList));
        editor.putString(KEY_COMPLETED, gson.toJson(completedList));
        editor.apply();
    }

    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Task");
        EditText input = new EditText(this);
        input.setHint("Enter task title");
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String title = input.getText().toString().trim();
            if (!title.isEmpty()) {
                Task newTask = new Task(title);
                taskList.add(newTask);
                tasksAdapter.updateTasks(taskList);
                saveTasks();
                Toast.makeText(this, "Task added", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Task title cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) { filterTasks(query); return false; }
            @Override public boolean onQueryTextChange(String newText) { filterTasks(newText); return false; }
        });
    }

    private void filterTasks(String query) {
        if (query == null || query.trim().isEmpty()) {
            tasksAdapter.updateTasks(taskList);
        } else {
            List<Task> filtered = new ArrayList<>();
            for (Task t : taskList) {
                if (t.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    filtered.add(t);
                }
            }
            tasksAdapter.updateTasks(filtered);
        }
    }

    private List<Task> getSelectedTasks() {
        List<Task> selected = new ArrayList<>();
        selected.addAll(tasksAdapter.getSelectedTasks());
        selected.addAll(completedAdapter.getSelectedTasks());
        return selected;
    }

    private void showDeleteConfirmationDialog(List<Task> selected) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Confirmation")
                .setMessage("Are you sure you want to delete these tasks?")
                .setIcon(R.drawable.ic_dialog_alert)
                .setPositiveButton("Yes", (d,w) -> {
                    deleteTasks(selected);
                    clearSelectionAndUpdateUI();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void showCompletionConfirmationDialog(List<Task> selected) {
        new AlertDialog.Builder(this)
                .setTitle("Completed")
                .setMessage("Did you complete your task?")
                .setIcon(R.drawable.ic_dialog_alert)
                .setPositiveButton("Yes", (d,w) -> {
                    for (Task task : selected) {
                        task.setCompleted(true);
                        taskList.remove(task);
                        completedList.add(task);
                    }
                    tasksAdapter.updateTasks(taskList);
                    completedAdapter.updateTasks(completedList);
                    updateCompletedSectionVisibility();
                    saveTasks();
                    clearSelectionAndUpdateUI();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteTasks(List<Task> selected) {
        taskList.removeAll(selected);
        completedList.removeAll(selected);
        tasksAdapter.updateTasks(taskList);
        completedAdapter.updateTasks(completedList);
        saveTasks();
        Toast.makeText(this, selected.size() + " task(s) deleted", Toast.LENGTH_SHORT).show();
    }

    private void clearSelectionAndUpdateUI() {
        selectionActive = false;
        toolbar.setTitle("Tasks");
        selectionActions.setVisibility(View.GONE);
        tasksAdapter.clearSelection();
        completedAdapter.clearSelection();
    }

    @Override
    public void onTaskCompleted(Task task) {
        task.setCompleted(true);
        taskList.remove(task);
        completedList.add(task);
        tasksAdapter.updateTasks(taskList);
        completedAdapter.updateTasks(completedList);
        updateCompletedSectionVisibility();
        saveTasks();
    }

    @Override
    public void onTaskSelectionChanged(int selectedCount) {
        if (isUpdatingSelection) return;

        isUpdatingSelection = true;
        if (selectedCount > 0) {
            selectionActive = true;
            toolbar.setTitle(selectedCount + " selected");
            selectionActions.setVisibility(View.VISIBLE);
            boolean anyCompleted = false;
            for (Task t : tasksAdapter.getSelectedTasks())
                if (t.isCompleted()) {
                    anyCompleted = true;
                    break;
                }
            btnComplete.setVisibility(anyCompleted ? View.GONE : View.VISIBLE);
        } else {
            if (selectionActive) clearSelectionAndUpdateUI();
        }
        invalidateOptionsMenu();
        isUpdatingSelection = false;
    }

    private void updateCompletedSectionVisibility() {
        if (completedList.isEmpty()) {
            completedLabel.setVisibility(View.GONE);
            recyclerViewCompleted.setVisibility(View.GONE);
        } else {
            completedLabel.setVisibility(View.VISIBLE);
            recyclerViewCompleted.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onDeleteTasks(List<Task> selectedTasks) {}

    @Override
    public void onGroupTasks(List<Task> selectedTasks) {}

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return handleNavigation(item.getItemId());
    }
}
