package com.jingkastudio.android.hippocampus.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jingkastudio.android.hippocampus.data.DBStructure.DailyEntry;
import com.jingkastudio.android.hippocampus.data.DBStructure.DateEntry;


/**
 * Database helper for the app. Manages database creation and version management.
 */

public class DbHelper extends SQLiteOpenHelper {

    /** Name of the database file */
    private static final String DATABASE_NAME = "hippo.db";

    /** Database version */
    private static final int DATABASE_VERSION = 1;

    /** Create Entry Table */
    String SQL_CREATE_ENTRY_TABLE =  "CREATE TABLE " + DailyEntry.TABLE_NAME + " ("
            + DailyEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + DailyEntry.COLUMN_TITLE + " TEXT NOT NULL, "
            + DailyEntry.COLUMN_BODY + " TEXT, "
            + DailyEntry.COLUMN_TAG + " TEXT, "
            + DailyEntry.COLUMN_PIN + " INTEGER DEFAULT 0, "
            + " FOREIGN KEY (" + DailyEntry.COLUMN_REF_DAY__ID
            + ") REFERENCES" + DateEntry.TABLE_NAME + "(" + DateEntry.COLUMN_DATE + "));";

    /** Create Date Table */
    String SQL_CREATE_DAY_TABLE =  "CREATE TABLE " + DateEntry.TABLE_NAME + " ("
            + DateEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + DateEntry.COLUMN_DATE + " INTEGER , "
            + DateEntry.COLUMN_MOOD + " INTEGER , "
            + DateEntry.COLUMN_WEATHER + " INTEGER, "
            + DateEntry.COLUMN_LOCATION + " TEXT);";
    
    /** Constructs a new instance of {@link DbHelper}. */
    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /** This is called when the database is created for the first time.*/
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Execute the SQL statement
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRY_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_DAY_TABLE);

    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}
