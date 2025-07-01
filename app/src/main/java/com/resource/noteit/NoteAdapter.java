package com.resource.noteit;

// 📦 Android and Java imports used in this adapter class:
// Context: Provides access to app-specific resources and classes, used here to start activities and inflate layouts.
// Intent: Used to start UploadActivity with extra data for editing notes.
// Color: Used to set dialog button text colors.
// TextUtils: Utility class to handle text operations like checking if input is empty.
// View and its sub-classes (ViewGroup, LayoutInflater, etc.): To inflate and manage views in RecyclerView items.
// Widget classes (TextView, ImageView, AlertDialog): To display UI components and dialogs.
// androidx.annotation.NonNull: For null safety annotations.
// androidx.recyclerview.widget.RecyclerView: Base class for the adapter and view holder pattern.
// java.util.List & ArrayList: To manage collections of note data.
// android.widget.Filter & Filterable: To implement search filtering in the adapter.

import android.app.AlertDialog;                 // For creating confirmation dialog on delete
import android.content.Context;                  // To access resources and start activities
import android.content.Intent;                   // To start UploadActivity with data
import android.graphics.Color;                   // To set colors of dialog buttons
import android.text.TextUtils;                   // To check if search query is empty
import android.view.LayoutInflater;              // To inflate XML layout into View
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;                     // To filter notes based on search query
import android.widget.Filterable;                 // Interface to add filtering capability
import android.widget.ImageView;                  // To display delete icon button
import android.widget.TextView;                   // To display note title, description and date/time

import androidx.annotation.NonNull;               // For null safety in overridden methods
import androidx.recyclerview.widget.RecyclerView; // RecyclerView adapter and view holder base classes

import java.util.ArrayList;                       // Dynamic array for full note list backup
import java.util.List;                            // List interface for notes collection


