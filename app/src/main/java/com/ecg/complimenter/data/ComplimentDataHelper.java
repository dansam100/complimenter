package com.ecg.complimenter.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Sam on 10/28/2014.
 */
public class ComplimentDataHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "complimenter.db";

    public static final String TABLE_COMPLIMENTS = "compliment_data";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TEXT = "compliment";
    public static final String COLUMN_IMAGE = "image_data";
    public static final String COLUMN_IMAGE_NAME = "image_name";
    public static final String[] ALL_COLUMNS;

    private Context mContext;

    static{
        ALL_COLUMNS = new String[]{COLUMN_ID, COLUMN_TEXT, COLUMN_IMAGE_NAME, COLUMN_IMAGE};
    }

    public ComplimentDataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        if(databaseExists()) {
            try {
                //Open your local db within apk as the input stream
                InputStream sourceDBStream = mContext.getAssets().open(DATABASE_NAME);
                //Open the empty db as the output stream
                OutputStream mainDBStream = new FileOutputStream(mContext.getDatabasePath(DATABASE_NAME).getPath());
                //transfer bytes from the inputFile to the outputFile
                byte[] buffer = new byte[1024];
                int length;
                while ((length = sourceDBStream.read(buffer)) > 0) {
                    mainDBStream.write(buffer, 0, length);
                }
                //Close the streams
                mainDBStream.flush();
                mainDBStream.close();
                sourceDBStream.close();
            } catch (IOException e) {
                Log.e("DATABASE:", "Error thrown while opening database " + e);
            }
        }
    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean databaseExists(){

        SQLiteDatabase database = null;
        try{
            database = SQLiteDatabase.openDatabase(mContext.getDatabasePath(DATABASE_NAME).getPath(),
                    null, SQLiteDatabase.OPEN_READONLY);
        }
        catch(SQLiteException e){
            Log.w("DATABASE:", "Database does not exist yet.");
        }
        if(database != null){
            database.close();
        }
        return database != null ? true : false;
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w("DATABASE", String.format("Upgrading database from version %i to %i, which will destroy all old data", oldVersion, newVersion));
        if(databaseExists()) {
            onCreate(database);
        }
    }
}
