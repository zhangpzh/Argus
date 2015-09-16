package com.floatdragon.argus.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by zx on 2015/9/6.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "db";
    private static final String TABLE_NAME    = "messages";
    private static final String COLUMN_ID     = "_id";
    private static final String COLUMN_DATE	  = "date";
    private static final String COLUMN_STRING   = "string";
    private static final String CREATE_TABLE  = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_DATE + " TEXT," +
            COLUMN_STRING + " TEXT" + ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("Drop table if exists " + TABLE_NAME);
        onCreate(db);
    }
    public String getTableName() {
        return TABLE_NAME;
    }
    public String getRowIdName() {
        return COLUMN_ID;
    }
}