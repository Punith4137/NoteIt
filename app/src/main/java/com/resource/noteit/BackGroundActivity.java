package com.resource.noteit;

// 💡 Used to access system-level services and application resources
import android.content.Context;
// 💡 Used to start new activities (navigate between screens)
import android.content.Intent;
// 💡 Used to store persistent key-value pairs (for saving background selection)
import android.content.SharedPreferences;
// 💡 Used to save and restore state when activity is recreated
import android.net.Uri;
import android.os.Bundle;
// 💡 Used to handle button and view clicks
import android.view.View;
import android.view.ViewGroup;
// 💡 Used to handle clicks on GridView items
// 💡 UI element to display a grid of items
import android.widget.Button;
import android.widget.GridView;
// 💡 UI element that shows an image and acts like a button
import android.widget.ImageButton;
// 💡 UI element to show an image in the layout
import android.widget.ImageView;
// 💡 Basic layout that aligns its children in a single direction vertically or horizontally
import android.widget.LinearLayout;
// 💡 Layout where child views are positioned relative to each other
import android.widget.RelativeLayout;
// 💡 Base adapter class for binding data to views
import android.widget.BaseAdapter;
import android.widget.Toast;

// 💡 Provides backward-compatible functionality for older Android versions
import androidx.annotation.Nullable;
// 💡 Main class for all activities using modern Android components
import androidx.appcompat.app.AppCompatActivity;
// 💡 Used for displaying side navigation drawer
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
// 💡 ViewGroup for toolbar in Material Design
import androidx.appcompat.widget.Toolbar;
// 💡 Synchronizes the state of the navigation drawer and toolbar icon
import androidx.appcompat.app.ActionBarDrawerToggle;
// 💡 Used to create a navigation menu with clickable items
import com.google.android.material.navigation.NavigationView;
// 💡 Layout manager that lets you place widgets relative to each other
import androidx.constraintlayout.widget.ConstraintLayout;

public class BackGroundActivity extends AppCompatActivity {

    // 🔧 Variables to track the selected background image and its position
    private int selectedBackgroundResId = -1; // -1 means nothing selected yet
    private int selectedPosition = -1;

    // 🔧 GridView to show the list of background images
    private GridView gridView;

    // 🔧 Custom adapter to load images in the grid
    private ImageAdapter adapter;

    private static final int PICK_IMAGE_REQUEST = 101; // Request code for image picker
    private Uri selectedImageUri = null; // To hold the selected image URI
    private Button btnChooseFromFiles; // Button for choosing from files


    // 🔧 Drawer and toolbar components
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    // 🔧 ID for the current activity to prevent reloading it again from nav
    final int CURRENT_ACTIVITY_ID = R.id.nav_header_title;

    // 🖼️ List of all background image resource IDs
    private final int[] backgroundImages = {
            R.drawable.bg1, R.drawable.bg2, R.drawable.bg3, R.drawable.bg4,
            R.drawable.bg5, R.drawable.bg6, R.drawable.bg7, R.drawable.bg8
    };

