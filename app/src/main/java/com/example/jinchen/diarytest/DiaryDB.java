package com.example.jinchen.diarytest;

/**
 * Created by JinChen on 2016/4/9.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.View;

public class DiaryDB  extends SQLiteOpenHelper{
    public static final String TABLE_NAME = "notes";
    //public static final String TITLE="title";
    public static final String DATE="date";
    public static final String WEATHER="weather";

    public static final String CONTENT = "content";
    public static final String PATH = "path";
    public static final String VIDEO = "video";
    public static final String ID = "_id";
    public static final String TIME = "time";
    public DiaryDB(Context context) {
        super(context, "diary", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DATE+ " TEXT NOT NULL,"
                + WEATHER+ " TEXT NOT NULL,"
                + CONTENT + " TEXT NOT NULL)");
//                + PATH + " TEXT NOT NULL,"
//                + VIDEO + " TEXT NOT NULL,"

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
