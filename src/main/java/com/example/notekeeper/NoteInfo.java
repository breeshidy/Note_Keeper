package com.example.notekeeper;

import android.os.Parcel;
import android.os.Parcelable;

public final class NoteInfo implements Parcelable {
    private CourseInfo mCourse;
    private String mTitle;
    private String mText;

    public NoteInfo(CourseInfo course, String title, String text) {
        mCourse = course;
        mTitle = title;
        mText = text;
    }

    //cannot be called out NoteInfo class
    private NoteInfo(Parcel parcel) {

        //parcel values must be accessed in the same way they were written (wireToParcel) because no identifiers

       //the loader class provides information on how to create instances of a type
        mCourse = parcel.readParcelable(CourseInfo.class.getClassLoader());

        mTitle = parcel.readString();
        mText = parcel.readString();
    }

    public static final Creator<NoteInfo> CREATOR = new Creator<NoteInfo>() {
        @Override
        public NoteInfo createFromParcel(Parcel in) {
            return new NoteInfo(in);
        }

        @Override
        public NoteInfo[] newArray(int size) {
            return new NoteInfo[size];
        }
    };

    public CourseInfo getCourse() {
        return mCourse;
    }

    public void setCourse(CourseInfo course) {
        mCourse = course;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    private String getCompareKey() {
        return mCourse.getCourseId() + "|" + mTitle + "|" + mText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NoteInfo that = (NoteInfo) o;

        return getCompareKey().equals(that.getCompareKey());
    }

    @Override
    public int hashCode() {
        return getCompareKey().hashCode();
    }

    @Override
    public String toString() {
        return getCompareKey();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        //other type of references requires a parcelable method
        //it requires two methods. the name of what is being passed and a flag to indicate any special handling
        parcel.writeParcelable(mCourse,0);

        //String can be parcel directly
        parcel.writeString(mTitle);
        parcel.writeString(mText);



        //details returned from this class will be use to make new instances of the NoteInfo class
        final Parcelable.Creator<NoteInfo> CREATOR =
                new Parcelable.Creator<NoteInfo>() {
                    @Override
                    //create a new instance and set values inside
                    //parcel values must be accessed in the same way they were written (wireToParcel) because no identifiers
                    public NoteInfo createFromParcel(Parcel parcel) {
                        return new NoteInfo(parcel);
                    }

                    //to create an array of a type with the appropriate size
                    @Override
                    public NoteInfo[] newArray(int size) {
                        return new NoteInfo[size];
                    }
                };

    }
}
