package usthb.lfbservices.com.pfe.activities;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import usthb.lfbservices.com.pfe.R;
import usthb.lfbservices.com.pfe.network.PfeRx;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SearchView searchView;
    private ListView listView;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        searchView = findViewById(R.id.search_view);
        listView = findViewById(R.id.list_view_products);
        linearLayout = findViewById(R.id.vLayoutMaps);

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linearLayout.getVisibility() != View.GONE)
                {
                    linearLayout.setVisibility(View.GONE);
                    ((RelativeLayout.LayoutParams)searchView.getLayoutParams()).setMargins(30,10,20,0);
                }
                else
                {
                    linearLayout.setVisibility(View.VISIBLE);
                    ((RelativeLayout.LayoutParams)searchView.getLayoutParams()).setMargins(0,0,0,0);
                    PfeRx.getProducts(MapsActivity.this);
                }
            }
        });
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                {
                    linearLayout.setVisibility(View.GONE);
                    ((RelativeLayout.LayoutParams)searchView.getLayoutParams()).setMargins(30,10,20,0);
                }
                else
                {
                    linearLayout.setVisibility(View.VISIBLE);
                    ((RelativeLayout.LayoutParams)searchView.getLayoutParams()).setMargins(0,0,0,0);
                    PfeRx.getProducts(MapsActivity.this);
                }
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

}
