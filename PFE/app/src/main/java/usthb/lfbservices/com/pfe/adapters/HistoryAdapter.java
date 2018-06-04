package usthb.lfbservices.com.pfe.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import usthb.lfbservices.com.pfe.R;
import usthb.lfbservices.com.pfe.activities.MainActivity;

/**
 * Created by ryadh on 09/03/18.
 */

public class HistoryAdapter extends ArrayAdapter<String> {

    private static String TAG = "HistoryAdapter";

    private Context context;
    private int layoutResourceId;

    public HistoryAdapter(Context context, int layoutResourceId, ArrayList<String> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable final View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    layoutResourceId, parent, false);
        }

       String history = getItem(position);

        final TextView historyElement = listItemView.findViewById(R.id.history_element);
        historyElement.setText(history);

        final ImageView imageView = listItemView.findViewById(R.id.history_to_search_view);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "ImageView OnClickListener");
                if (context instanceof MainActivity) {
                    ((MainActivity)context).setSearchViewQuery(getItem(position));
                }
            }
        });

        return listItemView;
    }
}