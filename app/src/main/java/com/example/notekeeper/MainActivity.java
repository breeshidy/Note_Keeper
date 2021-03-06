package com.example.notekeeper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import static java.sql.DriverManager.println;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private NoteRecyclerAdapter mNoteRecyclerAdapter;
    private NavigationView mNavigationView;
    private NavController mNavController;
    private RecyclerView mRecycleritems;
    private LinearLayoutManager mNoteLayoutManager;
    private GridLayoutManager mCourseLayoutManager;
    private CourseRecyclerAdapter mCourseRecyclerAdapter;
    private static final String TAG = "MainActivity";


    @Override
    public void onBackPressed() {
        //reference to drawer layout
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        {
            //condition for opening and closing drawers
            if(drawer.isDrawerOpen(GravityCompat.START)){
                drawer.closeDrawer(GravityCompat.START);
            }else super.onBackPressed();
        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //pass in the code to add new notes
                startActivity(new Intent(MainActivity.this, NoteActivity.class));
                println("Print fab $fab");
            }
        });

        //saves default to the preference list but does not override it hence "false"
        //initialise default values with our preferences
        PreferenceManager.setDefaultValues(this, R.xml.general_preferences, false);
        PreferenceManager.setDefaultValues(this, R.xml.messages_preferences, false);
        PreferenceManager.setDefaultValues(this, R.xml.notification_preference, false);
        PreferenceManager.setDefaultValues(this, R.xml.sync_preferences, false);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        //ACTION BUTTON acts as an action listener for the navigation drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//                drawer.setDrawerListener(toggle);
//                toggle.syncState();


        mNavigationView = findViewById(R.id.nav_view);


        // TODO: 2020-07-23 The stacked button function is affecting the onNavigationItemSelected.
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        // AppBarConfiguration object to manage the behavior of the Navigation button in the upper-left corner of your app's display area
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_note, R.id.nav_courses, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, mNavController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(mNavigationView, mNavController);

        //implement on selected item
        mNavigationView.setNavigationItemSelectedListener(this);

        initializeDisplayContent();
    }


        @Override
    protected void onResume() {
        super.onResume();
        //update the changes made to the fields
        mNoteRecyclerAdapter.notifyDataSetChanged();
        updateNavHeader();

    }

    private void updateNavHeader() {

        //get a reference for each view
        mNavigationView = findViewById(R.id.nav_view);
        View headerView = mNavigationView.getHeaderView(0);
        TextView textUserName = headerView.findViewById(R.id.text_user_name);
        TextView textEmailAddress = headerView.findViewById(R.id.text_email_address);

        //interact with preference system to get the values of the text views
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        //pass in the key of the preference you want displayed, defValue shows if no preference with that jey is found
        String userName = pref.getString("user_display_name", "");
        String emailAddress = pref.getString("user_email_address", "");

        //assign the "key values" to the text view
        textUserName.setText(userName);
        textEmailAddress.setText(emailAddress);
    }

    private void initializeDisplayContent() {
        //get a reference to the recycler view
        //making it as final means we can reference it in an anonymous class
        mRecycleritems = findViewById(R.id.list_items);
        //pick a type of layout manger
        mNoteLayoutManager = new LinearLayoutManager(this);

        //note we wanna display in our recycler view
        List<NoteInfo> note = DataManager.getInstance().getNotes();

        //create or adapter
        mNoteRecyclerAdapter = new NoteRecyclerAdapter(this, note);
        displayNotes();

        //a 2 column grid layout for the courses
        mCourseLayoutManager = new GridLayoutManager(this, getResources().getInteger(R.integer.course_grid_span));

        //list of courses
        List<CourseInfo> course = DataManager.getInstance().getCourses();

        mCourseRecyclerAdapter = new CourseRecyclerAdapter(this, course);


    }

    //when the setting button is clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_settings){
            Log.d(TAG, "onOptionsItemSelected: settings button clicked");
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void displayNotes() {
        //associate our adapter with our recycler view
        mRecycleritems.setLayoutManager(mNoteLayoutManager);
        mRecycleritems.setAdapter(mNoteRecyclerAdapter);

        selectNavigationMenuItem(R.id.nav_note);

    }

    private void selectNavigationMenuItem(int id) {
        mNavigationView = findViewById(R.id.nav_view);
        Menu menu = mNavigationView.getMenu();
        //set it to always be the highlighted/currently selected menu on nav bar
        menu.findItem(id).setChecked(true);
    }

    private void displayCourses() {
        mRecycleritems.setLayoutManager(mCourseLayoutManager);
        mRecycleritems.setAdapter(mCourseRecyclerAdapter);

        selectNavigationMenuItem(R.id.nav_courses);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void handleSelection(int message_id) {
        //snack bar is text that floats from the bottom
        //to use a snack bar, you have to make reference to the activity you wan the text to display
        View view = findViewById(R.id.list_items);
        Snackbar.make(view, message_id, Snackbar.LENGTH_LONG).show();

    }

    @Override
    public boolean onNavigationItemSelected( MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.nav_note){
            displayNotes();
//            Toast.makeText(getApplicationContext(), "Note", Toast.LENGTH_LONG).show();
            displayNotes();
            Log.d(TAG, "onNavigationItemSelected: display notes ");

        } else if (id == R.id.nav_courses){
            displayCourses();
//            handleSelection("Course");
            Log.d(TAG, "onNavigationItemSelected: display courses");

        }else if (id == R.id.nav_share){
            
            handleShare();

//            handleSelection(R.string.nav_share_message);

        }else if (id == R.id.nav_send){

            handleSelection(R.string.nav_send_messgae);

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    //this get the current value of the user's favourite social from the preference screen
    private void handleShare() {
        View view = findViewById(R.id.list_items);
        Snackbar.make(view, "Share to - " +
                        PreferenceManager.getDefaultSharedPreferences(this).getString("user_favorite_social", ""),
                Snackbar.LENGTH_LONG).show();
    }
}
