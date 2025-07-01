package com.resource.noteit; // 📦 Declares the package this class belongs to

// 📚 Used to mark this class as a table in the Room database
import androidx.room.Entity;
// 🗝️ Used to specify the primary key for the Room table
import androidx.room.PrimaryKey;

/**
 * 📋 DataClass is a model class for Room database.
 * It represents a "notes" table in the SQLite database through Room ORM.
 * This class is used in your note-taking app to store each note with:
 * - an auto-generated ID 🆔
 * - a title 📝
 * - a description 📄
 * - a timestamp 🕒
 *
 * 💾 This class is essential for saving and retrieving structured note data in the app.
 */
@Entity(tableName = "notes") // 🏷️ Annotates this class as a table named "notes"
public class DataClass {

    @PrimaryKey(autoGenerate = true) // 🗝️ Declares this field as the Primary Key, Room will auto-generate the value
    public int id; // 🆔 Unique ID for each note

    public String dataTitle; // 📝 Title of the note
    public String dataDesc; // 📄 Description/content of the note
    public String dateTime; // 🕒 Date and time when the note was created or modified

    /**
     * 🧱 Default constructor
     * Required by Room to recreate objects from the database automatically.
     */
    public DataClass() {
        // Required empty constructor for Room
    }

    /**
     * 🔨 Parameterized constructor
     * Used when creating a new note object before saving it in the database.
     *
     * @param dataTitle Title of the note
     * @param dataDesc Description of the note
     * @param dateTime Timestamp for the note
     */
    public DataClass(String dataTitle, String dataDesc, String dateTime) {
        this.dataTitle = dataTitle; // 💾 Save the title
        this.dataDesc = dataDesc;   // 💾 Save the description
        this.dateTime = dateTime;   // 💾 Save the date and time
    }

    /**
     * 🛠️ Setter for the note ID
     * Room will call this after generating the ID
     *
     * @param id The unique ID assigned by Room
     */
    public void setId(int id) {
        this.id = id; // 🆔 Assign generated ID
    }

    /**
     * 📤 Getter for the note ID
     *
     * @return ID of the note
     */
    public int getId() {
        return id; // 🆔 Return the current ID
    }

    /**
     * 📤 Getter for the dateTime
     *
     * @return Timestamp of the note
     */
    public String getDateTime() {
        return dateTime; // 🕒 Return date and time of note
    }

    /**
     * 🛠️ Setter for the dateTime
     *
     * @param dateTime New timestamp to set
     */
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime; // 🕒 Set the updated date and time
    }
}

