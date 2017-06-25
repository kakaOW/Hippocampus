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

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.jingkastudio.android.hippocampus.data.DBStructure;
import com.jingkastudio.android.hippocampus.data.DBStructure.DailyEntry;
import com.yuncun.swipeableweekview.WeekViewAdapter;
import com.yuncun.swipeableweekview.WeekViewSwipeable;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays list of hippocampus that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /** Identifier for the entry data loader */
    private static final int[] ENTRY_LOADER_ARRAY = {0,1,2,3,4,5,6,7,8,9,10,11,12,13};

    // Date data
    private List<String> recordList;
    private String mDate;
    private int currentIndex;


    /** Adapter for the ListView */
    EntryCursorAdapter mEntryCursorAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        mDate = new DateTime().toString(DBStructure.DATE_FORMAT);
        this.setTitle(mDate);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                intent.putExtra("mDate", getTitle().toString());
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the entry data
        ListView entryListView = (ListView) findViewById(R.id.list);

        // Setup an Adapter to create a list of item for each row of entry data in the Cursor
        mEntryCursorAdapter = new EntryCursorAdapter(this, null);
        entryListView.setAdapter(mEntryCursorAdapter);

        // Setup the item click listener
        entryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

                // Form the content URI that represents the specific pet that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link EntryEntry#CONTENT_URI}.
                Uri currentEntryUri = ContentUris.withAppendedId(DailyEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentEntryUri);
                intent.putExtra("mDate", getTitle().toString());
                intent.putExtra("currentIndex", getCurrentIndex());
                
                // Launch the {@link EditorActivity} to display the data for the current pet.
                startActivity(intent);
            }
        });

        //Text TODO
        final Context context = this;

        // Get Date for WeekViewAdapter
        getRecordListforWVS();

        WeekViewSwipeable wvs = (WeekViewSwipeable) findViewById(R.id.calendar_component);

        WeekViewAdapter adapter = new WeekViewAdapter(recordList) {
            @Override
            public int getStrokeColor(final int index){
                if (index == recordList.size()-1 ) { return ContextCompat.getColor(context, R.color.teal);}
                else { return ContextCompat.getColor(context, R.color.grey_500);}

            }

            @Override
            public int getFillColor(final int index){
                if (index == recordList.size()-1 ) { return ContextCompat.getColor(context, R.color.teal);}
                else { return ContextCompat.getColor(context, R.color.grey_500);}

            }

            @Override
            public TextView getTextView(TextView tv, int index){
                tv.setText(recordList.get(index));
                return tv;
            }

            @Override
            public View getDayLayout(View dv, final int index){
                dv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        GetDateList();
                        mDate = recordList.get(index);
                        setTitle(mDate);
                        setCurrentIndex(index);



                        // Kick off the loader
                        getLoaderManager().initLoader(ENTRY_LOADER_ARRAY[index], null, CatalogActivity.this);

                    }
                });
                return dv;
            }

        };

        wvs.setAdapter(adapter);

        // Workaround on a bug with the library that on Saturday the view will move to next week
        wvs.leftNav.performClick();
        // Set left and right button to null to only have 1 week view
        wvs.setNavEnabled(false);


        int dayLM = new DateTime().getDayOfWeek() == 7 ? 0 : new DateTime().getDayOfWeek();
        getLoaderManager().initLoader(ENTRY_LOADER_ARRAY[dayLM], null, CatalogActivity.this);

    }

    private void getDatesforWVS() {
        recordList = new ArrayList<>();
        int dayOfWeek = new DateTime().getDayOfWeek();
        switch(dayOfWeek) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                for (int i = dayOfWeek; i >= 0; i--) {
                    recordList.add(new DateTime().minusDays(i).toString(DBStructure.DATE_FORMAT));
                }
                break;
            case 7:
                recordList.add(new DateTime().toString(DBStructure.DATE_FORMAT));
                break;
            default:
                break;
        }
    }

    private List<String> getRecordListforWVS() {
        getDatesforWVS();
        return recordList;
    }

    private void getCompleteDateofWeek() {
        for(int i = 1; i <= 7 ; i++) {
                recordList.add(new DateTime().plusDays(i).toString(DBStructure.DATE_FORMAT));
        }


    }

    private List<String> GetDateList() {
        getCompleteDateofWeek();
        return recordList;
    }

    private void setCurrentIndex(int index) {
        currentIndex = index;
    }

    private int getCurrentIndex() {
        return currentIndex;
    }



    // Insert dummy data for debugging purposes only
    private void insertDummy() {
        // Create a ContentValues object where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DailyEntry.COLUMN_TITLE, "What problem did I encounter today? How did I solve the problem?");
        values.put(DailyEntry.COLUMN_CONTENT, "I want to have a horizontal view that scrolls and has for example the names of the days of the week. The user scrolls horizontally. The day selected is the one in the middle ( like a spinner selection ). You can see the below image.");
        values.put(DailyEntry.COLUMN_DATE_REF_DATE, mDate);

        // Insert a new row into the provider using the ContentResolver
        Uri newUri = getContentResolver().insert(DailyEntry.CONTENT_URI, values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertDummy();
                return true;

            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllEntries();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllEntries() {
        int rowsDeleted = getContentResolver().delete(DailyEntry.CONTENT_URI, null, null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                DailyEntry._ID,
                DailyEntry.COLUMN_TITLE,
                DailyEntry.COLUMN_CONTENT,
                DailyEntry.COLUMN_DATE_REF_DATE,
                DailyEntry.COLUMN_TAG };


        String  selection = "(" + DailyEntry.COLUMN_DATE_REF_DATE + "='"+ recordList.get(id)  +"')";






        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,
                DailyEntry.CONTENT_URI,
                projection,
                selection,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link EntryCursorAdapter} with this new cursor containing updated entry data
        mEntryCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mEntryCursorAdapter.swapCursor(null);
    }
}