/**
 * NoteAdapter 📒📋
 *
 * This adapter is responsible for binding a list of DataClass note objects
 * to the RecyclerView in the UI. It displays each note's title, truncated description,
 * and timestamp. It also handles click events for editing and deleting notes.
 *
 * Additionally, it implements Filterable to allow searching notes by title.
 *
 * Used for:
 * - Displaying notes efficiently in a scrollable list
 * - Handling note deletion with confirmation dialog
 * - Navigating to edit screen with selected note data
 * - Filtering notes based on search queries in real-time
 */
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> implements Filterable {

    // 🌍 Context of the calling activity or fragment for inflating views and starting intents
    Context context;

    // 📝 Current list of notes displayed in the RecyclerView
    List<DataClass> notes;

    // 🔄 Full unfiltered list of notes used for restoring and filtering
    List<DataClass> notesFull;

    // 📋 DAO object to perform database operations like delete
    NoteDao noteDao;

    // 🔍 Boolean flag to check if filter search has any matching notes
    private boolean hasMatch = true;


    /**
     * Constructor for NoteAdapter
     * Initializes adapter with context, notes list and NoteDao
     * Makes a copy of full notes list for filtering
     */
    public NoteAdapter(Context context, List<DataClass> notes, NoteDao dao) {
        this.context = context;                   // Assign context from caller
        this.notes = notes;                       // Assign the current filtered list
        this.noteDao = dao;                       // Assign NoteDao for DB ops
        this.notesFull = new ArrayList<>(notes); // Backup full list for search filtering
    }


    /**
     * Creates and returns a new ViewHolder object
     * Inflates the item_note.xml layout for each item
     *
     * @param parent The parent ViewGroup that will hold the item views
     * @param viewType The type of the new View
     * @return A new NoteViewHolder instance containing the inflated item view
     */
    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the single note item layout into a View
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_note, parent, false);
        // Create and return a new ViewHolder with the inflated view
        return new NoteViewHolder(itemView);
    }


    /**
     * Binds data from the note at the given position to the ViewHolder
     * Also sets click listeners for item click and delete button
     *
     * @param holder The ViewHolder to bind data to
     * @param position The position of the item in the data list
     */
    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        DataClass note = notes.get(position);      // Get the note object for this position

        holder.title.setText(note.dataTitle);      // Set note title text in TextView
        // Set truncated description (max 30 chars + ellipsis if longer)
        holder.desc.setText(note.dataDesc.length() > 30 ? note.dataDesc.substring(0, 30) + "..." : note.dataDesc);
        holder.dateTimeTextView.setText(note.getDateTime()); // Set the formatted date/time string

        // Set click listener on whole item to open UploadActivity for editing
        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, UploadActivity.class);  // Create intent to open UploadActivity
            i.putExtra("edit", true);                               // Pass flag to indicate edit mode
            i.putExtra("id", note.id);                              // Pass note ID for editing
            i.putExtra("title", note.dataTitle);                    // Pass note title
            i.putExtra("desc", note.dataDesc);                      // Pass note description
            context.startActivity(i);                               // Start the edit activity
        });

        // Set click listener for delete button to show confirmation dialog
        holder.deleteBtn.setOnClickListener(v -> {
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle("Delete Note")                           // Dialog title
                    .setMessage("Are you sure you want to delete this note?") // Confirmation message
                    .setIcon(R.drawable.ic_dialog_alert)              // Warning icon
                    .setPositiveButton("Yes", (dialogInterface, which) -> {
                        // If confirmed, delete note from database and update list & UI
                        if (position >= 0 && position < notes.size()) {
                            noteDao.delete(notes.get(position));       // Delete note via DAO
                            notes.remove(position);                     // Remove note from list
                            notifyItemRemoved(position);                // Notify adapter item removed
                            notifyItemRangeChanged(position, notes.size()); // Notify range changed for smooth animation
                        }
                    })
                    .setNegativeButton("No", null)                    // Cancel button does nothing
                    .create();

            // Customize dialog button colors to red on show
            dialog.setOnShowListener(dialogInterface -> {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
            });

            dialog.show();                                         // Show the dialog
        });
    }


    /**
     * Returns the current number of notes displayed in the RecyclerView
     * @return Size of notes list
     */
    @Override
    public int getItemCount() {
        return notes.size();
    }


    /**
     * Custom ViewHolder class holds references to the views for each note item
     * Improves performance by avoiding repeated findViewById calls
     */
    class NoteViewHolder extends RecyclerView.ViewHolder {

        TextView title, desc;              // Views for note title and description
        TextView dateTimeTextView;        // View for formatted date/time string
        ImageView deleteBtn;              // Delete button view

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize views by finding them inside the inflated item layout
            title = itemView.findViewById(R.id.noteTitle);
            desc = itemView.findViewById(R.id.noteDesc);
            dateTimeTextView = itemView.findViewById(R.id.textViewDateTime);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }
    }


    /**
     * Returns whether the current filter search query found any matching notes
     * Useful to show "no results found" UI outside the adapter if needed
     * @return true if filter has matches, false otherwise
     */
    public boolean hasMatch() {
        return hasMatch;
    }


    /**
     * Returns the Filter object used to perform search filtering on the notes list
     * @return Filter instance for filtering notes by title
     */
    @Override
    public Filter getFilter() {
        return noteFilter;
    }


    /**
     * Custom Filter implementation to search notes by their title
     * Filters notesFull backup list and updates the displayed notes list accordingly
     */
    private Filter noteFilter = new Filter() {

        /**
         * Performs filtering operation on background thread
         * @param constraint The search query string
         * @return FilterResults containing filtered notes list
         */
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<DataClass> filteredList = new ArrayList<>();

            // If search query is empty or null, show full list
            if (TextUtils.isEmpty(constraint)) {
                filteredList.addAll(notesFull);
                hasMatch = true;      // We have matches (all notes)
            } else {
                // Normalize query to lowercase and trimmed
                String filterPattern = constraint.toString().toLowerCase().trim();

                // Loop through full notes list and add matching notes to filteredList
                for (DataClass note : notesFull) {
                    if (note.dataTitle.toLowerCase().contains(filterPattern)) {
                        filteredList.add(note);
                    }
                }

                // Set hasMatch flag depending on if any notes matched
                hasMatch = !filteredList.isEmpty();
            }

            // Wrap filtered list into FilterResults and return
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        /**
         * Publishes filtering results on the UI thread by updating adapter data
         * @param constraint The search query string
         * @param results The FilterResults returned by performFiltering
         */
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            notes.clear();                             // Clear current displayed list
            notes.addAll((List<DataClass>) results.values); // Add filtered results
            notifyDataSetChanged();                    // Notify adapter to refresh RecyclerView
        }
    };
}

