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
 * Created by ryadh on 05/03/18.
 */

public class ProductsAdapter extends ArrayAdapter<Product> {

    private Context context;
    private int layoutResourceId;
    private ArrayList<Product> data;

    public ProductsAdapter(Activity context, int layoutResourceId, ArrayList<Product> data){
        super(context,0, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
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

       if (product != null) {
           TextView tradeMarkTextView = listItemView.findViewById(R.id.product_trademark);
           if (tradeMarkTextView != null) tradeMarkTextView.setText(product.getProductTradeMark());

           TextView nameTextView = listItemView.findViewById(R.id.product_name);
           if (nameTextView != null) nameTextView.setText(product.getProductName());
       }

        return listItemView;
    }

    public ArrayList<Product> getData() {
        return data;
    }
}