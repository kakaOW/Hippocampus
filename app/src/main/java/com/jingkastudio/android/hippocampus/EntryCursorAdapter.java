package com.jingkastudio.android.hippocampus;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.jingkastudio.android.hippocampus.data.EntryContract.DailyEntry;

/**
 * {@link EntryCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of entry data as its data source. This adapter knows
 * how to create list items for each row of entry data in the {@link Cursor}.
 */
public class EntryCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link EntryCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public EntryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the entry data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the title for the current entry can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView titleTextVIew = (TextView) view.findViewById(R.id.title);
        TextView bodyTextVIew = (TextView) view.findViewById(R.id.body);

        // Find the columns of entry attributes that we're interested in
        int titleColumnIndex = cursor.getColumnIndex(DailyEntry.COLUMN_TITLE);
        int bodyColumnIndex = cursor.getColumnIndex(DailyEntry.COLUMN_BODY);

        // Read the entry attributes from the Cursor for the current entry
        String entryTitle = cursor.getString(titleColumnIndex);
        String entryBody = cursor.getString(bodyColumnIndex);


        // Update the TextViews with the attributes for the current pet
        titleTextVIew.setText(entryTitle);
        bodyTextVIew.setText(entryBody);
    }
}
