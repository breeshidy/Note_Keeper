package com.example.notekeeper;

import android.os.Bundle;

import androidx.lifecycle.ViewModel;

public class NoteActivityViewModel extends ViewModel {
    //if made private, we need getters and setters to access them in other classes
    public static final String ORIGINAL_NOTE_COURSE_ID = "com.example.notekeeper.ORIGINAL_NOTE_COURSE_ID";
    public static final String ORIGINAL_NOTE_TITLE= "com.example.notekeeper.ORIGINAL_NOTE_TITLE";
    public static final String ORIGINAL_NOTE_TEXT = "com.example.notekeeper.ORIGINAL_NOTE_TEXT";

    public String mOriginalNoteCourseId;
    public String mOriginalNoteTitle;
    public String mOriginalNoteText;
    public boolean mIsNewlyCreated = true;

    public void saveState(Bundle outState) {
        //our values get saved on the bundle so when our Note Activity and View Model gets destroyed, the values would be retained in the Bundle
        outState.putString(ORIGINAL_NOTE_COURSE_ID, mOriginalNoteCourseId);
        outState.putString(ORIGINAL_NOTE_TITLE, mOriginalNoteTitle);
        outState.putString(ORIGINAL_NOTE_TEXT, mOriginalNoteText);

    }

    //get the bundle for save state
    public void restoreState(Bundle intState){
        mOriginalNoteCourseId = intState.getString(ORIGINAL_NOTE_COURSE_ID);
        mOriginalNoteTitle = intState.getString(ORIGINAL_NOTE_TITLE);
        mOriginalNoteText = intState.getString(ORIGINAL_NOTE_TEXT);
    }
}
