package usthb.lfbservices.com.pfe.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import usthb.lfbservices.com.pfe.R;
import usthb.lfbservices.com.pfe.roomDatabase.AppRoomDatabase;
import usthb.lfbservices.com.pfe.adapters.SalesPointsAdapter;
import usthb.lfbservices.com.pfe.models.ProductSalesPoint;
import usthb.lfbservices.com.pfe.models.SalesPoint;
import usthb.lfbservices.com.pfe.models.Singleton;
import usthb.lfbservices.com.pfe.models.Wilaya;
import usthb.lfbservices.com.pfe.network.PfeRx;
import usthb.lfbservices.com.pfe.utils.Constants;
import usthb.lfbservices.com.pfe.utils.Utils;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by ryadh on 06/05/18.
 */

public class FragmentMap extends Fragment  implements OnMapReadyCallback {

    private static final String TAG = "FragmentMap";

    private MapActions implementation;
    private View rootView;
    private FragmentActivity fragmentBelongActivity;

    public static final float ZOOM_LEVEL = 16.77f;

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private AppRoomDatabase db;
    private BottomSheetBehavior sheetBehavior;
    private Button showButton;
    private ListView listViewSalesPoints;
    private FloatingActionButton userLocation;
    private DescProductFragment descProductFragment;

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
    private static MarkerOptions userMakerOptions;
    private Circle circle = null ;
    private LatLng defaultPosition = new LatLng(36.7525, 3.04197);
    private boolean addMarker = true;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationManager locationManager;
    private Marker defaultMarker;

    private Location currentLocation;
    private boolean isActivateGPSVisible = false;
    private GPSLocationListener gpsLocationListener = new GPSLocationListener();

