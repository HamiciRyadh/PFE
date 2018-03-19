package usthb.lfbservices.com.pfe.activities;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import usthb.lfbservices.com.pfe.R;
import usthb.lfbservices.com.pfe.fragments.MapFragment;
import usthb.lfbservices.com.pfe.fragments.ProductsFragment;
import usthb.lfbservices.com.pfe.fragments.SearchFragment;
import usthb.lfbservices.com.pfe.network.PfeRx;
import usthb.lfbservices.com.pfe.utils.DisposableManager;
import usthb.lfbservices.com.pfe.utils.Utils;

public class MapsActivity extends FragmentActivity implements SearchFragment.SearchFragmentActions
, ProductsFragment.ProductsFragmentActions, MapFragment.MapFragmentActions, OnMapReadyCallback {

    private static final String TAG = MapsActivity.class.getName();


    private SearchView searchView;
    private SearchFragment searchFragment;
    private ProductsFragment productsFragment;

    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (findViewById(R.id.map_frame_layout) != null)
        {
            if (savedInstanceState != null) return;

            searchFragment = new SearchFragment();
            productsFragment = new ProductsFragment();
        }

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        initVariables();
        initSearchView();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.e(TAG, "onMapClick");
                onMapSelected();
            }
        });

    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy");
        DisposableManager.dispose();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Log.e(TAG, "OnBackPressed");
        super.onBackPressed();
    }

    @Override
    public void onCategorySelected(int category) {
        if (Utils.isNetworkAvailable(this)) {
            //Remove focus from searchView so that the keyboark doesn't appear
            searchView.clearFocus();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.map_frame_layout, productsFragment, Utils.FRAGMENT_PRODUCTS)
                    .addToBackStack(null)
                    .commit();
            PfeRx.searchCategory(this, R.layout.list_item_products, category);
        }
        else {
            Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onProductSelected(int productId) {
        //Network call to search
    }

    public void onMapSelected() {
        if (searchView.getVisibility() == View.VISIBLE) {
            searchView.animate()
                    .translationY(searchView.getHeight())
                    .alpha(0.0f)
                    .setDuration(300);
            searchView.setVisibility(View.GONE);
        } else {
            searchView.animate()
                    .translationY(0)
                    .alpha(1.0f)
                    .setDuration(300);
            searchView.setVisibility(View.VISIBLE);
        }
    }

    public void initVariables() {
        searchView = findViewById(R.id.search_view);
    }

    public void initSearchView() {
        //searchView.setBackgroundColor(Color.TRANSPARENT);
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.e(TAG, "SearchView : OnQueryTextFocusChange");
                refreshDisplay();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e(TAG, "SearchView : OnQueryTextSubmit");
                if (getSupportFragmentManager().findFragmentByTag(Utils.FRAGMENT_SEARCH) != null) {
                    searchFragment.addToHistorySearchs(query);
                    searchFragment.refreshHistory();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.e(TAG, "SearchView : OnQueryTextChange");
                return false;
            }
        });

    }

    public void refreshDisplay() {
        if (getSupportFragmentManager().findFragmentByTag(Utils.FRAGMENT_SEARCH) /*!=*/== null) {
            Log.e(TAG, "here");
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.map_frame_layout, searchFragment, Utils.FRAGMENT_SEARCH)
                    .commit();
            if (getSupportFragmentManager().findFragmentByTag(Utils.FRAGMENT_SEARCH) != null)
                searchFragment.refreshHistory();
        } else {
            Log.e(TAG, "there");
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(searchFragment)
                    .commit();
        }
    }
}
