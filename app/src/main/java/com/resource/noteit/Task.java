package com.resource.noteit;

/**
 * Task 📋🔥
 *
 * This class represents a Task item used to manage individual tasks in a task list.
 * Each Task has a title, a completion status, and an optional group/category it belongs to.
 *
 * Why use this Task class?
 * - Encapsulates task data and behavior (title, completed status, group)
 * - Provides getter/setter methods to access and modify task properties safely
 * - Overrides equals() and hashCode() so tasks can be compared by title, enabling
 *   correct behavior when using collections like List.contains() or List.remove()
 */
public class Task {

    private String title;        // Title or name of the task
    private boolean completed = false; // Status indicating if task is completed, defaults to false
    private String group = null; // Optional group/category the task belongs to (null if none)

    /**
     * Constructor to create a Task with a title.
     *
     * @param title The title of the task.
     */
    public Task(String title) {
        this.title = title; // Initialize the task title with the provided value
    }

    /**
     * Gets the task title.
     *
     * @return String The current title of the task.
     */
    public String getTitle() {
        return title; // Return the title field
    }

    /**
     * Sets or updates the task title.
     *
     * @param title New title to set for the task.
     */
    public void setTitle(String title) {
        this.title = title; // Update the title field
    }

    /**
     * Checks if the task is completed.
     *
     * @return boolean True if completed, false otherwise.
     */
    public boolean isCompleted() {
        return completed; // Return the completed status
    }

    /**
     * Sets the completed status of the task.
     *
     * @param completed True to mark as completed, false to mark as not completed.
     */
    public void setCompleted(boolean completed) {
        this.completed = completed; // Update the completed status
    }

    /**
     * Gets the group/category of the task.
     *
     * @return String The group name or null if not assigned.
     */
    public String getGroup() {
        return group; // Return the group/category field
    }

    /**
     * Sets or updates the group/category of the task.
     *
     * @param group The new group name to assign.
     */
    public void setGroup(String group) {
        this.group = group; // Update the group field
    }

    /**
     * Override equals() method to compare Task objects by their title.
     * This helps in determining equality when adding/removing tasks from collections.
     *
     * @param o Object to compare against.
     * @return boolean True if titles match, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;                // If same object reference, return true
        if (!(o instanceof Task)) return false;    // If not a Task instance, return false
        Task task = (Task) o;                       // Cast the object to Task
        return title.equals(task.title);            // Compare tasks by title equality
    }

    /**
     * Override hashCode() method to generate hash based on the title.
     * Required whenever equals() is overridden for consistent hashing behavior.
     *
     * @return int Hash code derived from the task's title.
     */
    @Override
    public int hashCode() {
        return title.hashCode();                     // Return hash code of the title string
    }
}
