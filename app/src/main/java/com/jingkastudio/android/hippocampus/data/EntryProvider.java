package com.jingkastudio.android.hippocampus.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.jingkastudio.android.hippocampus.data.EntryContract.DailyEntry;

/**
 * {@link ContentProvider} for Hippocampus app.
 */

public class EntryProvider extends ContentProvider{

    /** Tag for the log messages */
    public static final String LOG_TAG = EntryProvider.class.getSimpleName();

    /** URI matcher code for the content URI for the entries table */
    private static final int ENTRIES = 100;

    /** URI matcher code for the content URI for a single entry in the entries table */
    private static final int ENTRY_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // The content URI of the form "com.jingkastudio.android.hippocampus/entries" will map to the
        // integer code {@link #ENTRIES}. This URI is used to provide access to MULTIPLE rows
        // of the entries table.
        sUriMatcher.addURI(EntryContract.CONTENT_AUTHORITY, EntryContract.PATH_ENTRY, ENTRIES);

        // The content URI of the form "content://com.jingkastudio.android.hippocampus/entries/#" will map to the
        // integer code {@link #ENTRY_ID}. This URI is used to provide access to ONE single row
        // of the entries table.
        //
        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        // For example, "content://com.jingkastudio.android.hippocampus/entries/3" matches, but
        // "content://com.jingkastudio.android.hippocampus/entries" (without a number at the end) doesn't match.
        sUriMatcher.addURI(EntryContract.CONTENT_AUTHORITY, EntryContract.PATH_ENTRY + "/#", ENTRY_ID);
    }

    private EntryDbHelper mEntryDbHelper;

    @Override
    public boolean onCreate() {
        mEntryDbHelper = new EntryDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mEntryDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case ENTRIES:
                // For the ENTRIES code, query the entries table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the entries table.
                cursor = database.query(DailyEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case ENTRY_ID:
                // For the ENTRY_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.entries/entries/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = DailyEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the entries table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(DailyEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ENTRIES:
                return insertEntry(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a entry into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertEntry(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(DailyEntry.COLUMN_TITLE);
        if (name == null) {
            throw new IllegalArgumentException("Title can not be blank");
        }

        // Get writeable database
        SQLiteDatabase database = mEntryDbHelper.getWritableDatabase();

        // Insert the new entry with the given values
        long id = database.insert(DailyEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the pet content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ENTRIES:
                return updateEntry(uri, contentValues, selection, selectionArgs);
            case ENTRY_ID:
                // For the ENTRY_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = DailyEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateEntry(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update entries in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more entries).
     * Return the number of rows that were successfully updated.
     */
    private int updateEntry(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link DailyEntry#COLUMN_TITLE} key is present,
        // check that the name value is not null.
        if (values.containsKey(DailyEntry.COLUMN_TITLE)) {
            String name = values.getAsString(DailyEntry.COLUMN_TITLE);
            if (name == null) {
                throw new IllegalArgumentException("Title cannot be blank");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mEntryDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(DailyEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners
        if(rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }


        // Returns the number of database rows updated
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mEntryDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ENTRIES:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(DailyEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ENTRY_ID:
                // Delete a single row given by the ID in the URI
                selection = DailyEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(DailyEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners
        if (rowsDeleted !=0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Returns the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ENTRIES:
                return DailyEntry.CONTENT_LIST_TYPE;
            case ENTRY_ID:
                return DailyEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}