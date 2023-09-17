package com.ecg.complimenter.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Sam on 10/27/2014.
 */
public class ComplimentDataProvider {
    private SQLiteDatabase mDatabase;
    private final ComplimentDataHelper mHelper;
    private static ComplimentDataProvider PROVIDER;

    public ComplimentDataProvider(Context context){
        mHelper = new ComplimentDataHelper(context);
    }

    public static synchronized ComplimentDataProvider getInstance(Context context){
        if (PROVIDER == null) { return PROVIDER = new ComplimentDataProvider(context); }
        else{ return PROVIDER; }
    }

    private ComplimentData cursorToComplimentData(Cursor cursor){
        return new ComplimentData(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getBlob(3));
    }

    public void open() throws SQLException{
        this.mDatabase = mHelper.getWritableDatabase();
        this.mHelper.onCreate(this.mDatabase);
    }

    public void close() {
        mHelper.close();
    }

    public ComplimentData getItem(long id) {
        try (Cursor data = this.mDatabase.query(ComplimentDataHelper.TABLE_COMPLIMENTS,
                ComplimentDataHelper.ALL_COLUMNS, String.format("%s = %i",
                        ComplimentDataHelper.COLUMN_ID, id), null, null, null, null)) {
            return cursorToComplimentData(data);
        }
    }

    public ArrayList<ComplimentData> getRange(int start, int size) {
        ArrayList<ComplimentData> compliments = new ArrayList<ComplimentData>();
        try {
            if (mDatabase == null || !this.mDatabase.isOpen()) {
                this.open();
            }
            try (Cursor data = this.mDatabase.query(ComplimentDataHelper.TABLE_COMPLIMENTS,
                    ComplimentDataHelper.ALL_COLUMNS, null, null, null, null, null, Integer.toString(size))) {
                if (data.getCount() > 0) {
                    for (data.moveToPosition(start); !data.isAfterLast(); data.moveToNext()) {
                        compliments.add(cursorToComplimentData(data));
                    }
                }
            }
        }
        catch(SQLException e){
            Log.e("DATABASE:", "Unable to read values in getRange(" + start + ", " + size + ")" + e);
        }
        return compliments;
    }

    public int getCount() {
        try {
            if (mDatabase == null || !this.mDatabase.isOpen()) {
                this.open();
            }
             try (Cursor queryCount = this.mDatabase.query(ComplimentDataHelper.TABLE_COMPLIMENTS,
                    new String[]{"count(*)"}, null, null, null, null, null)) {
                 queryCount.moveToFirst();
                 return queryCount.getInt(0);
             }
        }
        catch(SQLException e){
            Log.e("DATABASE:", "Unable to query count " + e);
        }
        return 0;
    }
}