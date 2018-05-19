package usthb.lfbservices.com.pfe.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import usthb.lfbservices.com.pfe.R;
import usthb.lfbservices.com.pfe.RoomDatabase.AppRoomDatabase;
import usthb.lfbservices.com.pfe.models.KeyValue;

/**
 * Created by ryadh on 15/05/2018.
 */


public class ProductCaracteristicAdapter extends ArrayAdapter<KeyValue> {

    private static String TAG = "ProductCaracAdapter";

    private Context context;
    private int layoutResourceId;

    public ProductCaracteristicAdapter(Context context, int layoutResourceId, ArrayList<KeyValue> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
    }


    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Log.e("ADAPTER", "position + " + position);
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    layoutResourceId, parent, false);
        }

        if (listItemView != null) {
            final KeyValue productCaracteristic = getItem(position);
            if (productCaracteristic != null) {
                String productCaracteristicName = AppRoomDatabase.getInstance(context).typeCaracteristicDao().getTypeCaracteristicName(productCaracteristic.getTypeCaracteristicId());
                if (productCaracteristicName == null) productCaracteristicName = context.getResources().getString(R.string.not_available);
                final TextView caracteristicName = listItemView.findViewById(R.id.product_caracteristic_name);
                if (caracteristicName != null) caracteristicName.setText(productCaracteristicName);
                final TextView caracteristicValue = listItemView.findViewById(R.id.product_caracteristic_value);
                if (caracteristicValue != null) caracteristicValue.setText(productCaracteristic.getProductCaracteristicValue());

                Log.e("ADAPTER", "position + " + productCaracteristicName);
            }
        }

        return listItemView;
    }
}
