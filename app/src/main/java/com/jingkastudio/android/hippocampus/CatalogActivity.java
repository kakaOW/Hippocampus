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

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.jingkastudio.android.hippocampus.data.DBStructure.DailyEntry;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;

import noman.weekcalendar.WeekCalendar;
import noman.weekcalendar.listener.OnDateClickListener;

/**
 * Displays list of hippocampus that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {

    private WeekCalendar mWeekCalendar;
    private String mDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        displayWeekCalendar();

        mDate = new SimpleDateFormat("MMM dd yyyy").format(new Date());
        this.setTitle(mDate);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

    }


    // Initiate Week Calendar Library
    public void displayWeekCalendar() {
        mWeekCalendar = (WeekCalendar) findViewById(R.id.week_view);
        mWeekCalendar.setOnDateClickListener(new OnDateClickListener() {
            @Override
            public void onDateClick(DateTime dateTime) {
                DateTimeFormatter fmt = DateTimeFormat.forPattern("MMM dd yyyy");
                mDate = dateTime.toString(fmt);
                setTitle(mDate);
                //TODO load data or show template

            }
        });

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

    private void deleteAllPets() {
        int rowsDeleted = getContentResolver().delete(DailyEntry.CONTENT_URI, null, null);
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
                deleteAllPets();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



}
