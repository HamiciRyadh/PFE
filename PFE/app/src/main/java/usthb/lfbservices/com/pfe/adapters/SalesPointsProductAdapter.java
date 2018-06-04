package usthb.lfbservices.com.pfe.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import usthb.lfbservices.com.pfe.R;
import usthb.lfbservices.com.pfe.models.ProductSalesPoint;
import usthb.lfbservices.com.pfe.models.SalesPoint;
import usthb.lfbservices.com.pfe.models.Singleton;


public class SalesPointsProductAdapter extends ArrayAdapter<ProductSalesPoint> {

        private int layoutResourceId;


    public SalesPointsProductAdapter(Activity context, int layoutResourceId, ArrayList<ProductSalesPoint> data){
            super(context,0, data);
            this.layoutResourceId = layoutResourceId;
        }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    layoutResourceId, parent, false);
        }

        ProductSalesPoint productSalesPoint = getItem(position);
        if (productSalesPoint != null) {
            String salespointName, salespointAdresse;

            for( SalesPoint salesPoint : Singleton.getInstance().getSalesPointList()) {
                if (salesPoint.getSalesPointId().equals(productSalesPoint.getSalesPointId())) {
                    salespointName = salesPoint.getSalesPointName();
                    salespointAdresse = salesPoint.getSalesPointAddress();

                    TextView productPriceTextView = listItemView.findViewById(R.id.product_price_marker);
                    if (productPriceTextView != null)
                        productPriceTextView.setText(String.valueOf(productSalesPoint.getProductPrice()));
                    TextView productQteTextView = listItemView.findViewById(R.id.product_qte_marker);
                    if (productQteTextView != null)
                        productQteTextView.setText(String.valueOf(productSalesPoint.getProductQuantity()));

                    TextView salesPointNameTextView = listItemView.findViewById(R.id.sales_point_name_marker);
                    if (salesPointNameTextView != null)
                        salesPointNameTextView.setText(salespointName);
                    TextView salesPointAddressTextView = listItemView.findViewById(R.id.sales_point_address_marker);
                    if (salesPointAddressTextView != null)
                        salesPointAddressTextView.setText(salespointAdresse);
                    break;
                }
            }
        }

        return listItemView;
    }
}
