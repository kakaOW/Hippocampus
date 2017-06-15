package com.jingkastudio.android.hippocampus.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for the Hippocampus app.
 */

public class EntryContract {
    
    // To prevent someone from accidentally instantiating the contract class
    private EntryContract() {}

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.jingkastudio.android.hippocampus";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.pets/pets/ is a valid path for
     * looking at pet data. content://com.example.android.pets/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_ENTRY = "entries";

    /**
     * Inner class that defines constant values for the database table.
     */
    public static final class DailyEntry implements BaseColumns {

        /** The content URI to access the pet data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ENTRY);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of daily entries.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ENTRY;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single entry.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ENTRY;

        /** Name of database table for pets */
        public final static String TABLE_NAME = "entries";

        /**
         * Unique ID number for the pet (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Entry Title
         * Type: TEXT
         */
        public final static String COLUMN_TITLE = "title";

        /**
         * Entry Body
         * Type: TEXT
         */
        public final static String COLUMN_BODY = "body";

        /**
         * Entry Tag
         * Type: TEXT
         */
        public final static String COLUMN_TAG = "tag";

    }
    
}