    /**
     * 🚀 onCreate is the entry point of the activity when it is launched.
     * It sets up the layout, toolbar, navigation drawer, loads saved background,
     * and sets listeners for selecting and applying backgrounds. 📲🎨
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // 🧠 Call parent activity's onCreate
        setContentView(R.layout.activity_back_ground); // 🖼️ Set the XML layout

        gridView = findViewById(R.id.gridViewBackgrounds); // 🔍 Find GridView
        ImageButton btnDone = findViewById(R.id.btnDone); // 🔍 Find done button
        LinearLayout root = findViewById(R.id.background_root); // 🔍 Root layout for background

        drawerLayout = findViewById(R.id.drawer_layout); // 🧭 Initialize DrawerLayout
        navigationView = findViewById(R.id.navigation_view); // 🧭 Initialize NavigationView
        toolbar = findViewById(R.id.toolbar); // 🧭 Initialize Toolbar

        setSupportActionBar(toolbar); // 🛠️ Set Toolbar as ActionBar
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer); // 🎛️ Toggle for drawer
        drawerLayout.addDrawerListener(toggle); // 🔄 Attach drawer toggle listener
        toggle.syncState(); // 🔃 Sync state of toggle with drawer

        // 🧭 Handle navigation drawer item clicks
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId(); // 🔍 Get selected menu item ID

            if (id == CURRENT_ACTIVITY_ID) { // 🚫 If it's current activity, close drawer only
                drawerLayout.closeDrawers();
                return true;
            } else {
                // 🔀 Navigate to the selected activity
                if (id == R.id.nav_main) {
                    startActivity(new Intent(this, MainActivity.class));
                } else if (id == R.id.nav_details) {
                    startActivity(new Intent(this, Details.class));
                } else if (id == R.id.nav_tasks) {
                    startActivity(new Intent(this, TasksActivity.class));
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START); // 📕 Close drawer after action
            finish(); // ❌ Finish this activity to prevent stacking
            return true;
        });

        // 💾 Load saved background if exists
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int savedBgId = preferences.getInt("selected_bg", -1); // 🎯 Retrieve saved background
        if (savedBgId != -1) {
            root.setBackgroundResource(savedBgId); // 🎨 Set previously selected background
        }


        adapter = new ImageAdapter(this, backgroundImages); // 🧩 Create image adapter
        gridView.setAdapter(adapter); // 📦 Bind adapter to grid view

        // 🎯 Set click listener for selecting background
        gridView.setOnItemClickListener((adapterView, view, position, id) -> {
            selectedPosition = position; // ✅ Update selected position
            selectedBackgroundResId = backgroundImages[position]; // ✅ Save selected background
            adapter.setSelectedPosition(position); // ✅ Highlight selected item
            adapter.notifyDataSetChanged(); // 🔄 Refresh grid
        });

        // ✅ Save and apply background selection
        btnDone.setOnClickListener(v -> {
            if (selectedBackgroundResId != -1) {
                SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
                editor.putInt("selected_bg", selectedBackgroundResId); // 💾 Save to shared preferences
                editor.apply();

                // 🔁 Restart app to apply changes
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                if (i != null) {
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // 🔃 Clear stack
                    startActivity(i); // 🚀 Restart app
                    finishAffinity(); // 🔚 End all activities
                }
            }
        });
    }


    /**
     * Opens a system file chooser to let the user pick an image from their device.
     */
    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT); // Open file chooser
        intent.setType("image/*"); // Filter for image files only
        intent.addCategory(Intent.CATEGORY_OPENABLE); // Only files that can be opened
        startActivityForResult(intent, PICK_IMAGE_REQUEST); // Launch the picker
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData(); // Get the image URI
            getContentResolver().takePersistableUriPermission(
                    selectedImageUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
            );

            // Save the image URI to Room DB or SharedPreferences
            saveBackgroundUriToPreferences(selectedImageUri.toString());

            // Show a toast or preview to user
            Toast.makeText(this, "Background set from your files!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Stores the selected image URI string to SharedPreferences for later retrieval.
     */
    private void saveBackgroundUriToPreferences(String uriString) {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("custom_bg_uri", uriString); // Save the URI string
        editor.apply(); // Apply changes
    }


    /**
     * 🖼️ ImageAdapter binds background image resources to GridView items.
     * It handles selection and highlighting of the chosen image. 🌟📷
     */
    public class ImageAdapter extends BaseAdapter {

        private final Context context; // 🧠 App context
        private final int[] images; // 🖼️ Array of image resource IDs
        private int selectedPos = -1; // 📍 Position of selected image

        public ImageAdapter(Context c, int[] images) {
            this.context = c;
            this.images = images;
        }

        // 📌 Set the position of selected image
        public void setSelectedPosition(int position) {
            this.selectedPos = position;
        }

        @Override
        public int getCount() {
            return images.length; // 📊 Return number of images
        }

        @Override
        public Object getItem(int i) {
            return images[i]; // 🔄 Return image at position
        }

        @Override
        public long getItemId(int i) {
            return i; // 🔑 Return ID for item
        }

        // 📸 Create and return a view for each grid item
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ImageView imageView;

            if (view == null) {
                imageView = new ImageView(context); // 📷 Create new ImageView
                imageView.setLayoutParams(new GridView.LayoutParams(280, 250)); // 📏 Set size
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP); // 🔍 Crop image to center
                imageView.setPadding(10, 100, 10, 100); // 🧱 Padding inside the grid cell
            } else {
                imageView = (ImageView) view; // ♻️ Reuse existing view
            }

            imageView.setImageResource(images[i]); // 🖼️ Set image for grid item

            if (i == selectedPos) {
                imageView.setBackgroundResource(R.drawable.boorder); // ✅ Highlight selected
            } else {
                imageView.setBackground(null); // ❌ Remove highlight from others
            }

            return imageView; // 🔁 Return configured view
        }
    }
}