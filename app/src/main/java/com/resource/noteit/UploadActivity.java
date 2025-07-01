package com.resource.noteit;

// 🌟 Import statements used for various Android components and utilities

import android.content.Intent;                   // For starting new activities or navigating between screens
import android.content.SharedPreferences;       // To store and retrieve simple key-value pairs persistently
import android.os.Bundle;                        // Represents the state of an activity; used in lifecycle methods
import android.view.MenuItem;                    // Represents individual items in a menu, such as toolbar buttons
import android.widget.Button;                    // UI widget for clickable buttons
import android.widget.EditText;                  // UI widget to get user input as text
import android.widget.RelativeLayout;            // A type of ViewGroup for layout design using relative positioning
import android.widget.Toast;                     // Small popup messages to notify users
import androidx.appcompat.app.AppCompatActivity; // Base class for activities with ActionBar support
import androidx.appcompat.widget.Toolbar;         // Widget to add a customizable Toolbar (ActionBar) to the activity

import java.text.SimpleDateFormat;               // Utility to format dates to string representation
import java.util.Date;                           // Represents date and time
import java.util.Locale;                         // Provides locale information for localization

/*
 * UploadActivity 📝
 * This activity allows users to upload or edit notes with a title and description.
 * It also manages saving data to the local database and sets up UI elements like toolbar and background.
 * This activity uses an AppCompatActivity for modern Android support and a Toolbar for navigation.
 */
public class UploadActivity extends AppCompatActivity {

    Button saveButton;             // Button to save the note data
    EditText uploadTitle, uploadDesc;  // Input fields for note title and description
    NoteDatabase db;               // Instance of local database to store notes
    boolean isEdit = false;        // Flag to check if current operation is editing an existing note
    int noteId;                   // Stores the id of the note to be edited (if any)

    /*
     * onCreate method 🎬
     * Lifecycle method called when activity is created.
     * Sets the UI layout, initializes views, handles intent data (edit mode), toolbar setup, background,
     * and sets the click listener for saving data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);                       // Call parent method to create activity
        setContentView(R.layout.activity_upload);                 // Set the XML layout for this activity

        uploadTitle = findViewById(R.id.uploadTopic);             // Find EditText for title input
        uploadDesc = findViewById(R.id.uploadDesc);               // Find EditText for description input
        saveButton = findViewById(R.id.saveButton);               // Find Button for save action
        db = NoteDatabase.getInstance(this);                       // Get database instance for note operations

        RelativeLayout layout3 = findViewById(R.id.rel_upload);   // Get root RelativeLayout to set background

        Toolbar toolbar = findViewById(R.id.toolbar);             // Find Toolbar widget from layout
        setSupportActionBar(toolbar);                              // Set the Toolbar as the ActionBar

        // Enable the back arrow in toolbar for navigation back
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Show back button in toolbar
        }

        // Retrieve saved background id from shared preferences (default to R.drawable.bg1)
        int bgId2 = getSharedPreferences("AppPrefs", MODE_PRIVATE)
                .getInt("background_id", R.drawable.bg1);
        layout3.setBackgroundResource(bgId2);                      // Set background resource to the layout

        // Check if this activity was opened for editing an existing note
        if (getIntent().hasExtra("edit")) {
            isEdit = true;                                         // Mark as edit mode
            uploadTitle.setText(getIntent().getStringExtra("title")); // Set title field with existing note title
            uploadDesc.setText(getIntent().getStringExtra("desc"));  // Set description field with existing note desc
            noteId = getIntent().getIntExtra("id", -1);            // Get note ID for update
        }

        // Another background setting based on different SharedPreferences (if selected_bg is saved)
        RelativeLayout root = findViewById(R.id.rel_upload);       // Get the layout again (can be optimized)
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int bgId = preferences.getInt("selected_bg", -1);          // Retrieve selected background id
        if (bgId != -1) {
            root.setBackgroundResource(bgId);                      // Set background resource if valid
        }

        // Set click listener on saveButton to trigger saving data
        saveButton.setOnClickListener(view -> saveData());
    }

    /*
     * saveData method 💾
     * This method extracts user input from EditTexts,
     * validates the data, formats the current date and time,
     * and inserts or updates the note in the database accordingly.
     * Finally, it closes the activity to return to the previous screen.
     */
    public void saveData() {
        String title = uploadTitle.getText().toString().trim();   // Get trimmed title text from input field
        String desc = uploadDesc.getText().toString().trim();     // Get trimmed description text from input field

        // Check if either field is empty and show warning Toast
        if (title.isEmpty() || desc.isEmpty()) {
            Toast.makeText(this, "Title and Description can't be empty!", Toast.LENGTH_SHORT).show();
            return;                                               // Exit method early if validation fails
        }

        // Format current date and time as "dd MMM yyyy, hh:mm a" (e.g. 09 Jun 2025, 05:30 PM)
        String currentDateTime = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                .format(new Date());

        // If editing, create a DataClass instance with ID and update the existing note
        if (isEdit) {
            DataClass note = new DataClass(title, desc, currentDateTime); // Create note object with updated info
            note.setId(noteId);                                          // Set the existing note's ID
            db.noteDao().update(note);                                   // Update note in database
        } else {
            // Otherwise, insert a new note into the database
            db.noteDao().insert(new DataClass(title, desc, currentDateTime));
        }

        finish();                                                     // Close activity and return to previous
    }

    /*
     * onOptionsItemSelected method 🔙
     * Handles toolbar item selections.
     * Specifically handles back arrow press in the toolbar to navigate to MainActivity.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {                // If back arrow in toolbar pressed
            Intent intent = new Intent(this, MainActivity.class);    // Create intent to go back to MainActivity
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);        // Optional: clears activity stack on top
            startActivity(intent);                                   // Start MainActivity
            finish();                                               // Finish current UploadActivity
            return true;                                            // Indicate event handled
        }
        return super.onOptionsItemSelected(item);                   // Default handling for other items
    }
}
