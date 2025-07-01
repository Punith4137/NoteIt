package com.resource.noteit;

// Import statements: ⚙️ These bring in Android and Java classes needed for UI and data handling
import android.app.AlertDialog;                       // For showing alert dialogs to the user
import android.content.Context;                        // Context provides access to app-specific resources
import android.graphics.Color;                         // To set colors programmatically
import android.view.LayoutInflater;                    // To create views from XML layouts
import android.view.View;                              // Represents the UI components on screen
import android.view.ViewGroup;                         // Container for other views, used in RecyclerView
import android.widget.CheckBox;                        // UI component for selectable checkbox
import android.widget.TextView;                        // UI component to display text
import androidx.annotation.NonNull;                    // For null-safety annotations
import androidx.core.content.ContextCompat;           // For accessing resources with compatibility
import androidx.recyclerview.widget.RecyclerView;     // RecyclerView for efficient scrolling lists
import java.util.ArrayList;                            // List implementation with dynamic size
import java.util.List;                                 // Interface for ordered collections

/**
 * TasksAdapter class: 📝
 * This adapter manages a list of Task objects and binds them to a RecyclerView.
 * It supports multi-selection of tasks, marking tasks completed, and notifying the parent activity via listener callbacks.
 *
 * It handles user interactions such as selecting, deselecting, completing tasks and manages UI updates accordingly.
 */
