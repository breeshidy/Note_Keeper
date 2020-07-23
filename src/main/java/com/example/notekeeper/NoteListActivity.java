package com.example.notekeeper;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.LinearLayout;

import java.util.List;


public class NoteListActivity extends AppCompatActivity {

    private NoteRecyclerAdapter mNoteRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //pass in the code to add new notes
                startActivity(new Intent(NoteListActivity.this, NoteActivity.class));
            }
        });

        initializeDisplayContent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //update the changes made to the fields
        mNoteRecyclerAdapter.notifyDataSetChanged();

    }

    private void initializeDisplayContent() {
        //get a reference to the recycler view
        //making it as final means we can reference it in an anonymous class
        final RecyclerView recyclerNotes = (RecyclerView) findViewById(R.id.list_notes);
        //pick a type of layout manger
        final LinearLayoutManager noteLayoutManager = new LinearLayoutManager(this);
        recyclerNotes.setLayoutManager(noteLayoutManager);

        //note we wanna display in our recycler view
        List <NoteInfo> note = DataManager.getInstance().getNotes();
        //associate our adapter with our recycler view
        mNoteRecyclerAdapter = new NoteRecyclerAdapter(this, note);
        recyclerNotes.setAdapter(mNoteRecyclerAdapter);


    }

}
