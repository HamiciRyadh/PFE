package usthb.lfbservices.com.pfe.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Map;

import usthb.lfbservices.com.pfe.R;
import usthb.lfbservices.com.pfe.models.ProductSalesPoint;

public class CustomInfoWindowGoogleMap implements GoogleMap.InfoWindowAdapter {

    private Context context;
    private Map<LatLng, ProductSalesPoint> hashMap;

    public CustomInfoWindowGoogleMap(Context ctx, Map<LatLng, ProductSalesPoint> hashMap){
        context = ctx;
        this.hashMap = hashMap;
    }

    public Map<LatLng, ProductSalesPoint> getHashMap() {
        return hashMap;
    }

    public void setHashMap(Map<LatLng, ProductSalesPoint> hashMap) {
        this.hashMap = hashMap;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        ProductSalesPoint object = this.hashMap.get(marker.getPosition());
        View view = null;

        if (object != null) {
            view = ((Activity)context).getLayoutInflater()
                    .inflate(R.layout.list_item_salespoint_product, null);

            TextView name = view.findViewById(R.id.sales_point_name_marker);
            TextView address = view.findViewById(R.id.sales_point_address_marker);
            TextView qte = view.findViewById(R.id.product_qte_marker);
            TextView price = view.findViewById(R.id.product_price_marker);

            name.setText(marker.getTitle());
            address.setText(marker.getSnippet());

            if ( object.getProductQuantity() < 0) qte.setText(context.getString(R.string.not_available));
            else qte.setText(String.valueOf(object.getProductQuantity()));

            price.setText(String.valueOf(object.getProductPrice()));

        }
        return view;
    }
}
 