public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TaskViewHolder> {

    /**
     * OnTaskActionListener interface: 🔔
     * Allows communication between this adapter and the activity/fragment to handle task actions like completion, selection changes, deletion, and grouping.
     */
    public interface OnTaskActionListener {
        void onTaskCompleted(Task task);                      // Called when a task is marked completed
        void onTaskSelectionChanged(int selectedCount);      // Called when the number of selected tasks changes
        void onDeleteTasks(List<Task> selectedTasks);        // Called to delete selected tasks (not used here)
        void onGroupTasks(List<Task> selectedTasks);         // Called to group selected tasks (not used here)
    }

    // Context to access resources, layouts etc. 🎨
    private Context context;

    // List of all tasks currently displayed 🔥
    private List<Task> tasks;

    // List of currently selected tasks for multi-selection 🎯
    private List<Task> selectedTasks = new ArrayList<>();

    // Flag to indicate if multi-selection mode is active 🔄
    private boolean multiSelect = false;

    // Listener for callbacks to activity/fragment 📞
    private OnTaskActionListener listener;

    // Flag to prevent recursive clearing crash loop ✔️
    boolean isClearing = false;

    /**
     * Constructor: 🔨
     * Initializes adapter with context, list of tasks, and the listener for task actions.
     */
    public TasksAdapter(Context context, List<Task> tasks, OnTaskActionListener listener) {
        this.context = context;           // Store the Context for later use
        this.tasks = tasks;               // Initialize with the provided task list
        this.listener = listener;         // Assign listener for event callbacks
    }

    /**
     * updateTasks: 🔄
     * Replaces current task list with a new list and resets selection state.
     * Notifies RecyclerView to refresh all items.
     */
    public void updateTasks(List<Task> newTasks) {
        this.tasks = new ArrayList<>(newTasks);  // Create new list copy to avoid external modification
        selectedTasks.clear();                    // Clear any selected tasks
        multiSelect = false;                      // Disable multi-selection mode
        notifyDataSetChanged();                   // Refresh entire RecyclerView
    }

    /**
     * getSelectedTasks: 📋
     * Returns a copy of the currently selected tasks list.
     */
    public List<Task> getSelectedTasks() {
        return new ArrayList<>(selectedTasks);   // Return a new list to avoid external modifications
    }

    /**
     * clearSelection: ❌
     * Clears all selected tasks and exits multi-selection mode.
     * Prevents crash by avoiding re-entrance.
     * Notifies listener of zero selections.
     */
    public void clearSelection() {
        if (isClearing) return;            // Prevent re-entrance if already clearing

        isClearing = true;                 // Mark as clearing to avoid recursion
        selectedTasks.clear();             // Clear selected tasks list
        multiSelect = false;               // Turn off multi-select mode
        notifyDataSetChanged();            // Refresh UI to remove selection highlights

        if (listener != null) {
            listener.onTaskSelectionChanged(0);  // Notify listener no tasks are selected
        }

        isClearing = false;                // Reset clearing flag
    }

    /**
     * onCreateViewHolder: 🏗️
     * Inflates the individual task item layout and creates a ViewHolder for it.
     */
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the XML layout for a single task item
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);   // Return a new ViewHolder instance with this view
    }

    /**
     * onBindViewHolder: 🔗
     * Binds task data and listeners to the ViewHolder UI elements based on position.
     * Handles UI state for completed tasks, checkbox visibility, and selection highlighting.
     */
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);               // Get task at this position

        holder.title.setText(task.getTitle());         // Set task title text

        // If task is completed, prepend a check mark ✅ emoji for visual cue
        if (task.isCompleted()) {
            holder.title.setText("✅ " + task.getTitle());
        }

        // Completed tasks hide the checkbox unless multi-select is active
        if (task.isCompleted() && !multiSelect) {
            holder.checkbox.setVisibility(View.GONE);  // Hide checkbox for completed task
        } else {
            holder.checkbox.setVisibility(View.VISIBLE); // Show checkbox
            // Set checkbox checked state based on whether task is selected
            holder.checkbox.setChecked(selectedTasks.contains(task));
        }

        // Checkbox click listener handles selection or completion logic
        holder.checkbox.setOnClickListener(v -> {
            if (multiSelect) {
                // If multi-select mode, toggle selection on checkbox click
                toggleSelection(task);
            } else if (!task.isCompleted()) {
                // If not multi-select and task not completed, confirm completion with user
                new AlertDialog.Builder(context)
                        .setTitle("Complete Task")                   // Dialog title
                        .setMessage("Did you complete your task?")  // Dialog message
                        .setPositiveButton("Yes", (dialog, which) -> {
                            task.setCompleted(true);                  // Mark task completed
                            if (listener != null) listener.onTaskCompleted(task);  // Notify listener
                        })
                        .setNegativeButton("No", null)                // Cancel option does nothing
                        .show();

                holder.checkbox.setChecked(false);               // Uncheck checkbox immediately
            }
        });

        // Long click on item enables multi-selection mode and selects the task
        holder.itemView.setOnLongClickListener(v -> {
            if (!multiSelect) {
                multiSelect = true;        // Enable multi-selection mode
                toggleSelection(task);     // Select this task
                return true;               // Consume the event
            }
            return false;                  // Otherwise do not consume
        });

        // Regular click toggles selection only if multi-select mode is active
        holder.itemView.setOnClickListener(v -> {
            if (multiSelect) {
                toggleSelection(task);
            }
        });

        // Highlight the selected tasks with a gray background
        if (selectedTasks.contains(task)) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.darker_gray));
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);  // Default transparent background
        }
    }

    /**
     * toggleSelection: 🔀
     * Adds or removes the given task from selectedTasks list.
     * Updates UI and notifies listener about the selection count.
     */
    private void toggleSelection(Task task) {
        if (selectedTasks.contains(task)) {
            selectedTasks.remove(task);      // Deselect if already selected
        } else {
            selectedTasks.add(task);         // Select if not selected
        }

        notifyDataSetChanged();              // Refresh UI to show selection state

        if (listener != null) {
            listener.onTaskSelectionChanged(selectedTasks.size());  // Inform listener about selection count
        }
    }

    /**
     * getItemCount: 🔢
     * Returns the total number of tasks currently managed by the adapter.
     */
    @Override
    public int getItemCount() {
        return tasks.size();                 // Size of task list
    }

    /**
     * TaskViewHolder class: 🏷️
     * Holds references to the views in each task item for efficient reuse.
     */
    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView title;     // TextView for displaying task title
        CheckBox checkbox;  // CheckBox for task completion/selection

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.taskTitle);       // Find title TextView by ID
            checkbox = itemView.findViewById(R.id.taskCheckBox); // Find CheckBox by ID
        }
    }
}

