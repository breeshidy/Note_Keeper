package com.example.notekeeper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;


//the information for each item would be stored in noteRecycler adapter
public class CourseRecyclerAdapter extends RecyclerView.Adapter<CourseRecyclerAdapter.ViewHolder> {

    //added fields
    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private final List<CourseInfo> mCourses;

    public CourseRecyclerAdapter(Context context, List<CourseInfo> courses) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mCourses = courses;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //create the individual view
        View itemView = mLayoutInflater.inflate(R.layout.item_course_list, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //determines which data to display in each view
        CourseInfo course = mCourses.get(position);
        holder.mTextCourse.setText(course.getTitle());
        //set the current position
        holder.mCurrentPosition = position;

    }

    @Override
    public int getItemCount() {
        return mCourses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //a particular view holder, displays one information at a given time

        public final TextView mTextCourse;

        //not final because the variable would be changed
        public int mCurrentPosition;

        //adds a constructor that pass our view to the surclass of ViewHolder
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextCourse = (TextView) itemView.findViewById(R.id.text_course);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //show a message of title
                    Snackbar.make(v, mCourses.get(mCurrentPosition).getTitle(),
                            Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }
}
