package usthb.lfbservices.com.pfe.activities;

import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
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

    private Button btnWilaya;
    private Button btnSearchPerimeter;
    private Button btnVille;

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
    private LatLng defaultPosition = new LatLng(36.7525, 3.04197);

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationManager locationManager;
    private Marker defaultMarker;

    //TODO: Add LocationProvider using network connexion and then save it in a variable and always
    //compare with the other variable obtained via GPS Provider using the accuracy to choose one to use

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (findViewById(R.id.map_frame_layout) != null) {
            if (savedInstanceState != null) return;

            searchFragment = new SearchFragment();
            productsFragment = new ProductsFragment();
        }
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

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
            Log.e(TAG, "MARKER 1");
        if (Utils.checkPermission(this)) {
            Log.e(TAG, "MARKER 2");
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            Log.e(TAG, "MARKER 333");
                            if (location != null) {
                                Log.e(TAG, "MARKER 4");
                                latitudeSearchPosition = location.getLongitude();
                                longitudeSearchPosition = location.getLatitude();

                                userPosition = new LatLng(longitudeSearchPosition, latitudeSearchPosition);
                                if (defaultMarker != null) defaultMarker.remove();
                                if (userMarker != null) {
                                    userMarker.setPosition(userPosition);
                                }
                                else {
                                    userMakerOptions =  new MarkerOptions().
                                            position(userPosition).
                                            title("Votre position!").
                                            icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

                                    userMarker= mMap.addMarker(userMakerOptions);
                                }
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userPosition, 15));
                                Log.e(TAG, "MARKER 5");
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "LOCATION FAILURE :  " + e);
                        }
                    });
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "OnResume");
        if (Utils.checkPermission(this)) {
            /** THIS CODE WORKS DO NOT DELETE
             * Liiiiittle problem, only works after approximately 20s
             */
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0, new android.location.LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.e(TAG, "15151515");
                    if (location != null) {
                        Log.e(TAG, "MARKER 14");
                        latitudeSearchPosition = location.getLongitude();
                        longitudeSearchPosition = location.getLatitude();

                        userPosition = new LatLng(longitudeSearchPosition, latitudeSearchPosition);
                        if (defaultMarker != null) defaultMarker.remove();
                        if (userMarker != null) {
                            Log.e(TAG, "Changing position");
                            userMarker.setPosition(userPosition);
                            Log.e(TAG, "Position changed");
                        }
                        else {
                            Log.e(TAG, "Creating");
                            userMakerOptions =  new MarkerOptions().
                                    position(userPosition).
                                    title("Votre position!").
                                    icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

                            userMarker= mMap.addMarker(userMakerOptions);
                        }
                        Log.e(TAG, "MARKER 15");
                    }
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {
                    Log.e(TAG, "CHANGED");
                }

                @Override
                public void onProviderEnabled(String s) {
                    Log.e(TAG, "ENABLED");
                }

                @Override
                public void onProviderDisabled(String s) {
                    Log.e(TAG, "DISABLED");
                }
            });
            if (Utils.isGPSActivated(this)) {
                Log.e(TAG, "GPS ACTIVATED2");
                if (userPosition == null) addUserMarkerPosition();
            } else {
                Log.e(TAG, "GPS NON ACTIVATED2");
                Utils.activateGPS(this);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Singleton.getInstance().setMap(mMap);

        //googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        if (!Utils.checkPermission(this)) {
            Log.e(TAG, "GPS AUTH1");
            Utils.requestGPSPermissions(this);
        }
        if (Utils.checkPermission(this)) {
            Log.e(TAG, "GPS AUTH2");
            if (Utils.isGPSActivated(this)) {
                Log.e(TAG, "GPS ACTIVATED4");
                mMap.clear();
                addUserMarkerPosition();
            } else {
                Log.e(TAG, "GPS ACTIVATED5");
                defaultMarker = mMap.addMarker(new MarkerOptions().position(defaultPosition).title("Alger"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultPosition, 10));
            }
        } else {
            Log.e(TAG, "GPS ACTIVATED6");
            defaultMarker = mMap.addMarker(new MarkerOptions().position(defaultPosition).title("Alger"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultPosition, 10));
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                onMapSelected();
            }
        });
    }

    // onPause ==> locationManager.removeUpdates(this);
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
        if (btnWilaya.getVisibility() == View.VISIBLE) {
            btnWilaya.setVisibility(View.GONE);
        }
        if (btnVille.getVisibility() == View.VISIBLE) {
            btnVille.setVisibility(View.GONE);
        }
        if (btnSearchPerimeter.getVisibility() == View.VISIBLE) {
            btnSearchPerimeter.setVisibility(View.GONE);
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

        btnWilaya.setVisibility(View.VISIBLE);
        btnSearchPerimeter.setVisibility(View.VISIBLE);
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
            if (btnWilaya != null) btnWilaya.setVisibility(View.GONE);
            if (btnVille != null) btnVille.setVisibility(View.GONE);
            if (btnSearchPerimeter != null) btnSearchPerimeter.setVisibility(View.GONE);
        } else {
            searchView.animate()
                    .translationY(0)
                    .alpha(1.0f)
                    .setDuration(300);
            searchView.setVisibility(View.VISIBLE);
            if (this.hasData) {
                if (showButton != null) showButton.setVisibility(View.VISIBLE);
                showButton.setText(getResources().getString(R.string.sales_points_show_list));
                if (btnWilaya != null) btnWilaya.setVisibility(View.VISIBLE);
                //TODO:Faire en sorte qu'il réaparraise seulement dans le cas où une ville à été sélectionnée
                // if (btn_Ville != null) btn_Ville.setVisibility(View.VISIBLE);
                if (btnSearchPerimeter != null) btnSearchPerimeter.setVisibility(View.VISIBLE);
            }
        }
    }

    public void initVariables() {
        db = new DatabaseHelper(this);
        searchView = findViewById(R.id.search_view);
        sheetBehavior = BottomSheetBehavior.from(findViewById(R.id.layout_bottom_sheet));
        showButton = findViewById(R.id.show_list_button);
        listViewSalesPoints = findViewById(R.id.list_view_sales_points);

        btnWilaya = findViewById(R.id.btn_Wilaya);
        btnVille = findViewById(R.id.btn_Ville);
        btnSearchPerimeter = findViewById(R.id.btn_Rayon_Recherche);
        listItemsWilaya = db.getWilayas();
        checkedItemsWilaya = new boolean[listItemsWilaya.length];
    }

    public void initSearchView() {
        btnWilaya.setVisibility(View.GONE);
        btnSearchPerimeter.setVisibility(View.GONE);

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.e(TAG, "SearchView : OnQueryTextFocusChange");
                //DisposableManager.dispose();
                if (hasFocus) {
                    if (showButton.getVisibility() != View.GONE)
                        showButton.setVisibility(View.GONE);
                    if (btnWilaya != null) btnWilaya.setVisibility(View.GONE);
                    if (btnVille != null) btnVille.setVisibility(View.GONE);
                    if (btnSearchPerimeter != null) btnSearchPerimeter.setVisibility(View.GONE);
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
        btnVille.setOnClickListener(new View.OnClickListener() {
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
        btnSearchPerimeter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Utils.isGPSActivated(MapsActivity.this)) {
                    if (!Utils.checkPermission(MapsActivity.this)) {
                        Utils.requestGPSPermissions(MapsActivity.this);
                    } else {
                        Utils.activateGPS(MapsActivity.this);
                    }
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

                            if (circle != null) {circle.remove();}
                            if (userPosition != null) {
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
                                builder.include(userPosition);
                                LatLngBounds bounds = builder.build();
                                // offset from the edges of the map in pixels
                                int padding = 200;
                                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                                mMap.animateCamera(cu);
                            }
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
                            if (userMarker != null) userMarker.remove();
                            if (circle != null) circle.remove();
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
        btnWilaya.setOnClickListener(new View.OnClickListener() {
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
                        if (mUserItemsWilaya.size() != 0) btnVille.setVisibility(View.VISIBLE);

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

                            btnVille.setVisibility(View.GONE);
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