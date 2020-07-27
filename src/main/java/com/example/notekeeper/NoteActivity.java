package com.example.notekeeper;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

public class NoteActivity extends AppCompatActivity {

    //pass reference as intent extras. should be CONSTANT
    //to qualify and make the constant value unique
    public static final String NOTE_POSITION = "com.example.notekeeper.NOTE_POSITION";
    public static final int POSITION_NOT_SET = -1;
    private NoteInfo mNote;
    private boolean mIsNewNote;
    private Spinner mSpinnerCourses;
    private EditText mTextNoteTitle;
    private EditText mTextNoteText;
    private int mNotePosition;
    private boolean mIsCancalling;
    //a field of type NoteActivityViewModel
    private NoteActivityViewModel mViewModel;

    @Override
    protected void onPause() {
        super.onPause();
        //while sending an email, the onPause method is called
        if (mIsCancalling) {
            //remove note saved in backing store
            if(mIsNewNote){
                DataManager.getInstance().removeNote(mNotePosition);
            }
            else {
                //to ensure if its an email we can get the original text after sending the email
                storePreviousNoteValues();
            }
        } else {
            saveNote();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null){
            //method in NoteActivityViewModel file
            mViewModel.saveState(outState);
        }
    }

    private void storePreviousNoteValues() {
        //get a reference to the original values and set them
        CourseInfo courses = DataManager.getInstance().getCourse(mViewModel.mOriginalNoteCourseId);
        mNote.setCourse(courses);
        mNote.setTitle(mViewModel.mOriginalNoteTitle);
        mNote.setText(mViewModel.mOriginalNoteText);

    }

    private void saveNote() {

        //Takes values from the field of the screen and adds them to our note
        //set the course to the currently selected item
        mNote.setCourse((CourseInfo) mSpinnerCourses.getSelectedItem());
        mNote.setTitle(mTextNoteTitle.getText().toString());
        mNote.setText(mTextNoteText.getText().toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //create an instance of the class view model
        ViewModelProvider viewModelProvider = new ViewModelProvider(getViewModelStore(), ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()));

        //get reference to view model
        mViewModel = viewModelProvider.get(NoteActivityViewModel.class);

        //restore original value if activity is destroyed
        if (mViewModel.mIsNewlyCreated && savedInstanceState != null)
            mViewModel.restoreState(savedInstanceState);

        mViewModel.mIsNewlyCreated = false;

        //To make reference to the spinner layout
        mSpinnerCourses = findViewById(R.id.spinner_courses);

        //declare a list and give it a variable to get the list
        // of courses in the data manager file
        List<CourseInfo> course = DataManager.getInstance().getCourses();
        //we are using an array adapter (good for lists and arrays)
        ArrayAdapter<CourseInfo> adapterCourses =
                //construct an instance of the array adapter class
                //The parameters accepts a context, a resource and
                // the list object we will pass in
                //simple_spinner_item give us a way to format the selected course
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, course);
        //a way to format our drop down list
        adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //associate the adapter with the spinner
        mSpinnerCourses.setAdapter(adapterCourses);

        //take the intent passed through noteListActivity and display it
        readDisplyStateValues();
        saveOriginalValues();

        mTextNoteTitle = findViewById(R.id.text_note_title);
        mTextNoteText = findViewById(R.id.text_note_text);

        if (!mIsNewNote){
            displayNote(mSpinnerCourses, mTextNoteTitle, mTextNoteText);
        }


    }

    private void saveOriginalValues() {
        if (!mIsNewNote){
            mViewModel.mOriginalNoteCourseId = mNote.getCourse().getCourseId();
            mViewModel.mOriginalNoteTitle = mNote.getTitle();
            mViewModel.mOriginalNoteText = mNote.getText();
        }
    }

    private void displayNote(Spinner spinnerCourses, EditText textNoteTitle, EditText textNoteText) {

        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        //get index of the note clicked
        int courseIndex = courses.indexOf(mNote.getCourse());
        spinnerCourses.setSelection(courseIndex);

        textNoteText.setText(mNote.getText());
        textNoteTitle.setText(mNote.getTitle());

    }

    private void readDisplyStateValues() {
        //get intent that was used to start the activity
        Intent intent = getIntent();

        //get the extra containing note reference. note-info/ changed to position containing
        //mNote = intent.getParcelableExtra(NOTE_POSITION);

        //values types dont have null therefore have to provide a return/default value incase nothing is found
        int position = intent.getIntExtra(NOTE_POSITION, POSITION_NOT_SET);


        //for new note the intent extra would be null
        mIsNewNote = position == POSITION_NOT_SET;
        //get notes that exist
        if(mIsNewNote){
            createNewNote();
        }
        else {
            mNote = DataManager.getInstance().getNotes().get(position);
        }
    }

    private void createNewNote() {
        //create a reference to data manager
        DataManager dm = DataManager.getInstance();
        mNotePosition = dm.createNewNote();
        mNote = dm.getNotes().get(mNotePosition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //menu button
        if (id == R.id.action_send_email) {
            sendEmail();
            return true;
        } else if (id == R.id.action_cancel){
            //set a boolean flag
            mIsCancalling = true;
            //activity will exit and return to previous activity
            finish();
            //upon finishing this activity the onPause method is called

        } else if (id == R.id.action_next){
            moveNext();
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //get a reference to the menu button you would like to disable
        MenuItem menuItem = menu.findItem(R.id.action_next);
        //get the note with an index of no_of_size of note minus 1 i.e last index
        int lastNoteIndex = DataManager.getInstance().getNotes().size()-1;
        //using bool we check if the current note index/position is less than last_note_index
        return super.onPrepareOptionsMenu(menu);
    }

    private void moveNext() {
        //saved changes made to the existing note
        saveNote();

        //advance to next position
        ++mNotePosition;
        mNote = DataManager.getInstance().getNotes().get(mNotePosition);

        //in case a user cancels out
        saveOriginalValues();
        
        displayNote(mSpinnerCourses, mTextNoteTitle, mTextNoteText);

        //re-run onPrepareOption whenever moveNext function is called
        invalidateOptionsMenu();

    }

    private void sendEmail() {
        //add our information to the email.
        CourseInfo course = (CourseInfo) mSpinnerCourses.getSelectedItem();
        //it returns an editable without the .toString()
        String subject = mTextNoteTitle.getText().toString();
        String text = "Checkout what i learned in the pluralsight course \"" + course.getTitle() + "\"\n" + mTextNoteText.getText();

        //create an intent that displays the email
        Intent intent = new Intent(Intent.ACTION_SEND);
        //provide a characteristic
        intent.setType("message/rfc2822"); //standard mimiType for sending email
        //add extras
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);

        startActivity(intent);

    }
}
