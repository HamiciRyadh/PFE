package usthb.lfbservices.com.pfe.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import usthb.lfbservices.com.pfe.R;

/**
 * Created by ryadh on 22/05/18.
 */

public class SuggestinAdapter extends CursorAdapter {

    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private SearchView searchView;

    public SuggestinAdapter(Context context, Cursor cursor, SearchView sv) {
        super(context, cursor, false);
        mContext = context;
        searchView = sv;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = mLayoutInflater.inflate(R.layout.list_item_suggestion, parent, false);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.e("SearchAdapter","HERE");
        TextView suggestion = view.findViewById(R.id.proposition);
        if (suggestion != null) {
            Log.e("SearchAdapter",cursor.getString(0));
            suggestion.setText(cursor.getString(1));
        }
    }
}