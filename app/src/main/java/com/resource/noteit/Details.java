package com.resource.noteit; // 📦 This is the package name where the class resides

// 📚 Used to save and retrieve key-value pairs of primitive data types
import android.content.SharedPreferences;
// 📦 Handles app activity lifecycle and UI
import android.os.Bundle;
// 🧱 Layout container in XML
import android.widget.LinearLayout;
// ✅ Enables drawing edge-to-edge content
import androidx.activity.EdgeToEdge;
// 🏛️ Base class for activities using the support library action bar features
import androidx.appcompat.app.AppCompatActivity;
// 🎯 Layout for arranging views in a constraint-based system
import androidx.constraintlayout.widget.ConstraintLayout;
// 📦 Represents the insets of the system bars
import androidx.core.graphics.Insets;
// 👂 Lets you modify how insets like status bar are handled
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
// 📦 Used to launch new activities
import android.content.Intent;
// 🎯 Handles item selection in menus
import android.view.MenuItem;
// 🎯 Base layout for navigation drawer
import androidx.drawerlayout.widget.DrawerLayout;
// 🎨 Toolbar widget replacement for ActionBar
import androidx.appcompat.widget.Toolbar;
// 🔁 Toggles the navigation drawer
import androidx.appcompat.app.ActionBarDrawerToggle;
// 📑 Navigation menu used in navigation drawer
import com.google.android.material.navigation.NavigationView;
// 📦 Handles window insets like status bar
import androidx.core.view.WindowInsetsCompat;

/**
 * 📄 Details.java
 * ✅ This activity is part of the app's navigation system and represents a screen where users might see some detailed info.
 * 📋 It supports a navigation drawer to switch between Main, Background, and Task screens.
 * 🎨 It also applies a background stored in SharedPreferences, allowing dynamic UI customization.
 */
public class Details extends AppCompatActivity {

    final int CURRENT_ACTIVITY_ID = R.id.nav_header_title; // 🆔 ID to check if current activity is selected in nav drawer

    DrawerLayout drawerLayout; // 📦 Navigation drawer container
    NavigationView navigationView; // 📑 Navigation menu
    Toolbar toolbar; // 🎨 App bar at the top

    /**
     * 🚀 Called when the activity is first created.
     * ✅ Sets up the navigation drawer, background, and toolbar back button.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // 🟢 Call to parent class to initialize
        EdgeToEdge.enable(this); // ✅ Enables edge-to-edge display
        setContentView(R.layout.activity_details); // 🖼️ Sets the layout file for this activity

        // 🧱 Handles system UI insets like status bar, navigation bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cons_Details), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars()); // 📥 Get insets for status/nav bars
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom); // 🖍️ Apply padding to view
            return insets; // 🔄 Return the same insets
        });

        drawerLayout = findViewById(R.id.drawer_layout); // 🔧 Link DrawerLayout from XML
        navigationView = findViewById(R.id.navigation_view); // 🔧 Link NavigationView from XML
        toolbar = findViewById(R.id.toolbar); // 🔧 Link Toolbar from XML

        setSupportActionBar(toolbar); // 🏗️ Set the Toolbar to act as the ActionBar
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer); // 🔁 Drawer toggle button
        drawerLayout.addDrawerListener(toggle); // 👂 Listen for drawer state changes
        toggle.syncState(); // 🔄 Sync the toggle state

        ConstraintLayout layout3 = findViewById(R.id.cons_Details); // 🔧 Get reference to main content layout

        int bgId1 = getSharedPreferences("AppPrefs", MODE_PRIVATE).getInt("background_id", R.drawable.bg1); // 🧠 Get saved background or use default
        layout3.setBackgroundResource(bgId1); // 🎨 Apply background to layout

        ConstraintLayout root = findViewById(R.id.cons_Details); // 🔧 Duplicate reference (already done above, but required here again for second background)
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE); // 📂 Access another preference file
        int bgId = preferences.getInt("selected_bg", -1); // 🧠 Get selected background
        if (bgId != -1) {
            root.setBackgroundResource(bgId); // 🎨 Apply selected background if exists
        }

        // 🧭 Handle navigation menu item selection
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId(); // 🆔 Get selected item's ID

            if (id == CURRENT_ACTIVITY_ID) { // 🔁 If user taps current screen
                drawerLayout.closeDrawers(); // ❌ Close drawer
                return true;
            } else if (id != CURRENT_ACTIVITY_ID) {
                if (id == R.id.nav_main) { // 👉 Navigate to Main
                    startActivity(new Intent(this, MainActivity.class));
                } else if (id == R.id.nav_change_background) { // 👉 Navigate to Background selector
                    startActivity(new Intent(this, BackGroundActivity.class));
                } else if (id == R.id.nav_tasks) { // 👉 Navigate to Tasks
                    startActivity(new Intent(this, TasksActivity.class));
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START); // ❌ Close drawer after selection
            finish(); // 🛑 Finish current activity so it doesn't stack
            return true;
        });

        toolbar = findViewById(R.id.toolbar); // 🔧 Toolbar reference (already done above, redundant but harmless)
        setSupportActionBar(toolbar); // 🏗️ Set Toolbar again (redundant)

        // 🔙 Enable the back button on the ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // ⬅️ Show back arrow
        }
    }

    /**
     * 🔙 Handles the ActionBar back button click
     * 👉 Navigates back to MainActivity when the back arrow is tapped
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { // ⬅️ If back arrow is clicked
            Intent intent = new Intent(this, MainActivity.class); // 🎯 Create intent for MainActivity
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 🧹 Clear all activities on top of Main
            startActivity(intent); // 🚀 Launch MainActivity
            finish(); // 🛑 Kill current activity
            return true;
        }
        return super.onOptionsItemSelected(item); // 🔁 Otherwise, default behavior
    }
}
