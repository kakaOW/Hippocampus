package com.jingkastudio.android.hippocampus.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jingkastudio.android.hippocampus.data.EntryContract.DailyEntry;

/**
 * Database helper for the app. Manages database creation and version management.
 */

public class EntryDbHelper extends SQLiteOpenHelper {

    /** Name of the database file */
    private static final String DATABASE_NAME = "hippo.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link EntryDbHelper}.
     *
     * @param context of the app
     */
    public EntryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_PETS_TABLE =  "CREATE TABLE " + DailyEntry.TABLE_NAME + " ("
                + DailyEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DailyEntry.COLUMN_TITLE + " TEXT NOT NULL, "
                + DailyEntry.COLUMN_BODY + " TEXT, "
                + DailyEntry.COLUMN_TAG + " TEXT);";

        // Execute the SQL statement
        sqLiteDatabase.execSQL(SQL_CREATE_PETS_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}
