package com.sargent.mark.todolist.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by mark on 7/4/17.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "items.db";
    private static final String TAG = "dbhelper";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String queryString = "CREATE TABLE " + Contract.TABLE_TODO.TABLE_NAME + " (" +
                Contract.TABLE_TODO._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Contract.TABLE_TODO.COLUMN_NAME_DESCRIPTION + " TEXT NOT NULL, " +
                Contract.TABLE_TODO.COLUMN_NAME_CATEGORY_TYPE + " INTEGER NOT NULL, " +
                Contract.TABLE_TODO.COLUMN_NAME_IS_DONE + " INTEGER NOT NULL, " +
                Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE + " DATE " + "); ";

        //Table for Categories
        String categoryTableQry = "CREATE TABLE " + Category.TABLE_CATEGORY.TABLE_NAME + " (" +
                Category.TABLE_CATEGORY._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Category.TABLE_CATEGORY.COLUMN_CATEGORY_NAME + " TEXT NOT NULL" + "); ";


        Log.d(TAG, "Create table SQL: " + queryString);
        db.execSQL(queryString);
        Log.d(TAG, "Create table SQL: " + categoryTableQry);
        db.execSQL(categoryTableQry);//Creating categories Table


//Inserting 4 Category types
        ContentValues cv = new ContentValues();
        cv.put(Category.TABLE_CATEGORY.COLUMN_CATEGORY_NAME, "Shopping");
        db.insert(Category.TABLE_CATEGORY.TABLE_NAME, null, cv);

        ContentValues cv1 = new ContentValues();
        cv1.put(Category.TABLE_CATEGORY.COLUMN_CATEGORY_NAME, "Travel");
        db.insert(Category.TABLE_CATEGORY.TABLE_NAME, null, cv1);

        ContentValues cv2 = new ContentValues();
        cv2.put(Category.TABLE_CATEGORY.COLUMN_CATEGORY_NAME, "Meeting");
        db.insert(Category.TABLE_CATEGORY.TABLE_NAME, null, cv2);

        ContentValues cv3 = new ContentValues();
        cv3.put(Category.TABLE_CATEGORY.COLUMN_CATEGORY_NAME, "Office Work");
        db.insert(Category.TABLE_CATEGORY.TABLE_NAME, null, cv3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("drop table " + Contract.TABLE_TODO.TABLE_NAME + " if exists;");
    }
}
