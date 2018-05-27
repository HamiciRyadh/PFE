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

import usthb.lfbservices.com.pfe.R;
import usthb.lfbservices.com.pfe.models.SalesPoint;


public class SalesPointsAdapter extends ArrayAdapter<SalesPoint> {

        private Context context;
        private int layoutResourceId;

        public SalesPointsAdapter(Activity context, int layoutResourceId, ArrayList<SalesPoint> data){
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

            SalesPoint salesPoint = getItem(position);

            if (salesPoint != null) {
                TextView salesPointNameTextView = listItemView.findViewById(R.id.sales_point_name_list);
                if (salesPointNameTextView != null ) salesPointNameTextView.setText(salesPoint.getSalesPointName());

                TextView salesPointAddressTextView = listItemView.findViewById(R.id.sales_point_address_list);
                if (salesPointAddressTextView != null) salesPointAddressTextView.setText(salesPoint.getSalesPointAddress());
            }

            return listItemView;
        }
}
