package com.resource.noteit;

// 🔄 Used to launch new activities (pages)
import android.content.Intent;
// 🗃️ Used to store small persistent key-value data
import android.content.SharedPreferences;
// 📦 Base class for activities that use the modern Android features
import android.os.Bundle;
// ⏳ Used to create delay before showing toast message if no results
import android.os.Handler;
// 🍞 Used to show short messages on screen
import android.widget.Toast;

// 🧭 Toolbar support for newer Android versions
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
// 🔍 Custom Search bar widget
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
// 🎯 For using constraint-based layout
import androidx.constraintlayout.widget.ConstraintLayout;
// 📦 For layout background inset handling
import androidx.core.view.GravityCompat;
// 🗂️ To create a side drawer (navigation menu)
import androidx.drawerlayout.widget.DrawerLayout;
// 🔃 Scrollable list view
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// ➕ Floating add button
import com.google.android.material.floatingactionbutton.FloatingActionButton;
// 📜 View that shows navigation items in the drawer
import com.google.android.material.navigation.NavigationView;

import java.util.List;

/**
 * 📘 MainActivity
 * This activity serves as the home screen where the user sees all saved notes.
 * It includes:
 * - Navigation drawer to switch between other activities
 * - RecyclerView to show notes
 * - FAB to add new notes
 * - Search functionality to filter notes
 * - Background preference loading 🔥
 */

public class MainActivity extends AppCompatActivity {

    // 🌟 Drawer and navigation components
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    // ➕ Button to add notes
    FloatingActionButton fab;

    // 📜 Note list and related objects
    RecyclerView recyclerView;
    NoteAdapter adapter;
    NoteDatabase db;
    List<DataClass> notes;

    final int CURRENT_ACTIVITY_ID = R.id.nav_header_title; // 🎯 ID to prevent reloading same page
    SearchView searchView; // 🔍 Search bar

    // ⏳ Used for delaying toast if no result is found
    private Handler handler = new Handler();
    private Runnable searchTimeoutRunnable;

    /**
     * 🚀 onCreate - Initializes the activity and UI components
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); // ☀️ Disable dark mode
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // 🎨 Set the layout

        // 🔧 Setup toolbar and drawer
        toolbar = findViewById(R.id.toolbar); // 🔍 Find toolbar by ID
        setSupportActionBar(toolbar); // ✅ Set as action bar

        drawerLayout = findViewById(R.id.drawer_layout); // 🗂️ Main drawer layout
        navigationView = findViewById(R.id.nav_view); // 📜 Navigation side menu

        // 🎨 Set background based on shared preferences (default is bg1)
        ConstraintLayout layout3 = findViewById(R.id.cons_main);
        int bgId1 = getSharedPreferences("AppPrefs", MODE_PRIVATE).getInt("background_id", R.drawable.bg1);
        layout3.setBackgroundResource(bgId1);

        // 🔍 Configure search bar
        searchView = findViewById(R.id.searchBar);
        searchView.setIconifiedByDefault(false); // 📂 Show search open by default
        searchView.setQueryHint("Search notes..."); // 💬 Hint text
        searchView.setSubmitButtonEnabled(false); // ❌ No submit button

        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) searchView.clearFocus(); // 🧹 Clear keyboard if lost focus
        });

        // 🔁 Search logic with delayed toast
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus(); // 🧹 Hide keyboard
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (searchTimeoutRunnable != null) {
                    handler.removeCallbacks(searchTimeoutRunnable); // ❌ Cancel previous timeout
                }

                if (adapter != null) {
                    adapter.getFilter().filter(newText); // 🔍 Filter notes
                }

                searchTimeoutRunnable = () -> {
                    if (adapter != null && !adapter.hasMatch()) {
                        Toast.makeText(MainActivity.this, "Searched note is not present", Toast.LENGTH_SHORT).show(); // 🍞 Show toast
                    }
                };

                handler.postDelayed(searchTimeoutRunnable, 1000); // ⏳ Delay toast for 1 sec
                return true;
            }
        });

        // 🔗 Toggle button (hamburger icon)
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle); // 📥 Add toggle to drawer
        toggle.syncState(); // 🔃 Sync toggle icon state

        // 🗂️ Handle navigation drawer item clicks
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == CURRENT_ACTIVITY_ID) {
                drawerLayout.closeDrawers();
                return true;
            } else if (id != CURRENT_ACTIVITY_ID) {
                if (id == R.id.nav_change_background) {
                    startActivity(new Intent(this, BackGroundActivity.class));
                } else if (id == R.id.nav_details) {
                    startActivity(new Intent(this, Details.class));
                } else if (id == R.id.nav_tasks) {
                    startActivity(new Intent(this, TasksActivity.class));
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START); // 📤 Close drawer
            return true;
        });

        // 🎨 Set saved background from MyPrefs
        ConstraintLayout root = findViewById(R.id.cons_main);
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int bgId = preferences.getInt("selected_bg", -1);
        if (bgId != -1) {
            root.setBackgroundResource(bgId);
        }

        // 📝 Setup notes list & FAB
        fab = findViewById(R.id.fab); // ➕ Add note button
        recyclerView = findViewById(R.id.recyclerView); // 🧾 Note list

        db = NoteDatabase.getInstance(this); // 🗃️ Get DB instance
        loadNotes(); // 🔄 Load saved notes

        fab.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, UploadActivity.class))); // ➕ Go to Upload
    }

    /**
     * 🔁 onResume - Refresh notes when coming back from other activities
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadNotes(); // 🔄 Reload notes
    }

    /**
     * 📥 loadNotes - Load notes from the Room database and display them
     */
    private void loadNotes() {
        notes = db.noteDao().getAllNotes(); // 📥 Fetch all notes
        adapter = new NoteAdapter(this, notes, db.noteDao()); // 🔧 Set adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // 📊 Layout
        recyclerView.setAdapter(adapter); // 📜 Attach adapter
    }
}
