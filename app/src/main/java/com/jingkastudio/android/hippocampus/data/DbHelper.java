package com.jingkastudio.android.hippocampus.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jingkastudio.android.hippocampus.data.DBStructure.DailyEntry;
import com.jingkastudio.android.hippocampus.data.DBStructure.DateEntry;
import com.jingkastudio.android.hippocampus.data.DBStructure.SubjectTemplate;


/**
 * Database helper for the app. Manages database creation and version management.
 */

public class DbHelper extends SQLiteOpenHelper {

    /** Name of the database file */
    private static final String DATABASE_NAME = "hippotest.db";

    /** Database version */
    private static final int DATABASE_VERSION = 1;

    /** Create Entry Table */
    private static final String SQL_CREATE_ENTRY_TABLE =  "CREATE TABLE " + DailyEntry.TABLE_NAME + " ("
            + DailyEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + DailyEntry.COLUMN_TITLE + " TEXT NOT NULL, "
            + DailyEntry.COLUMN_CONTENT + " TEXT, "
            + DailyEntry.COLUMN_DATE_REF_DATE + " INTEGER NOT NULL, "
            + DailyEntry.COLUMN_TAG + " TEXT, "
            + DailyEntry.COLUMN_PIN + " INTEGER DEFAULT 0);";

    /** Create Date Table */
    private static final String SQL_CREATE_DAY_TABLE =  "CREATE TABLE " + DateEntry.TABLE_NAME + " ("
            + DateEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + DateEntry.COLUMN_DATE + " INTEGER NOT NULL, "
            + DateEntry.COLUMN_MOOD + " INTEGER, "
            + DateEntry.COLUMN_WEATHER + " INTEGER, "
            + DateEntry.COLUMN_LOCATION + " TEXT);";

    /** Create Date Table */
    private static final String SQL_CREATE_TEMPLATE_TABLE =  "CREATE TABLE " + SubjectTemplate.TABLE_NAME + " ("
            + SubjectTemplate._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SubjectTemplate.COLUMN_SUBJECT + " TEXT NOT NULL, "
            + SubjectTemplate.COLUMN_GROUP_ID + " INTEGER);";

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
        sqLiteDatabase.execSQL(SQL_CREATE_TEMPLATE_TABLE);

    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if(newVersion > oldVersion) {
            sqLiteDatabase.beginTransaction();
        }
    }
}
