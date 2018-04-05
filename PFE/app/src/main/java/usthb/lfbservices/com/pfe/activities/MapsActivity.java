package usthb.lfbservices.com.pfe.activities;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import usthb.lfbservices.com.pfe.R;
import usthb.lfbservices.com.pfe.fragments.ProductsFragment;
import usthb.lfbservices.com.pfe.fragments.SearchFragment;
import usthb.lfbservices.com.pfe.models.BottomSheetDataSetter;
import usthb.lfbservices.com.pfe.models.SalesPoint;
import usthb.lfbservices.com.pfe.models.Singleton;
import usthb.lfbservices.com.pfe.network.PfeRx;
import usthb.lfbservices.com.pfe.utils.DisposableManager;
import usthb.lfbservices.com.pfe.utils.Utils;

public class MapsActivity extends FragmentActivity implements SearchFragment.SearchFragmentActions
, ProductsFragment.ProductsFragmentActions, BottomSheetDataSetter, OnMapReadyCallback {


    private static final String TAG = MapsActivity.class.getName();

    private SearchView searchView;
    private SearchFragment searchFragment;
    private ProductsFragment productsFragment;

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private BottomSheetBehavior sheetBehavior;
    private Button showButton;

    private boolean hasData = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
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
        initBottomSheet();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Singleton.getInstance().setMap(mMap);

        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
        LatLng alger = new LatLng(36.7525, 3.04197);
        mMap.addMarker(new MarkerOptions().position(alger).title("Alger"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(alger,10));

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
        if (showButton.getVisibility() == View.VISIBLE) {
            showButton.setVisibility(View.GONE);
        }
        super.onBackPressed();
    }

    @Override
    public void onCategorySelected(int category) {
        if (Utils.isNetworkAvailable(this)) {
            /**
             * Remove focus from searchView so that the keyboark doesn't appear.
             */
            searchView.clearFocus();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.map_frame_layout, productsFragment, Utils.FRAGMENT_PRODUCTS)
                    .addToBackStack(null)
                    .remove(searchFragment)
                    .addToBackStack(null)
                    .commit();
            PfeRx.searchCategory(this,category);
        }
        else {
            Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onProductSelected(final int productId) {
        getSupportFragmentManager()
                .beginTransaction()
                .remove(productsFragment)
                .addToBackStack(null)
                .commit();
        PfeRx.searchFromProductId(this, productId);
    }

    @Override
    public void setBottomSheetData(@NonNull SalesPoint salesPoint) {
        TextView nameTextView = findViewById(R.id.sales_point_name_details);
        TextView addressTextView = findViewById(R.id.sales_point_address_details);

        nameTextView.setText(salesPoint.getSalesPointName());
        addressTextView.setText(salesPoint.getSalesPointAddress());
    }

    @Override
    public void setBottomSheetDataDetails(@NonNull SalesPoint salesPoint) {
        ImageView salesPointPhoto = findViewById(R.id.sales_point_image_details);
        RatingBar salesPointRating = findViewById(R.id.sales_point_rating_details);
        TextView salesPointPhoneNumber = findViewById(R.id.sales_point_phone_number_details);
        TextView salesPointWebSite = findViewById(R.id.sales_point_website_details);

        Picasso.get()
                .load(Utils.buildGooglePictureUri(MapsActivity.this, salesPoint.getSalesPointPhotoReference()))
                .into(salesPointPhoto);
        salesPointRating.setRating((float)salesPoint.getSalesPointRating());
        salesPointPhoneNumber.setText(salesPoint.getSalesPointPhoneNumber());
        salesPointWebSite.setText(salesPoint.getSalesPointWebSite());
    }

    @Override
    public void setBottomSheetState(int state) {
        if (state == BottomSheetBehavior.PEEK_HEIGHT_AUTO || (state >= BottomSheetBehavior.STATE_DRAGGING
        && state <= BottomSheetBehavior.STATE_HIDDEN)) {
            sheetBehavior.setState(state);
        }
    }

    public void onMapSelected() {
        ListView listViewSalesPoints = findViewById(R.id.list_view_sales_points);
        if (showButton.getVisibility() == View.VISIBLE) this.hasData = true;
        if (searchView.getVisibility() == View.VISIBLE) {
            searchView.animate()
                    .translationY(searchView.getHeight())
                    .alpha(0.0f)
                    .setDuration(300);
            searchView.setVisibility(View.GONE);
            if (listViewSalesPoints != null) listViewSalesPoints.setVisibility(View.GONE);
            if (showButton != null) showButton.setVisibility(View.GONE);
        } else {
            searchView.animate()
                    .translationY(0)
                    .alpha(1.0f)
                    .setDuration(300);
            searchView.setVisibility(View.VISIBLE);
            if (this.hasData) {
                if (showButton != null) showButton.setVisibility(View.VISIBLE);
                showButton.setText(getResources().getString(R.string.sales_points_show_list));
            }
        }
    }

    public void initVariables() {
        searchView = findViewById(R.id.search_view);
        sheetBehavior = BottomSheetBehavior.from(findViewById(R.id.layout_bottom_sheet));
        showButton = findViewById(R.id.show_list_button);
    }

    public void initSearchView() {
        //searchView.setBackgroundColor(Color.TRANSPARENT);

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.e(TAG, "SearchView : OnQueryTextFocusChange");
                //DisposableManager.dispose();
                if (hasFocus) {
                    if (showButton.getVisibility() != View.GONE)
                        showButton.setVisibility(View.GONE);
                    if (productsFragment.isAdded()) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .remove(productsFragment)
                                .commit();
                    }
                    if (!searchFragment.isVisible()) {
                        Log.e(TAG, "Setting SearchFragment");
                        getSupportFragmentManager()
                                .beginTransaction()
                                .add(R.id.map_frame_layout, searchFragment, Utils.FRAGMENT_SEARCH)
                                .addToBackStack(null)
                                .commit();
                        if (getSupportFragmentManager().findFragmentByTag(Utils.FRAGMENT_SEARCH) != null)
                            searchFragment.refreshHistory();
                    }
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e(TAG, "SearchView : OnQueryTextSubmit");
                if (getSupportFragmentManager().findFragmentByTag(Utils.FRAGMENT_SEARCH) != null) {
                    searchFragment.addToHistorySearches(query);
                    searchFragment.refreshHistory();
                    searchQuery(query);
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

    public void initBottomSheet() {
        if (sheetBehavior.isHideable()) sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        //btnBottomSheet.setText("Close Sheet");
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        //btnBottomSheet.setText("Expand Sheet");
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    public void searchQuery (@NonNull String query) {
        if (Utils.isNetworkAvailable(this)) {
            searchView.clearFocus();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.map_frame_layout, productsFragment, Utils.FRAGMENT_PRODUCTS)
                    .addToBackStack(null)
                    .remove(searchFragment)
                    .addToBackStack(null)
                    .commit();
            PfeRx.searchFromQuery(this, query);
        }
        else {
            Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_LONG).show();
        }
    }

    //Probably to remove
    public void refreshDisplay() {
        if (showButton.getVisibility() != View.GONE) showButton.setVisibility(View.GONE);
        if (getSupportFragmentManager().findFragmentByTag(Utils.FRAGMENT_SEARCH) == null) {
            Log.e(TAG, "Setting SearchFragment");
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.map_frame_layout, searchFragment, Utils.FRAGMENT_SEARCH)
                    .commit();
            if (getSupportFragmentManager().findFragmentByTag(Utils.FRAGMENT_SEARCH) != null)
                searchFragment.refreshHistory();
        } else {
            Log.e(TAG, "Removing SearchFragment");
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(searchFragment)
                    .commit();
        }
    }

}