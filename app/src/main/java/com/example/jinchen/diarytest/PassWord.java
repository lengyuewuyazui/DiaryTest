package com.example.jinchen.diarytest;

/**
 * Created by JinChen on 2016/5/3.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class PassWord extends SQLiteOpenHelper{
    public static final String TABLE_NAME = "login";
    public static final String NAME="name";
    public static final String PASSWORD="password";
    public PassWord(Context context) {
        super(context, "password", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
                + NAME+ " TEXT NOT NULL,"
                + PASSWORD + " TEXT NOT NULL)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