    public FragmentMap() {

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");
        rootView = inflater.inflate(R.layout.activity_maps, container, false);
        fragmentBelongActivity = getActivity();

        if (rootView != null) {
            mapFragment = SupportMapFragment.newInstance();
            mapFragment.getMapAsync(this);
            getChildFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();

            locationManager = (LocationManager) fragmentBelongActivity.getSystemService(LOCATION_SERVICE);
            if (Build.VERSION.SDK_INT >= 23) {
                if (!Utils.checkGPSPermission(fragmentBelongActivity)) {
                    Utils.requestGPSPermission(fragmentBelongActivity);
                }
            }

            initVariables();
            initBottomSheet();
            initWilayaButton();
            initVillesButton();
            initSearchPerimeterButton();
            initUserLocation();
        }

        implementation.checkGoToSearchFragment();
        implementation.checkSearchProductBarcode();
        implementation.setToolbarTitleForFragmentMap();

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e(TAG, "onMapReady");
        mMap = googleMap;
        Singleton.getInstance().setMap(mMap);
        SharedPreferences sharedPreferences = fragmentBelongActivity.getSharedPreferences(Constants.SHARED_PREFERENCES_POSITION, fragmentBelongActivity.MODE_PRIVATE);
        String sLat = sharedPreferences.getString(Constants.SHARED_PREFERENCES_POSITION_LATITUDE,null);
        String sLong = sharedPreferences.getString(Constants.SHARED_PREFERENCES_POSITION_LONGITUDE,null);
        String sMapStyle = sharedPreferences.getString(Constants.SHARED_PREFERENCES_USER_MAP_STYLE, null);

        if (sLat != null && sLong != null) {
            double lat = Double.parseDouble(sLat);
            double lon = Double.parseDouble(sLong);
            userPosition = new LatLng(lat, lon);
        }

        if (Utils.checkGPSPermission(fragmentBelongActivity)) {
            if (addMarker && userPosition != null) {
                userMakerOptions = new MarkerOptions().
                        position(userPosition).
                        title(getResources().getString(R.string.your_position)).
                        icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue));

                userMarker = mMap.addMarker(userMakerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userPosition, ZOOM_LEVEL));
            } else {
                addUserMarkerPosition(true);
            }
        } else {
            defaultMarker = mMap.addMarker(new MarkerOptions().position(defaultPosition)
                    .title(getResources().getString(R.string.default_marker_title))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultPosition, ZOOM_LEVEL));
        }
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                onMapSelected();
            }
        });
        Log.e(TAG, "Style " + (sMapStyle == null));
        if (sMapStyle != null) {
            if (sMapStyle.equalsIgnoreCase(Constants.SATELLITE)) {
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            } else if (sMapStyle.equalsIgnoreCase(Constants.STANDARD)) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                //mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(fragmentBelongActivity, R.raw.map_style));
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        Log.e(TAG, "onAttach");
        super.onAttach(context);
        addMarker = true;
        try {
            implementation = (MapActions) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement MapActions");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "OnResume");
        if (Utils.checkGPSPermission(fragmentBelongActivity)) {
            if (addMarker && userPosition != null) {
                if (userMarker != null) userMarker.setPosition(userPosition);
                else {
                    userMakerOptions =  new MarkerOptions().
                            position(userPosition).
                            title(getResources().getString(R.string.your_position)).
                            icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue));

                    userMarker = mMap.addMarker(userMakerOptions);
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userPosition, ZOOM_LEVEL));
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, gpsLocationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, gpsLocationListener);

            if (Utils.isGPSActivated(fragmentBelongActivity)) {
                Log.e(TAG, "Adding user marker position from onResume.");
            } else {
                if (!isActivateGPSVisible) {
                    isActivateGPSVisible = true;
                    Utils.activateGPS(fragmentBelongActivity);
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "OnPause");
        locationManager.removeUpdates(gpsLocationListener);
    }

    @Override
    public void onDetach() {
        Log.e(TAG, "OnDetach");
        super.onDetach();
        implementation = null;
    }

    public void initVariables() {
        db = AppRoomDatabase.getInstance(fragmentBelongActivity);
        sheetBehavior = BottomSheetBehavior.from(rootView.findViewById(R.id.layout_bottom_sheet));
        showButton = rootView.findViewById(R.id.show_list_button);
        listViewSalesPoints = rootView.findViewById(R.id.list_view_sales_points);
        userLocation = rootView.findViewById(R.id.geolocalisation);
        btnWilaya = rootView.findViewById(R.id.btn_Wilaya);
        btnVille = rootView.findViewById(R.id.btn_Ville);
        btnWilaya.setVisibility(View.GONE);
        btnSearchPerimeter = rootView.findViewById(R.id.btn_Rayon_Recherche);
        btnSearchPerimeter.setVisibility(View.GONE);
        ArrayList<String> wilayaNames = new ArrayList<>();
        for (Wilaya wilaya : Wilaya.Data()) wilayaNames.add(wilaya.getWilayaName());
        listItemsWilaya = new String[wilayaNames.size()];
        listItemsWilaya = wilayaNames.toArray(listItemsWilaya);
        checkedItemsWilaya = new boolean[Wilaya.Data().size()];
    }

    public void initUserLocation() {
        if (userLocation != null) {
            userLocation.setVisibility(View.VISIBLE);
            userLocation.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    SharedPreferences preferences = fragmentBelongActivity.getSharedPreferences(Constants.SHARED_PREFERENCES_POSITION, MODE_PRIVATE);
                    String sUserLatitude = preferences.getString(Constants.SHARED_PREFERENCES_POSITION_LATITUDE, null);
                    String sUserLongitude = preferences.getString(Constants.SHARED_PREFERENCES_POSITION_LONGITUDE, null);
                    LatLng sUserPosition = null;

                    if (sUserLatitude != null && sUserLongitude != null) {
                        try {
                            sUserPosition = new LatLng(Double.parseDouble(sUserLatitude), Double.parseDouble(sUserLongitude));
                        } catch (Exception e) {
                            Log.e(TAG, "Exception creating user position : " + e);
                        }
                    }

                    if (sUserPosition != null) userPosition = sUserPosition;
                    if (userPosition != null) {
                        if (defaultMarker != null) defaultMarker.remove();
                               if (userMarker != null) userMarker.setPosition(userPosition);
                               else {
                                   userMarker = mMap.addMarker(new MarkerOptions().
                                           position(userPosition).
                                           title(getResources().getString((Utils.isNetworkAvailable(fragmentBelongActivity) ? R.string.your_position : R.string.last_known_position))).
                                           icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue)));
                               }

                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userPosition, FragmentMap.ZOOM_LEVEL));
                    } else {
                        if (userMarker != null) userMarker.remove();
                        defaultMarker = mMap.addMarker(new MarkerOptions().
                                position(defaultPosition).
                                title(getResources().getString(R.string.default_marker_title)).
                                icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue)));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(defaultPosition, FragmentMap.ZOOM_LEVEL));
                    }
                }
            });
        }
    }

    public void searchQuery(@NonNull String query) {
            implementation.iconifySearchView();
            ProductsFragment productsFragment = implementation.getActivityProductsFragment();
            SearchFragment searchFragment = implementation.getActivitySearchFragment();
            productsFragment.clearProductsFragment();
            getChildFragmentManager()
                    .beginTransaction()
                    .add(R.id.map_frame_layout, productsFragment, Constants.FRAGMENT_PRODUCTS)
                    .addToBackStack(null)
                    .remove(searchFragment)
                    .addToBackStack(null)
                    .commit();
            PfeRx.searchFromQuery(fragmentBelongActivity, query);
    }

    public void onCategorySelected(int category) {
        if (Utils.isNetworkAvailable(fragmentBelongActivity)) {
            implementation.iconifySearchView();
            SearchFragment searchFragment = implementation.getActivitySearchFragment();
            ProductsFragment productsFragment = implementation.getActivityProductsFragment();
            productsFragment.clearProductsFragment();
            getChildFragmentManager()
                    .beginTransaction()
                    .add(R.id.map_frame_layout, productsFragment, Constants.FRAGMENT_PRODUCTS)
                    .addToBackStack(null)
                    .remove(searchFragment)
                    .addToBackStack(null)
                    .commit();
            PfeRx.searchCategory(fragmentBelongActivity, category);
        } else {
            Toast.makeText(fragmentBelongActivity, getString(R.string.no_internet), Toast.LENGTH_LONG).show();
        }
    }

    public void onProductSelected(final String productBarcode) {
        if (Utils.isNetworkAvailable(fragmentBelongActivity)) {
            addUserMarkerPosition(false);
            checkedItemsWilaya = new boolean[listItemsWilaya.length];
            mUserItemsVille = new ArrayList<>();
            mUserItemRayon = -1;
            ProductsFragment productsFragment = implementation.getActivityProductsFragment();
            getChildFragmentManager()
                    .beginTransaction()
                    .remove(productsFragment)
                    .addToBackStack(null)
                    .commit();
            PfeRx.searchFromProductBarcode(fragmentBelongActivity, productBarcode);
            hideWilayaAndPerimeter();
        } else {
            Toast.makeText(fragmentBelongActivity, getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
        }
    }

    public void addUserMarkerPosition(final boolean zoom) {
        if (Utils.checkGPSPermission(fragmentBelongActivity)) {
            if (Utils.isGPSActivated(fragmentBelongActivity)) {
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(fragmentBelongActivity);
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(fragmentBelongActivity, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    Log.e(TAG, "AddUserMarkerPosition:Updating position.");
                                    if (currentLocation == null) currentLocation = location;
                                    if (location.getAccuracy() > currentLocation.getAccuracy()) {
                                        Log.e(TAG, "AddUserMarkerPosition:Bad Accuracy, do nothing.");
                                        return;
                                    }
                                    Log.e(TAG, "AddUserMarkerPosition: Good Accuracy.");

                                    currentLocation = location;

                                    latitudeSearchPosition = location.getLongitude();
                                    longitudeSearchPosition = location.getLatitude();

                                    userPosition = new LatLng(longitudeSearchPosition, latitudeSearchPosition);
                                    if (defaultMarker != null) defaultMarker.remove();
                                    if (userMarker != null) {
                                        userMarker.setPosition(userPosition);
                                        Log.e(TAG, "AddUserMarkerPosition: set position.");
                                    }
                                    else {
                                        userMakerOptions =  new MarkerOptions().
                                                position(userPosition).
                                                title(getResources().getString(R.string.your_position)).
                                                icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue));

                                        userMarker= mMap.addMarker(userMakerOptions);
                                        Log.e(TAG, "AddUserMarkerPosition: AddMarker.");
                                    }
                                    SharedPreferences.Editor editor =
                                            fragmentBelongActivity.getSharedPreferences(Constants.SHARED_PREFERENCES_POSITION, MODE_PRIVATE).edit();
                                    editor.putString(Constants.SHARED_PREFERENCES_POSITION_LATITUDE, ""+userPosition.latitude);
                                    editor.putString(Constants.SHARED_PREFERENCES_POSITION_LONGITUDE, ""+userPosition.longitude);
                                    editor.apply();
                                    if (zoom) mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userPosition, FragmentMap.ZOOM_LEVEL));
                                } else {
                                    Log.e(TAG, "AddUserMarkerPosition: Else location null.");
                                    if (userPosition != null) {
                                        if (userMarker != null) userMarker.setPosition(userPosition);
                                        else {
                                            userMarker = mMap.addMarker(new MarkerOptions().position(userPosition)
                                                    .title(getResources().getString(R.string.last_known_position))
                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue)));
                                        }

                                        if (zoom) mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userPosition, FragmentMap.ZOOM_LEVEL));
                                    }
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "AddUserMarkerPosition: Location update failure :  " + e);
                            }
                        });
            } else {
                Log.e(TAG, "AddUserMarkerPosition: Else gps.");
                if (userMarker != null) userMarker.remove();
                defaultMarker = mMap.addMarker(new MarkerOptions().position(defaultPosition)
                        .title(getResources().getString(R.string.default_marker_title))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue)));
                if (zoom) mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(defaultPosition, FragmentMap.ZOOM_LEVEL));
            }
        } else {
            Log.e(TAG, "AddUserMarkerPosition: Else authorisation.");
            if (userMarker != null) userMarker.remove();
            defaultMarker = mMap.addMarker(new MarkerOptions().position(defaultPosition)
                    .title(getResources().getString(R.string.default_marker_title))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue)));
            if (zoom) mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(defaultPosition, FragmentMap.ZOOM_LEVEL));
        }
    }

    public void onMapSelected() {
        Log.e(TAG, "Zoom : " + mMap.getCameraPosition().zoom);
        if (showButton.getVisibility() == View.VISIBLE) this.hasData = true;
        SearchFragment searchFragment = implementation.getActivitySearchFragment();
        if (searchFragment.isVisible()) return;
        if (userLocation.getVisibility() == View.VISIBLE) {
            Log.e(TAG, "enter1");
            if (sheetBehavior != null) sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            if (showButton != null) showButton.setVisibility(View.GONE);
            if (btnWilaya != null) btnWilaya.setVisibility(View.GONE);
            if (btnVille != null) btnVille.setVisibility(View.GONE);
            if (btnSearchPerimeter != null) btnSearchPerimeter.setVisibility(View.GONE);
            if (listViewSalesPoints != null) listViewSalesPoints.setVisibility(View.GONE);
            Log.e(TAG, "UserLocation 1: " + (userLocation == null));
            if (userLocation != null) userLocation.setVisibility(View.GONE);
        } else {
            Log.e(TAG, "enter2");
            Log.e(TAG, "UserLocation 2: " + (userLocation == null));
            if(userLocation != null) userLocation.setVisibility(View.VISIBLE);
            if (this.hasData) {
                if (showButton != null) {
                    showButton.setVisibility(View.VISIBLE);
                    showButton.setText(getResources().getString(R.string.sales_points_show_list));
                }
                if (btnWilaya != null) btnWilaya.setVisibility(View.VISIBLE);
                if (mUserItemsWilaya.size() != 0) btnVille.setVisibility(View.VISIBLE);
                if (btnSearchPerimeter != null) btnSearchPerimeter.setVisibility(View.VISIBLE);
            }
        }
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
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
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

    public void refreshMap(@NonNull List<SalesPoint> temporarySalesPointList) {
        mMap.clear();

        if (temporarySalesPointList.isEmpty()) {
            Toast.makeText(fragmentBelongActivity, getResources().getString(R.string.nothing_match_filters), Toast.LENGTH_LONG).show();
            return;
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (SalesPoint salesPoint : temporarySalesPointList) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(new LatLng(salesPoint.getSalesPointLat(), salesPoint.getSalesPointLong()))
                    .title(salesPoint.getSalesPointName())
                    .snippet(salesPoint.getSalesPointAddress());

            for (ProductSalesPoint productSalesPoint : Singleton.getInstance().getProductSalesPointList()) {
                if (productSalesPoint.getSalesPointId().equals(salesPoint.getSalesPointId())) {
                    if (productSalesPoint.getProductQuantity() > 0) {
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_green));
                    } else {
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_red));
                    }
                }
            }
            mMap.addMarker(markerOptions);
            builder.include(salesPoint.getSalesPointLatLng());
        }

        if (userPosition != null) {
            mMap.addMarker(new MarkerOptions().position(userPosition)
                    .title(getResources().getString(R.string.your_position))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue)));
            //TODO: Est ce qu'on ajoute le marquer représentant l'utilisateur à la liste des marqueurs sur les quels centrer?
            //builder.include(userPosition);
        }

        if (temporarySalesPointList.size() == 1 ) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(temporarySalesPointList.get(0).getSalesPointLatLng(), FragmentMap.ZOOM_LEVEL));
        } else {
            LatLngBounds bounds = builder.build();
            // offset from the edges of the map in pixels
            int padding = 80;
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mMap.animateCamera(cu);
        }
    }

    public void initVillesButton() {
        btnVille.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ArrayList<Integer> mUserItemsVilleSave = new ArrayList<Integer>();
                mUserItemsVilleSave.addAll(mUserItemsVille);

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(fragmentBelongActivity);
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
                        mUserItemsVille.clear();
                        mUserItemsVille.addAll(mUserItemsVilleSave);
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
                if (!Utils.isGPSActivated(fragmentBelongActivity)) {
                    if (!Utils.checkGPSPermission(fragmentBelongActivity)) {
                        Utils.requestGPSPermission(fragmentBelongActivity);
                    } else {
                        Utils.activateGPS(fragmentBelongActivity);
                    }
                } else {
                    final int mUserItemRayonSave = mUserItemRayon;
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(fragmentBelongActivity);
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
                                int nbMarkers = 0;
                                for (SalesPoint salesPoint : temporarySalesPointsList) {

                                    if (Utils.isInsidePerimeter(userPosition, salesPoint.getSalesPointLatLng(), circle.getRadius())) {
                                        builder.include(salesPoint.getSalesPointLatLng());
                                        nbMarkers++;
                                    }
                                }
                                if (nbMarkers != 0) {
                                    builder.include(userPosition);
                                    LatLngBounds bounds = builder.build();
                                    // offset from the edges of the map in pixels
                                    int padding = 200;
                                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                                    mMap.animateCamera(cu);
                                } else {
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userPosition, FragmentMap.ZOOM_LEVEL));
                                    Toast.makeText(fragmentBelongActivity, getResources().getString(R.string.no_marker_in_search_area), Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
                    mBuilder.setNegativeButton(R.string.dismiss_label, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mUserItemRayon = mUserItemRayonSave;
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
                final boolean[] checkedItemsWilayaSave = checkedItemsWilaya.clone();

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(fragmentBelongActivity);
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

                        if (listViewSalesPoints.getAdapter() != null) {
                            ((SalesPointsAdapter)listViewSalesPoints.getAdapter()).clear();
                            ((SalesPointsAdapter)listViewSalesPoints.getAdapter()).addAll(temporarySalespointList);
                            refreshMap(temporarySalespointList);
                        }
                    }
                });


                mBuilder.setNegativeButton(R.string.dismiss_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        checkedItemsWilaya = checkedItemsWilayaSave;
                        dialogInterface.dismiss();
                    }
                });

                mBuilder.setNeutralButton(R.string.clear_all_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        for (int i = 0; i < checkedItemsWilaya.length; i++) {
                            checkedItemsWilaya[i] = false;
                        }
                        mUserItemsWilaya.clear();

                        btnVille.setVisibility(View.GONE);
                        listItemsVille.clear();

                        List<SalesPoint> temporarySalesPointList = Singleton.getInstance().getSalesPointList();
                        if (listViewSalesPoints.getAdapter() != null) {
                            ((SalesPointsAdapter)listViewSalesPoints.getAdapter()).clear();
                            ((SalesPointsAdapter)listViewSalesPoints.getAdapter()).addAll(temporarySalesPointList);
                            refreshMap(temporarySalesPointList);
                        }
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });
    }

    public void hideWilayaAndPerimeter() {
        btnWilaya.setVisibility(View.VISIBLE);
        btnSearchPerimeter.setVisibility(View.VISIBLE);
    }

    public void changeSheetBehaviorState(int state) {
        sheetBehavior.setState(state);
    }

    public void onBackPressed() {
        if (showButton != null) {
            showButton.setVisibility(View.GONE);
        }
        if (listViewSalesPoints != null) {
            listViewSalesPoints.setVisibility(View.GONE);
        }
        if (userLocation != null) {
            userLocation.setVisibility(View.GONE);
        }
        if (sheetBehavior != null) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
        if (btnWilaya != null) {
            btnWilaya.setVisibility(View.GONE);
        }
        if (btnVille != null) {
            btnVille.setVisibility(View.GONE);
        }
        if (btnSearchPerimeter != null) {
            btnSearchPerimeter.setVisibility(View.GONE);
        }
    }

    public void hideSearchFragment() {
            SearchFragment searchFragment = implementation.getActivitySearchFragment();
            if (searchFragment != null) {
                if (getChildFragmentManager().findFragmentByTag(Constants.FRAGMENT_SEARCH) != null)
                    getChildFragmentManager()
                            .beginTransaction()
                            .remove(searchFragment)
                            .commit();
        }
    }

    public void removeSearchFragment() {
        SearchFragment searchFragment = implementation.getActivitySearchFragment();
        if (getChildFragmentManager().findFragmentByTag(Constants.FRAGMENT_SEARCH) != null)
            getChildFragmentManager()
                    .beginTransaction()
                    .remove(searchFragment)
                    .commit();
    }

    public void removeProductFragment() {
        ProductsFragment productsFragment = implementation.getActivityProductsFragment();
        if (getChildFragmentManager().findFragmentByTag(Constants.FRAGMENT_PRODUCTS) != null)
            getChildFragmentManager()
                    .beginTransaction()
                    .remove(productsFragment)
                    .commit();
    }

    public void removeProductDescFragment() {
        if (getChildFragmentManager().findFragmentByTag(Constants.FRAGMENT_PRODUCT_DESCRIPTION) != null)
            if (descProductFragment != null) {
                getChildFragmentManager()
                        .beginTransaction()
                        .remove(descProductFragment)
                        .commit();
                implementation.setDescProductFragment(null);
            }
    }

    public void removeFragments() {
        removeSearchFragment();
        removeProductFragment();
        removeProductDescFragment();
    }

    public void popSearchFragment() {
        removeProductFragment();
        //getChildFragmentManager().popBackStack(Constants.FRAGMENT_SEARCH, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        Log.e(TAG, "BackStackCount : " + getChildFragmentManager().getBackStackEntryCount());
        getChildFragmentManager().popBackStackImmediate();
        Log.e(TAG, "BackStackCount 2: " + getChildFragmentManager().getBackStackEntryCount());
    }

    public void popProductsFragment() {
        onBackPressed();
        hasData = false;
        removeProductDescFragment();
        Log.e(TAG, "BackStackCount : " + getChildFragmentManager().getBackStackEntryCount());
        getChildFragmentManager().popBackStackImmediate();
        Log.e(TAG, "BackStackCount 2: " + getChildFragmentManager().getBackStackEntryCount());
       // getChildFragmentManager().popBackStack(Constants.FRAGMENT_PRODUCTS, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public void resetUserMarker() {
        userMarker = null;
        defaultMarker = null;
        currentLocation = null;
        addUserMarkerPosition(false);
    }

    public boolean hasData() {
        return (hasData || (showButton.getVisibility() == View.VISIBLE));
    }

    public void onSearchViewFocus() {
        final ProductsFragment productsFragment = implementation.getActivityProductsFragment();
        final SearchFragment searchFragment = implementation.getActivitySearchFragment();

        if (showButton.getVisibility() != View.GONE)
            showButton.setVisibility(View.GONE);
        if (btnWilaya != null) btnWilaya.setVisibility(View.GONE);
        if (btnVille != null) btnVille.setVisibility(View.GONE);
        if (btnSearchPerimeter != null) btnSearchPerimeter.setVisibility(View.GONE);
        if (sheetBehavior != null) sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        if (listViewSalesPoints != null) listViewSalesPoints.setVisibility(View.GONE);
        if (userLocation != null) userLocation.setVisibility(View.GONE);
        if (productsFragment.isAdded()) {
            getChildFragmentManager()
                    .beginTransaction()
                    .remove(productsFragment)
                    .commit();
        }
        if (!searchFragment.isVisible()) {
            Log.e(TAG, "Setting SearchFragment");
            getChildFragmentManager()
                    .beginTransaction()
                    .add(R.id.map_frame_layout, searchFragment, Constants.FRAGMENT_SEARCH)
                    .addToBackStack(null)
                    .commit();
            if (getChildFragmentManager().findFragmentByTag(Constants.FRAGMENT_SEARCH) != null)
                searchFragment.refreshHistory();
        }
    }

    public void submitSearchQuery(final String searchQuery) {
        final SearchFragment searchFragment = implementation.getActivitySearchFragment();
        if (getChildFragmentManager().findFragmentByTag(Constants.FRAGMENT_SEARCH) != null) {
            searchFragment.addToHistorySearches(searchQuery);
            searchFragment.refreshHistory();
            searchQuery(searchQuery);
        }
    }

    public void onProductMoreDetails(final String productBarcode) {
        final ProductsFragment productsFragment = implementation.getActivityProductsFragment();
        if (productsFragment.isVisible()) {
            descProductFragment = DescProductFragment.newInstance(productBarcode);
            implementation.setDescProductFragment(descProductFragment);
            getChildFragmentManager()
                    .beginTransaction()
                    .remove(productsFragment)
                    .add(R.id.map_frame_layout, descProductFragment, Constants.FRAGMENT_PRODUCT_DESCRIPTION)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private class GPSLocationListener implements android.location.LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "Location regular update using provider");
            if (location != null) {
                Log.e(TAG, "Location regular update using provider is not null : " + location.getLatitude() + "  " + location.getLongitude());
                latitudeSearchPosition = location.getLongitude();
                longitudeSearchPosition = location.getLatitude();

                if (currentLocation == null) currentLocation = location;
                if (location.getAccuracy() > currentLocation.getAccuracy()) {
                    Log.e(TAG, "Bad Accuracy, do nothing.");
                    return;
                }

                Log.e(TAG, "Good Accuracy.");
                currentLocation = location;

                userPosition = new LatLng(longitudeSearchPosition, latitudeSearchPosition);
                if (defaultMarker != null) defaultMarker.remove();
                if (addMarker && userMarker != null) {
                    if (userPosition != null) {
                        addMarker = false;
                        userMarker.setPosition(userPosition);
                    }
                }
                else {
                    if (userMarker != null) {
                        if (userPosition != null) {
                            userMarker.setPosition(userPosition);
                        }
                    } else {
                        userMakerOptions =  new MarkerOptions().
                                position(userPosition).
                                title(getResources().getString(R.string.your_position)).
                                icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue));

                        userMarker = mMap.addMarker(userMakerOptions);
                    }
                }
                SharedPreferences.Editor editor =
                        fragmentBelongActivity.getSharedPreferences(Constants.SHARED_PREFERENCES_POSITION,MODE_PRIVATE).edit();
                editor.putString(Constants.SHARED_PREFERENCES_POSITION_LATITUDE, ""+userPosition.latitude);
                editor.putString(Constants.SHARED_PREFERENCES_POSITION_LONGITUDE, ""+userPosition.longitude);
                editor.apply();
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            Log.e(TAG, "Provider Status Changed.");
        }

        @Override
        public void onProviderEnabled(String s) {
            Log.e(TAG, "Provider Enabled : " + s);
            addUserMarkerPosition(true);
        }

        @Override
        public void onProviderDisabled(String s) {
            Log.e(TAG, "Provider Disabled.");
        }
    }

    public interface MapActions {

        ProductsFragment getActivityProductsFragment();
        SearchFragment getActivitySearchFragment();
        FragmentMap getActivityFragmentMap();

        void iconifySearchView();
        void openSearchView();
        void checkGoToSearchFragment();
        void checkSearchProductBarcode();
        void setToolbarTitleForFragmentMap();
        void setDescProductFragment(final DescProductFragment descProductFragment);
    }
}