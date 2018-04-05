package usthb.lfbservices.com.pfe.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Map;

import usthb.lfbservices.com.pfe.R;
import usthb.lfbservices.com.pfe.models.ProductSalesPoint;

public class CustomInfoWindowGoogleMap implements GoogleMap.InfoWindowAdapter {

    private Context context;
    private Map<Marker, ProductSalesPoint> hashMap;

    public CustomInfoWindowGoogleMap(Context ctx, Map<Marker, ProductSalesPoint> hashMap){
        context = ctx;
        this.hashMap = hashMap;
    }

    public Map<Marker, ProductSalesPoint> getHashMap() {
        return hashMap;
    }

    public void setHashMap(Map<Marker, ProductSalesPoint> hashMap) {
        this.hashMap = hashMap;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = ((Activity)context).getLayoutInflater()
                .inflate(R.layout.list_item_salespoint_product, null);

        ProductSalesPoint object = this.hashMap.get(marker);

        TextView name = view.findViewById(R.id.sales_point_name_marker);
        TextView address = view.findViewById(R.id.sales_point_address_marker);
        TextView qte = view.findViewById(R.id.product_qte_marker);
        TextView price = view.findViewById(R.id.product_price_marker);

        if (object != null) {
            name.setText(marker.getTitle());
            address.setText(marker.getSnippet());

            if ( object.getProductQuantity() < 0) qte.setText(context.getString(R.string.not_available));
            else qte.setText(String.valueOf(object.getProductQuantity()));

            price.setText(String.valueOf(object.getProductPrice()));

        }
        return view;
    }
}
 