package com.jingkastudio.android.hippocampus.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for the Hippocampus app.
 */

public class DBStructure {
    
    // To prevent someone from accidentally instantiating the contract class
    private DBStructure() {}

    /** The "Content authority" is a name for the entire content provider */
    public static final String CONTENT_AUTHORITY = "com.jingkastudio.android.hippocampus";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    

    /**
     * Inner class that defines constant values for the daily_entry table.
     */
    public static final class DailyEntry implements BaseColumns {

        /** Possible path (appended to base content URI for possible URI's) */
        public static final String PATH_ENTRY = "entry";

        /** The content URI to access the pet data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ENTRY);

        /** The MIME type of the {@link #CONTENT_URI} for a list of daily entries. */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ENTRY;

        /** The MIME type of the {@link #CONTENT_URI} for a single entry. */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ENTRY;
        
        /** Name of database table for pets */
        public static final String TABLE_NAME = "daily_entry";

        /**
         * Unique ID number
         * Type: INTEGER
         */
        public static final String _ID = BaseColumns._ID;

        /**
         * Entry Title
         * Type: TEXT
         */
        public static final String COLUMN_TITLE = "entry_subject";

        /**
         * Entry Content
         * Type: TEXT
         */
        public static final String COLUMN_BODY = "entry_content";

        /**
         * Entry Tag
         * Type: TEXT
         */
        public static final String COLUMN_TAG = "entry_tag";

        /**
         * Entry Pin
         * Type: INTEGER
         */
        public static final String COLUMN_PIN = "entry_pin";

        /**
         * Entry Tag
         * Type: TEXT
         */
        public static final String COLUMN_REF_DAY__ID = "date";

    }

    /**
     * Inner class that defines constant values for the date table.
     */
    public static final class DateEntry implements BaseColumns {

        /** Possible path (appended to base content URI for possible URI's) */
        public static final String PATH_ENTRY = "date";

        /** The content URI to access the pet data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ENTRY);

        /** The MIME type of the {@link #CONTENT_URI} for a list of daily entries. */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ENTRY;

        /** The MIME type of the {@link #CONTENT_URI} for a single entry. */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ENTRY;


        /** Name of database table for pets */
        public static final String TABLE_NAME = "date_entry";

        /**
         * Unique ID number
         * Type: INTEGER
         */
        public static final String _ID = BaseColumns._ID;

        /**
         * Entry Date
         * Type: INTEGER
         */
        public static final String COLUMN_DATE = "daily_date";

        /**
         * Entry Mood
         * Type: INTEGER
         */
        public static final String COLUMN_MOOD = "daily_mood";

        /**
         * Entry Weather
         * Type: INTEGER
         */
        public static final String COLUMN_WEATHER = "daily_weather";

        /**
         * Entry Location
         * Type: TEXT
         */
        public static final String COLUMN_LOCATION = "daily_location";


    }
    
}
