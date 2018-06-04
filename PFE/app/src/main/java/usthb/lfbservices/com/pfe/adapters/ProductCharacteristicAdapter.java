package usthb.lfbservices.com.pfe.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import usthb.lfbservices.com.pfe.R;
import usthb.lfbservices.com.pfe.models.KeyValue;
import usthb.lfbservices.com.pfe.roomDatabase.AppRoomDatabase;

/**
 * Created by ryadh on 15/05/2018.
 */


public class ProductCharacteristicAdapter extends ArrayAdapter<KeyValue> {

    private static String TAG = "ProductCaracAdapter";

    private Context context;
    private int layoutResourceId;




    public ProductCharacteristicAdapter(Context context, int layoutResourceId, ArrayList<KeyValue> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
    }


    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    layoutResourceId, parent, false);
        }

        if (listItemView != null) {
            final KeyValue productCharacteristic = getItem(position);
            if (productCharacteristic != null) {
                String productCharacteristicName = AppRoomDatabase.getInstance(context).typeCharacteristicDao().getTypeCharacteristicName(productCharacteristic.getTypeCharacteristicId());
                if (productCharacteristicName == null) productCharacteristicName = context.getResources().getString(R.string.not_available);
                final TextView characteristicName = listItemView.findViewById(R.id.product_caracteristic_name);
                if (characteristicName != null) characteristicName.setText(productCharacteristicName);
                final TextView characteristicValue = listItemView.findViewById(R.id.product_caracteristic_value);
                if (characteristicValue != null) characteristicValue.setText(productCharacteristic.getProductCharacteristicValue());
            }
        }

        return listItemView;
    }
}
