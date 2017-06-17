package com.jingkastudio.android.hippocampus;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.jingkastudio.android.hippocampus.data.DBStructure.DailyEntry;

/**
 * Created by oscarwei on 6/16/17.
 */

public class EntryFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    /** Adapter for the ListView */
    private SimpleCursorAdapter mAdapter;


    /** Identifier for the entry data loader */
    private static final int ENTRY_LOADER = 0;


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment, container, false);
        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final String[] select = new String[] {DailyEntry.COLUMN_TITLE, DailyEntry.COLUMN_CONTENT, DailyEntry.COLUMN_DATE_REF_DATE};
        final int[] layout = new int[] {R.id.title, R.id.content, R.id.date};

        mAdapter = new SimpleCursorAdapter(getActivity(),
                R.layout.list_item, null,
                select, layout, 0);
        setListAdapter(mAdapter);

        getLoaderManager().initLoader(ENTRY_LOADER, null, this);


    }


    @Override
    public void onListItemClick(ListView parent, View v, int position, long id) {
        // Create new intent to go to {@link EditorActivity}
        Intent intent = new Intent(getActivity(), EditorActivity.class);

        // Form the content URI that represents the specific pet that was clicked on,
        // by appending the "id" (passed as input to this method) onto the
        // {@link EntryEntry#CONTENT_URI}.
        Uri currentEntryUri = ContentUris.withAppendedId(DailyEntry.CONTENT_URI, id);

        // Set the URI on the data field of the intent
        intent.setData(currentEntryUri);

        // Launch the {@link EditorActivity} to display the data for the current pet.
        startActivity(intent);



    }

    String mDate = getActivity().getTitle().toString();
    String selection = "(" + DailyEntry.COLUMN_DATE_REF_DATE + "= '" + mDate + "')";

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                DailyEntry._ID,
                DailyEntry.COLUMN_TITLE,
                DailyEntry.COLUMN_CONTENT,
                DailyEntry.COLUMN_DATE_REF_DATE,
                DailyEntry.COLUMN_TAG };




        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(getActivity(),
                DailyEntry.CONTENT_URI,
                projection,
                selection,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
