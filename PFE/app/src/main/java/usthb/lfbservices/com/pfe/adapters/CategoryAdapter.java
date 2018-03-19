package usthb.lfbservices.com.pfe.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import usthb.lfbservices.com.pfe.R;
import usthb.lfbservices.com.pfe.models.Category;

/**
 * Created by root on 08/03/18.
 */

public class CategoryAdapter extends ArrayAdapter<Category> {

    private  Context context;
    private int layoutResourceId;

    public CategoryAdapter(Context context, int layoutResourceId,
                                 ArrayList<Category> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RecordHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new RecordHolder();
            holder.txtTitle = row.findViewById(R.id.category_name);
            holder.imageItem = row.findViewById(R.id.category_image);
            row.setTag(holder);

        } else {
            holder = (RecordHolder) row.getTag();
        }

        Category category = getItem(position);
        holder.txtTitle.setText(category.getTitle());
        holder.imageItem.setImageBitmap(category.getImage());

        return row;
    }

    static class RecordHolder {
        TextView txtTitle;
        ImageView imageItem;
    }
}