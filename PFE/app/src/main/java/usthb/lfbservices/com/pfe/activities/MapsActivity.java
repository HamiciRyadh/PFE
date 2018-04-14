package usthb.lfbservices.com.pfe.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import usthb.lfbservices.com.pfe.R;
import usthb.lfbservices.com.pfe.adapters.SalesPointsAdapter;
import usthb.lfbservices.com.pfe.database.DatabaseHelper;
import usthb.lfbservices.com.pfe.fragments.ProductsFragment;
import usthb.lfbservices.com.pfe.fragments.SearchFragment;
import usthb.lfbservices.com.pfe.models.BottomSheetDataSetter;
import usthb.lfbservices.com.pfe.models.ProductSalesPoint;
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
    private DatabaseHelper db;
    private BottomSheetBehavior sheetBehavior;
    private Button showButton;
    private ListView listViewSalesPoints;

    private Button btn_Wilaya;
    private Button btn_searchPerimeter;
    private Button btn_Ville;

    private String[] listItemsWilaya;
    private ArrayList<String> listItemsVille = new ArrayList<>();
    private boolean[] checkedItemsWilaya;
    private boolean[] checkedItemsVille;
    private ArrayList<Integer> mUserItemsWilaya = new ArrayList<>();
    private ArrayList<Integer> mUserItemsVille = new ArrayList<>();
    private int mUserItemRayon = -1;
    private String[] listVille = null;

    private double latitudeSearchPosition;
    private double longitudeSearchPosition;

    private boolean hasData = false;
    private LatLng userPosition;
    private Marker userMarker;
    private MarkerOptions userMakerOptions;
    private Circle circle = null ;

    private FusedLocationProviderClient mFusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (findViewById(R.id.map_frame_layout) != null) {
            if (savedInstanceState != null) return;

            searchFragment = new SearchFragment();
            productsFragment = new ProductsFragment();
        }

        if (Build.VERSION.SDK_INT >= 23) {
            if (!Utils.checkPermission(this)) {
                Utils.requestGPSPermissions(this);
            }
        }


        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        initVariables();
        initWilayaButton();
        initVillesButton();
        initSearchPerimeterButton();
        initSearchView();
        initBottomSheet();
    }

    public void addUserMarkerPosition() {
        if (Utils.checkPermission(this)) {
            Log.e(TAG, "Permitted");
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                latitudeSearchPosition = location.getLongitude();
                                longitudeSearchPosition = location.getLatitude();

                                userPosition = new LatLng(longitudeSearchPosition, latitudeSearchPosition);
                                userMakerOptions =  new MarkerOptions().
                                        position(userPosition).
                                        title("Votre position!").
                                        icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

                                userMarker= mMap.addMarker(userMakerOptions);
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userPosition, 15));
                            }
                        }
                    });
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (Utils.isGPSActivated(this)) {
            addUserMarkerPosition();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Singleton.getInstance().setMap(mMap);

        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));

        if (!Utils.checkPermission(this)) {
            Utils.requestGPSPermissions(this);
        }
        if (Utils.checkPermission(this)) {
            if (!Utils.isGPSActivated(this)) {
                Utils.activateGPS(this);
            }
            if (Utils.isGPSActivated(this)) {
                mMap.clear();
                addUserMarkerPosition();
            } else {
                LatLng alger = new LatLng(36.7525, 3.04197);
                mMap.addMarker(new MarkerOptions().position(alger).title("Alger"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(alger, 10));
            }
        } else {
            LatLng alger = new LatLng(36.7525, 3.04197);
            mMap.addMarker(new MarkerOptions().position(alger).title("Alger"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(alger, 10));
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
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
        if (listViewSalesPoints.getVisibility() == View.VISIBLE) {
            listViewSalesPoints.setVisibility(View.GONE);
        }
        if (sheetBehavior != null) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
        if (btn_Wilaya.getVisibility() == View.VISIBLE) {
            btn_Wilaya.setVisibility(View.GONE);
        }
        if (btn_Ville.getVisibility() == View.VISIBLE) {
            btn_Ville.setVisibility(View.GONE);
        }
        if (btn_searchPerimeter.getVisibility() == View.VISIBLE) {
            btn_searchPerimeter.setVisibility(View.GONE);
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
            PfeRx.searchCategory(this, category);
        } else {
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

        btn_Wilaya.setVisibility(View.VISIBLE);
        btn_searchPerimeter.setVisibility(View.VISIBLE);
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
        salesPointRating.setRating((float) salesPoint.getSalesPointRating());
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
        if (showButton.getVisibility() == View.VISIBLE) this.hasData = true;
        if (searchView.getVisibility() == View.VISIBLE) {
            searchView.animate()
                    .translationY(searchView.getHeight())
                    .alpha(0.0f)
                    .setDuration(300);
            searchView.setVisibility(View.GONE);
            if (sheetBehavior != null) sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            if (showButton != null) showButton.setVisibility(View.GONE);
            if (btn_Wilaya != null) btn_Wilaya.setVisibility(View.GONE);
            if (btn_Ville != null) btn_Ville.setVisibility(View.GONE);
            if (btn_searchPerimeter != null) btn_searchPerimeter.setVisibility(View.GONE);
        } else {
            searchView.animate()
                    .translationY(0)
                    .alpha(1.0f)
                    .setDuration(300);
            searchView.setVisibility(View.VISIBLE);
            if (this.hasData) {
                if (showButton != null) showButton.setVisibility(View.VISIBLE);
                showButton.setText(getResources().getString(R.string.sales_points_show_list));
                if (btn_Wilaya != null) btn_Wilaya.setVisibility(View.VISIBLE);
                //TODO:Faire en sorte qu'il réaparraise seulement dans le cas où une ville à été sélectionnée
                // if (btn_Ville != null) btn_Ville.setVisibility(View.VISIBLE);
                if (btn_searchPerimeter != null) btn_searchPerimeter.setVisibility(View.VISIBLE);
            }
        }
    }

    public void initVariables() {
        db = new DatabaseHelper(this);
        searchView = findViewById(R.id.search_view);
        sheetBehavior = BottomSheetBehavior.from(findViewById(R.id.layout_bottom_sheet));
        showButton = findViewById(R.id.show_list_button);
        listViewSalesPoints = findViewById(R.id.list_view_sales_points);

        btn_Wilaya = findViewById(R.id.btn_Wilaya);
        btn_Ville = findViewById(R.id.btn_Ville);
        btn_searchPerimeter = findViewById(R.id.btn_Rayon_Recherche);
        listItemsWilaya = db.getWilayas();
        checkedItemsWilaya = new boolean[listItemsWilaya.length];
    }

    public void initSearchView() {
        btn_Wilaya.setVisibility(View.GONE);
        btn_searchPerimeter.setVisibility(View.GONE);

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.e(TAG, "SearchView : OnQueryTextFocusChange");
                //DisposableManager.dispose();
                if (hasFocus) {
                    if (showButton.getVisibility() != View.GONE)
                        showButton.setVisibility(View.GONE);
                    if (btn_Wilaya != null) btn_Wilaya.setVisibility(View.GONE);
                    if (btn_Ville != null) btn_Ville.setVisibility(View.GONE);
                    if (btn_searchPerimeter != null) btn_searchPerimeter.setVisibility(View.GONE);
                    if (sheetBehavior != null)
                        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
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

    public void searchQuery(@NonNull String query) {
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
        } else {
            Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_LONG).show();
        }
    }


    public void refreshMap(List<SalesPoint> temporarySalespointList) {
        mMap.clear();

        for (SalesPoint salesPoint : temporarySalespointList) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(new LatLng(salesPoint.getSalesPointLat(), salesPoint.getSalesPointLong()))
                    .title(salesPoint.getSalesPointName())
                    .snippet(salesPoint.getSalesPointAddress());

            for (ProductSalesPoint productSalesPoint : Singleton.getInstance().getProductSalesPointList()) {
                if (productSalesPoint.getSalespointId().equals(salesPoint.getSalesPointId())) {
                    if (productSalesPoint.getProductQuantity() > 0) {
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    } else {
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    }
                }
            }
            mMap.addMarker(markerOptions);
        }
    }


    public void initVillesButton() {
        btn_Ville.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MapsActivity.this);
                mBuilder.setTitle(R.string.dialog_title_Ville);

                mBuilder.setMultiChoiceItems(listVille, checkedItemsVille, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        if (isChecked) {
                            mUserItemsVille.add(new Integer(position));
                        } else {
                            mUserItemsVille.remove(new Integer(position));
                        }
                    }

                });

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {

                        final List<SalesPoint> listsalesPoints = Singleton.getInstance().getSalesPointList();
                        List<SalesPoint> temporarySalespointList;

                        if (mUserItemsVille.size() == 0 || mUserItemsVille.size() == listItemsVille.size()) {
                            temporarySalespointList = Singleton.getInstance().getSalesPointList();
                        } else {
                            temporarySalespointList = new ArrayList<>();
                            for (SalesPoint salesPoint : listsalesPoints) {
                                for (int i = 0; i < mUserItemsVille.size(); i++) {
                                    String ville = listVille[mUserItemsVille.get(i)];
                                    if (salesPoint.getSalesPointAddress().contains(ville)) {
                                        temporarySalespointList.add(salesPoint);
                                    }
                                }
                            }
                        }

                        refreshMap(temporarySalespointList);

                        ((SalesPointsAdapter) listViewSalesPoints.getAdapter()).clear();
                        ((SalesPointsAdapter) listViewSalesPoints.getAdapter()).addAll(temporarySalespointList);
                    }
                });

                mBuilder.setNegativeButton(R.string.dismiss_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                mBuilder.setNeutralButton(R.string.clear_all_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        for (int i = 0; i < checkedItemsVille.length; i++) {
                            checkedItemsVille[i] = false;
                            mUserItemsVille.clear();

                            List<SalesPoint> temporarySalespointList = Singleton.getInstance().getSalesPointList();
                            ((SalesPointsAdapter) listViewSalesPoints.getAdapter()).clear();
                            ((SalesPointsAdapter) listViewSalesPoints.getAdapter()).addAll(temporarySalespointList);
                            refreshMap(temporarySalespointList);
                        }
                    }
                });
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });
    }


    public void initSearchPerimeterButton() {
        btn_searchPerimeter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Utils.isGPSActivated(MapsActivity.this)) {
                    if (!Utils.checkPermission(MapsActivity.this)) {
                        ActivityCompat.requestPermissions(MapsActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 123);
                    }
                    Utils.activateGPS(MapsActivity.this);
                } else {
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(MapsActivity.this);
                    mBuilder.setTitle(R.string.dialog_title_Perimeter);
                    final String[] listRayon = getResources().getStringArray(R.array.rayon_recherche);
                    mBuilder.setSingleChoiceItems(listRayon, mUserItemRayon, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mUserItemRayon = which;
                        }
                    });

                    mBuilder.setCancelable(false);
                    mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            int searchDiametre = 0;
                            switch (mUserItemRayon) {
                                case 0: {
                                    searchDiametre = 5000;
                                    break;
                                }
                                case 1: {
                                    searchDiametre = 10000;
                                    break;
                                }
                                case 2: {
                                    searchDiametre = 20000;
                                    break;
                                }
                                case 3: {
                                    searchDiametre = 50000;
                                    break;
                                }
                            }

                            if (userMakerOptions != null) mMap.addMarker(userMakerOptions);

                            if  (circle != null) {circle.remove();}
                            CircleOptions circleOptions =new CircleOptions()
                                    .center(userPosition)
                                    .radius(searchDiametre)
                                    .strokeColor(Color.DKGRAY)
                                    .fillColor( R.color.color_transparent);

                            circle = mMap.addCircle(circleOptions);

                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            List<SalesPoint> temporarySalesPointsList = Singleton.getInstance().getSalesPointList();
                            for (SalesPoint salesPoint : temporarySalesPointsList) {
                                if (Utils.isInsidePerimeter(userPosition, salesPoint.getSalesPointLatLng(), circle.getRadius())) {
                                    builder.include(salesPoint.getSalesPointLatLng());
                                }
                            }
                            if (userPosition != null) builder.include(userPosition);
                            LatLngBounds bounds = builder.build();
                            // offset from the edges of the map in pixels
                            int padding = 200;
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                            mMap.animateCamera(cu);
                        }
                    });
                    mBuilder.setNegativeButton(R.string.dismiss_label, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    mBuilder.setNeutralButton(R.string.clear_all_label, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            userMarker.remove();
                            circle.remove();
                            mUserItemRayon = -1;
                        }

                    });

                    AlertDialog mDialog = mBuilder.create();
                    mDialog.show();
                }
            }
        });
    }


    public void initWilayaButton() {
        btn_Wilaya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MapsActivity.this);
                mBuilder.setTitle(R.string.dialog_title_Wilaya);
                mBuilder.setMultiChoiceItems(listItemsWilaya, checkedItemsWilaya, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        if (isChecked) {
                            mUserItemsWilaya.add(new Integer(position));
                        } else {
                            mUserItemsWilaya.remove(new Integer(position));
                        }
                    }
                });

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {

                        listItemsVille.clear();

                        for (int i = 0; i < mUserItemsWilaya.size(); i++) {
                            String element = listItemsWilaya[mUserItemsWilaya.get(i).intValue()];

                            if (element.equals("Alger")) {
                                listItemsVille.addAll(Arrays.asList(getResources().getStringArray(R.array.Algiers_item)));
                            }
                            if (element.equals("Blida")) {
                                listItemsVille.addAll(Arrays.asList(getResources().getStringArray(R.array.Blida_item)));
                            }
                        }
                        if (mUserItemsWilaya.size() != 0) btn_Ville.setVisibility(View.VISIBLE);

                        listVille = listItemsVille.toArray(new String[listItemsVille.size()]);
                        checkedItemsVille = new boolean[listVille.length];
                        final List<SalesPoint> listsalesPoints = Singleton.getInstance().getSalesPointList();
                        List<SalesPoint> temporarySalespointList;

                        if ( mUserItemsWilaya.size() == 0 ||  mUserItemsWilaya.size() == listItemsWilaya.length) {
                            temporarySalespointList = Singleton.getInstance().getSalesPointList();
                        }
                        else {
                            temporarySalespointList = new ArrayList<>();
                            for (SalesPoint salesPoint : listsalesPoints) {
                                for (int i = 0; i < mUserItemsWilaya.size(); i++) {
                                    String wilaya = listItemsWilaya[mUserItemsWilaya.get(i)];
                                    if (salesPoint.getSalesPointWilaya().toUpperCase().contains(wilaya.toUpperCase())) {
                                        temporarySalespointList.add(salesPoint);
                                    } else {
                                        for (String ville : listItemsVille) {
                                            if (salesPoint.getSalesPointAddress().toUpperCase().contains(ville.toUpperCase())) {
                                                temporarySalespointList.add(salesPoint);
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        ((SalesPointsAdapter) listViewSalesPoints.getAdapter()).clear();
                        ((SalesPointsAdapter) listViewSalesPoints.getAdapter()).addAll(temporarySalespointList);
                        refreshMap(temporarySalespointList);
                    }
                });


                mBuilder.setNegativeButton(R.string.dismiss_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                mBuilder.setNeutralButton(R.string.clear_all_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        for (int i = 0; i < checkedItemsWilaya.length; i++) {
                            checkedItemsWilaya[i] = false;
                            mUserItemsWilaya.clear();

                            btn_Ville.setVisibility(View.GONE);
                            listItemsVille.clear();

                            List<SalesPoint> temporarySalespointList = Singleton.getInstance().getSalesPointList();
                            ((SalesPointsAdapter) listViewSalesPoints.getAdapter()).clear();
                            ((SalesPointsAdapter) listViewSalesPoints.getAdapter()).addAll(temporarySalespointList);
                            refreshMap(temporarySalespointList);
                        }
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });
    }
}