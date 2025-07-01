package com.resource.noteit;

// 🏛️ Room Persistence Library imports:
// @Dao: Marks this interface as a DAO (Data Access Object) to provide DB operations
// @Insert, @Update, @Delete: Annotations for insert, update, and delete queries automatically handled by Room
// @Query: Allows writing custom SQL queries to fetch data from the database

import androidx.room.Dao;          // Marks the interface as DAO for Room database operations
import androidx.room.Delete;       // Used to annotate delete operations
import androidx.room.Insert;       // Used to annotate insert operations
import androidx.room.Query;        // Used to write custom SQL queries
import androidx.room.Update;       // Used to annotate update operations

import java.util.List;             // For returning a list of DataClass objects


/**
 * NoteDao 🗃️📋
 *
 * DAO stands for Data Access Object.
 * It is an interface that provides methods for interacting with the Room database,
 * abstracting the actual SQL queries and allowing easy CRUD (Create, Read, Update, Delete) operations on DataClass entities.
 *
 * Why use DAO?
 * - Separates database logic from UI/business logic
 * - Provides a clean API to access the database
 * - Room auto-generates the implementation based on the annotations, so you don’t write raw SQL manually (except with @Query)
 *
 * This NoteDao interface declares methods to:
 * - Insert new notes
 * - Update existing notes
 * - Delete notes
 * - Fetch all notes
 */
@Dao
public interface NoteDao {

    /**
     * Updates an existing note in the database.
     * The note object must have a valid primary key to identify which record to update.
     *
     * @param note The DataClass note object with updated fields to be saved.
     */
    @Update
    void update(DataClass note);   // Automatically generates SQL UPDATE based on the note's primary key


    /**
     * Inserts a new note into the database.
     * If a conflict occurs (e.g., same primary key), Room will throw an error unless conflict strategy is defined.
     *
     * @param note The new DataClass note to be inserted.
     */
    @Insert
    void insert(DataClass note);   // Automatically generates SQL INSERT statement


    /**
     * Deletes a note from the database.
     * The note must have a primary key to identify which record to delete.
     *
     * @param note The DataClass note to be deleted.
     */
    @Delete
    void delete(DataClass note);   // Automatically generates SQL DELETE statement


    /**
     * Retrieves all notes from the "notes" table.
     * This query returns a list of all notes currently stored in the database.
     *
     * @return List<DataClass> A list of all notes.
     */
    @Query("SELECT * FROM notes")
    List<DataClass> getAllNotes(); // Custom query to fetch all notes from the table
}
