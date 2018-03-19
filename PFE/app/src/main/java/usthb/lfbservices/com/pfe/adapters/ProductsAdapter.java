package usthb.lfbservices.com.pfe.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import usthb.lfbservices.com.pfe.models.Product;
import usthb.lfbservices.com.pfe.R;

/**
 * Created by root on 05/03/18.
 */

public class ProductsAdapter extends ArrayAdapter<Product> {

    private Context context;
    int layoutResourceId;

    public ProductsAdapter(Activity context, int layoutResourceId, ArrayList<Product> data){
        super(context,0, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    layoutResourceId, parent, false);
        }

        Product product = getItem(position);

        TextView tradeMarkTextView = listItemView.findViewById(R.id.product_trademark);
        tradeMarkTextView.setText(product.getProductTradeMark());

        TextView nameTextView = listItemView.findViewById(R.id.product_name);
        nameTextView.setText(product.getProductName());

        return listItemView;
    }


}