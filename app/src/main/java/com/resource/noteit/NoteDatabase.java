package com.resource.noteit;

// 📦 Android and Room imports:
// Context: Provides access to application-specific resources and classes, used to build the DB instance
// @Database: Annotation to define the Room database and its entities
// Room: Utility class to create the database builder
// RoomDatabase: Base class for Room database implementations

import android.content.Context;                // Used to get application context for database building
import androidx.room.Database;                 // Marks this class as a Room database
import androidx.room.Room;                     // Provides database builder to create Room database instance
import androidx.room.RoomDatabase;             // Base class for Room database to extend


/**
 * NoteDatabase 🏛️📚
 *
 * This is the main Room database class for the app.
 * It serves as the database holder and is the main access point for the underlying SQLite database connection.
 *
 * Why use NoteDatabase?
 * - Defines the database configuration (entities and version)
 * - Provides a singleton instance to ensure only one database connection exists throughout the app lifecycle
 * - Provides access to DAO interfaces to perform database operations
 */
@Database(entities = {DataClass.class}, version = 2)  // Defines entities and database version for migration
public abstract class NoteDatabase extends RoomDatabase {

    // Abstract method to get the DAO interface for notes
    public abstract NoteDao noteDao();  // Room generates implementation to provide DAO methods

    // Singleton instance of NoteDatabase to avoid multiple instances and resource leaks
    private static NoteDatabase INSTANCE;

    /**
     * Returns the singleton instance of NoteDatabase.
     * Uses synchronized method to ensure thread-safety while creating the instance.
     * Creates the database using Room's databaseBuilder with:
     * - application context to avoid leaks
     * - destructive migration fallback to reset DB if no migration provided
     * - allowMainThreadQueries() for simplicity (not recommended for production)
     *
     * @param context Application context used for database creation
     * @return NoteDatabase Singleton instance of the Room database
     */
    public static synchronized NoteDatabase getInstance(Context context) {
        if (INSTANCE == null) {  // Check if database instance already exists
            INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),  // Use application context to prevent leaks
                            NoteDatabase.class,               // Specify the Room database class
                            "note_db")                       // Database name
                    .allowMainThreadQueries()        // Allows DB queries on main thread (avoid in production)
                    .fallbackToDestructiveMigration() // Recreates DB if migration strategy not specified on version change
                    .build();                       // Build the Room database instance
        }
        return INSTANCE;  // Return the singleton instance
    }
}
