package com.example.user.argus.storage;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Created by zx on 2015/9/6.
 */
public class DatabaseHandler {
    static final String DATE   = "date";
    static final String STR   = "string";
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public DatabaseHandler(Context context) {
        dbHelper = new DatabaseHelper(context);
    }
    public void openDB() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }
    public void close() {
        dbHelper.close();
    }
    public void insertMessage(String date, String string) {
        openDB();
        ContentValues cv = new ContentValues();
        cv.put(DATE, date);
        cv.put(STR, string);
        database.insert(dbHelper.getTableName(), DATE, cv);
        close();
    }
    public void deleteContact(long id) {
        openDB();
        Cursor cursor = database.query(dbHelper.getTableName(), null, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            for (int i = 1; i < cursor.getCount() - id; i ++) {
                cursor.moveToNext();
            }
            int index = cursor.getColumnIndex(dbHelper.getRowIdName());
            long ID = cursor.getLong(index);
            database.delete(dbHelper.getTableName(), dbHelper.getRowIdName() + "=" + ID, null);
        }
        close();
    }

    public void deleteselect(Vector vector) {
        openDB();
        String [] IDs = new String[vector.size()];
        for (int i = 0; i < vector.size(); i ++) {
            Cursor cursor = database.query(dbHelper.getTableName(), null, null, null, null, null, null);
            Object obj = vector.get(i);
            int index = Integer.parseInt(String.valueOf(obj));
            if (cursor != null) {
                cursor.moveToFirst();
                for (int j = 1; j < cursor.getCount() - index; j ++) {
                    cursor.moveToNext();
                }
                int row = cursor.getColumnIndex(dbHelper.getRowIdName());
                String ID = "" + cursor.getLong(row);
                IDs[i] = ID;
            }
            cursor.close();
        }
        for (int i = 0; i < IDs.length; i ++) {
            database.delete(dbHelper.getTableName(), dbHelper.getRowIdName() + "=" + IDs[i], null);
        }
        close();
    }

    public List<Map<String, Object>> selectAll() {
        openDB();
        List <Map <String, Object>> sessions = new ArrayList<Map <String, Object>>();
        Cursor cursor = database.query(dbHelper.getTableName(), null, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while(! cursor.isAfterLast()) {
                String dbdate = cursor.getString(cursor.getColumnIndex(DATE));
                String dbstring = cursor.getString(cursor.getColumnIndex(STR));
                Map <String, Object> tmp = new HashMap<String, Object>();
                tmp.put("date", dbdate);
                tmp.put("string", dbstring);
                sessions.add(0, tmp);
                cursor.moveToNext();
            }
        }
        cursor.close();
        close();
        return sessions;
    }
    public List<Map<String, Object>> selectAllforDelete() {
        openDB();
        List <Map <String, Object>> sessions = new ArrayList<Map <String, Object>>();
        Cursor cursor = database.query(dbHelper.getTableName(), null, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while(! cursor.isAfterLast()) {
                String dbdate = cursor.getString(cursor.getColumnIndex(DATE));
                String dbstring = cursor.getString(cursor.getColumnIndex(STR));
                Map <String, Object> tmp = new HashMap<String, Object>();
                tmp.put("date", dbdate);
                tmp.put("string", dbstring);
                tmp.put("checkbox", false);
                sessions.add(0, tmp);
                cursor.moveToNext();
            }
        }
        cursor.close();
        close();
        return sessions;
    }
}
