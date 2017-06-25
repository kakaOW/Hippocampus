/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jingkastudio.android.hippocampus;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jingkastudio.android.hippocampus.data.DBStructure.DailyEntry;

/**
 * Allows user to create a new entry or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    /** Content URI for the existing entry (null if it's a new entry) */
    private Uri mCurrentEntryUri;

    /** EditText field to enter the entry's name */
    private EditText mTitleEditText;

    /** EditText field to enter the entry's breed */
    private EditText mBodyEditText;

    /** Content Date and Index from @CatalogActivity */
    private String editorDate;
    private int editorIndex;


    /** Boolean flag that keeps track of whether the entry has been edited */
    private boolean mEntryHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mEntryHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mEntryHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Examine the intent to determine to create a new entry or edit an existing one
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null) {
            editorDate = bundle.getString("mDate");
            editorIndex = bundle.getInt("currentIndex");
        }
        mCurrentEntryUri = intent.getData();

        // If the intent DOES NOT contain an entry content URI, create a new entry
        if (mCurrentEntryUri == null) {

        } else {
            // Initialize a loader to read the entry data from the database
            getLoaderManager().initLoader(editorIndex, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mTitleEditText = (EditText) findViewById(R.id.edit_entry_title);
        mBodyEditText = (EditText) findViewById(R.id.edit_entry_body);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them.
        mTitleEditText.setOnTouchListener(mTouchListener);
        mBodyEditText.setOnTouchListener(mTouchListener);
    }

    /**
     *  Get user input from editor and save new entry into database
     */
    private void saveEntry() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String titleString = mTitleEditText.getText().toString().trim();
        String bodyString = mBodyEditText.getText().toString().trim();
        String dateString = editorDate;

        if (mCurrentEntryUri == null &&
                TextUtils.isEmpty(titleString) && TextUtils.isEmpty(bodyString)) {
            // No change, return early
            return;
        }


        // Create a ContentValues object where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DailyEntry.COLUMN_TITLE, titleString);
        values.put(DailyEntry.COLUMN_CONTENT, bodyString);


        // Determine if this is a new or existing entry
        if(mCurrentEntryUri == null) {
            // This is a new entry
            values.put(DailyEntry.COLUMN_DATE_REF_DATE, dateString);
            Uri newUri = getContentResolver().insert(DailyEntry.CONTENT_URI, values);

            if(newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_entry_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_entry_successful), Toast.LENGTH_SHORT).show();
            }
        } else {
            // This is an existing entry
            int rowsAffected = getContentResolver().update(mCurrentEntryUri, values, null, null);

            if(rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_entry_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_entry_successful), Toast.LENGTH_SHORT).show();
            }
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save entry to database
                saveEntry();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the entry hasn't changed, return back
                if( !mEntryHasChanged) {
                    // Navigate back to parent activity (CatalogActivity)
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the entry hasn't changed, continue with handling back button press
        if (!mEntryHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                DailyEntry._ID,
                DailyEntry.COLUMN_TITLE,
                DailyEntry.COLUMN_CONTENT,
                DailyEntry.COLUMN_DATE_REF_DATE,
                DailyEntry.COLUMN_TAG };

        // Execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,
                mCurrentEntryUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if(cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        if(cursor.moveToFirst()) {
            int titleColumnIndex = cursor.getColumnIndex(DailyEntry.COLUMN_TITLE);
            int bodyColumnIndex = cursor.getColumnIndex(DailyEntry.COLUMN_CONTENT);
            int dateColumnIndex = cursor.getColumnIndex(DailyEntry.COLUMN_DATE_REF_DATE);
            int tagColumnIndex = cursor.getColumnIndex(DailyEntry.COLUMN_TAG);

            // Extract out the value from the Cursor for the given column index
            String title = cursor.getString(titleColumnIndex);
            String body = cursor.getString(bodyColumnIndex);
            String date = cursor.getString(dateColumnIndex); // TODO to be used later
            String tag = cursor.getString(tagColumnIndex); // TODO to be used later

            // Update the views on the screen with the values from the database
            mTitleEditText.setText(title);
            mBodyEditText.setText(body);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields
        mTitleEditText.setText("");
        mBodyEditText.setText("");
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the entry.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this entry.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the entry.
                deleteEntry();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the entry.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the entry in the database.
     */
    private void deleteEntry() {
        // Only perform the delete if this is an existing entry.
        if (mCurrentEntryUri != null) {
            // Call the ContentResolver to delete the entry at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentEntryUri
            // content URI already identifies the entry that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentEntryUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_entry_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_entry_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }
}