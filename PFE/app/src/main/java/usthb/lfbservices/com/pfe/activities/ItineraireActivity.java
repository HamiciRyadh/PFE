package usthb.lfbservices.com.pfe.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import usthb.lfbservices.com.pfe.itinerary.autocomplete.GoogleAutocompleteResponse;
import usthb.lfbservices.com.pfe.itinerary.autocomplete.Prediction;
import usthb.lfbservices.com.pfe.itinerary.direction.GoogleDirections;
import usthb.lfbservices.com.pfe.itinerary.place.GooglePlaceDetails;
import usthb.lfbservices.com.pfe.itinerary.place.Location;
import usthb.lfbservices.com.pfe.R;
import usthb.lfbservices.com.pfe.models.SalesPoint;
import usthb.lfbservices.com.pfe.models.Singleton;
import usthb.lfbservices.com.pfe.network.ItineraireService;
import usthb.lfbservices.com.pfe.utils.Constantes;
import usthb.lfbservices.com.pfe.utils.Utils;

public class ItineraireActivity extends FragmentActivity implements OnMapReadyCallback  {

    private static final String TAG = ItineraireActivity.class.getName();
    public static final float ZOOM_LEVEL = 18.23f;

    private AutoCompleteTextView depart;
    private AutoCompleteTextView arrivee;
    private LinearLayout layoutDriving;
    private LinearLayout layoutWalking;
    private GoogleMap mMap;
    private LatLng origin;
    private LatLng dest;
    private LatLng userPosition;
    private LatLng defaultPosition = new LatLng(36.7525, 3.04197);
    private TextView ShowDistanceDurationDriving;
    private RelativeLayout blockItineraire;
    private FloatingActionButton userLocation;
    private TextView ShowDistanceDurationWalking;
    private Polyline line;
    private ItineraireService service;
    private String apiKey;
    private Marker originMarker;
    private Marker destMarker;
    private List<String> positions = new ArrayList<>();
    private List<Prediction> predictions = new ArrayList<>();
    private List<SalesPoint> salesPointList;
    private FusedLocationProviderClient mFusedLocationClient;
    private SalesPoint salesPointTemps;
    private CameraUpdate cameraUpdate;
    private boolean gpsPermissionRequested = false;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itineraire);

        if (!isGooglePlayServicesAvailable()) {
            Log.d("onCreate", "Google Play Services not available. Ending Test case.");
            finish();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_itineraire);
        mapFragment.getMapAsync(this);

        initVariables();
        build_retrofit();
        initDepart();
        initArrivee();
        initUserLocation();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        originMarker = mMap.addMarker(new MarkerOptions()
                .position(defaultPosition)
                .title(getResources().getString(R.string.start_point))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
        origin = defaultPosition;

        SharedPreferences preferences = getSharedPreferences(Constantes.SHARED_PREFERENCES_POSITION, MODE_PRIVATE);
        String sUserLatitude = preferences.getString(Constantes.SHARED_PREFERENCES_POSITION_LATITUDE, null);
        String sUserLongitude = preferences.getString(Constantes.SHARED_PREFERENCES_POSITION_LONGITUDE, null);
        LatLng userPosition = null;

        if (sUserLatitude != null && sUserLongitude != null) {
            try {
                userPosition = new LatLng(Double.parseDouble(sUserLatitude), Double.parseDouble(sUserLongitude));
            } catch (Exception e) {
                Log.e(TAG, "Exception creating user position : " + e);
            }
        }

        String salesPointID = getIntent().getStringExtra(Constantes.INTENT_SALES_POINT_ID);
        if (salesPointID != null) {
            for (SalesPoint salesPoint : salesPointList) {
                if (salesPoint.getSalesPointId().equals(salesPointID)) {
                    salesPointTemps = salesPoint;
                    dest = new LatLng(salesPoint.getSalesPointLat(), salesPoint.getSalesPointLong());
                    arrivee.setText(salesPoint.getSalesPointAddress());
                    break;
                }
            }
        }

        if (dest != null ) {
        destMarker = mMap.addMarker(new MarkerOptions()
                .position(dest)
                .title(getResources().getString(R.string.destination_point))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
        }

        if(userPosition != null ) {
            depart.setText(getResources().getString(R.string.your_position));
            origin = userPosition;
            originMarker.setPosition(origin);
        }
        get_distance(origin, dest);
        layoutDriving.performClick();

        if (!Utils.isGPSActivated(this)) {
            if (userPosition != null) {
                Toast.makeText(this, getResources().getString(R.string.no_gps_use_last_position), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, getResources().getString(R.string.no_gps_default_position), Toast.LENGTH_LONG).show();
            }
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Utils.hideKeyboard(ItineraireActivity.this);
                if (blockItineraire.getVisibility() == View.VISIBLE) {
                    blockItineraire.animate()
                            .translationY(0)
                            .alpha(1.0f)
                            .setDuration(300)
                            .setListener(new AnimatorListenerAdapter() {
                                       @Override
                                           public void onAnimationEnd(Animator animation) {
                                           super.onAnimationEnd(animation);
                                           blockItineraire.setVisibility(View.GONE);
                                       }
                            });
                } else {
                    blockItineraire.animate()
                            .translationY(0)
                            .alpha(1.0f)
                            .setDuration(300)
                            .setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            super.onAnimationEnd(animation);
                                            blockItineraire.setVisibility(View.VISIBLE);
                                        }
                            });
                }
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.e(TAG, "onMarkerClick");
                marker.showInfoWindow();
                if (mMap.getCameraPosition().zoom > ItineraireActivity.ZOOM_LEVEL*0.8f) {
                    mMap.animateCamera(cameraUpdate);
                }
                else {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), ItineraireActivity.ZOOM_LEVEL));
                }
                return true;
            }
        });
    }


    @Override
    protected void onResume() {
        Log.e(TAG, "onResume");
        Log.e(TAG, "gpsPermissionRequest : " + gpsPermissionRequested);
        super.onResume();
        if (gpsPermissionRequested) {
            Log.e(TAG, "onResume, gpsPermissionRequested = true");
            gpsPermissionRequested = false;
            userLocation.callOnClick();
        }
    }


    public void initVariables(){
        apiKey = getResources().getString(R.string.google_maps_key);
        depart =  findViewById(R.id.editDepart);
        arrivee =  findViewById(R.id.editArrivee);
        ShowDistanceDurationDriving =  findViewById(R.id.show_distance_time_driving);
        ShowDistanceDurationWalking =  findViewById(R.id.show_distance_time_walking);
        layoutDriving = findViewById(R.id.layout_driving);
        layoutDriving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick : Driving");
                get_response(Constantes.ITINERAIRE_DRIVING, origin, dest);
            }
        });
        layoutWalking = findViewById(R.id.layout_walking);
        layoutWalking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick : Walking");
                get_response(Constantes.ITINERAIRE_WALKING, origin, dest);
            }
        });
        blockItineraire = findViewById(R.id.block_itineraire);
        userLocation = findViewById(R.id.btn_location);
        salesPointList = Singleton.getInstance().getSalesPointList();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        for( SalesPoint salesPoint : salesPointList) {
            adapter.add(salesPoint.getSalesPointName());
        }
        arrivee.setAdapter(adapter);
    }


    public void initDepart() {
        depart.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                get_places(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        depart.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                Utils.hideKeyboard(ItineraireActivity.this);
                Log.e(TAG, "onClick : Depart");
                get_latlng(apiKey, predictions.get(position).getPlaceID());
            }
        });

        depart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                depart.setText("");
            }
        });

        depart.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    depart.setText("");
                } else {
                    if (origin.equals(userPosition)) {
                        depart.setText(getResources().getString(R.string.your_position));
                    }
                }
            }
        });
    }


    public void initArrivee() {
        arrivee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        arrivee.showDropDown();
                    }
                }, 500);
                arrivee.setText("");
            }
        });

        arrivee.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                SalesPoint salesPointSelected = salesPointList.get(position);
                arrivee.setText(salesPointSelected.getSalesPointAddress());
                dest = new LatLng(salesPointSelected.getSalesPointLat(),salesPointSelected.getSalesPointLong());
                Utils.hideKeyboard(ItineraireActivity.this);
                Log.e(TAG, "onClick : Arrivee");
                get_distance(origin, dest);
                get_response(Constantes.ITINERAIRE_DRIVING,origin,dest);
            }
        });

        arrivee.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    arrivee.setText("");
                } else {
                    if (salesPointTemps != null) {
                        if (dest.equals(salesPointTemps.getSalesPointLatLng())) {
                            arrivee.setText(salesPointTemps.getSalesPointAddress());
                        }
                    }
                }
            }
        });
    }


    public void initUserLocation() {
        //TODO: If we don't stop MapsActivity, we can still retrieve new data from there, or use the same code here
        userLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "UserLocationClick");
                Log.e(TAG, "onClick : UserLocation");
                if (!Utils.checkPermission(ItineraireActivity.this)) {
                    Log.e(TAG, "onMapReady : No GPS Permissions.");
                    Utils.requestGPSPermissions(ItineraireActivity.this);
                }
                if (Utils.checkPermission(ItineraireActivity.this)) {
                    Log.e(TAG, "onMapReady : GPS Permissions Ok.");
                    if (Utils.isGPSActivated(ItineraireActivity.this)) {
                        Log.e(TAG, "onMapReady : GPS Activated.");
                        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(ItineraireActivity.this);
                        mFusedLocationClient.getLastLocation()
                                .addOnSuccessListener(ItineraireActivity.this, new OnSuccessListener<android.location.Location>() {
                                    @Override
                                    public void onSuccess(android.location.Location location) {
                                        depart.setText(getResources().getString(R.string.your_position));
                                        if (location != null) {
                                            userPosition =new LatLng(location.getLatitude(),location.getLongitude());

                                            SharedPreferences.Editor editor = getSharedPreferences(Constantes.SHARED_PREFERENCES_POSITION, MODE_PRIVATE).edit();
                                            editor.putString(Constantes.SHARED_PREFERENCES_POSITION_LATITUDE, ""+userPosition.latitude);
                                            editor.putString(Constantes.SHARED_PREFERENCES_POSITION_LONGITUDE, ""+userPosition.longitude);
                                            editor.apply();

                                            origin = userPosition;
                                            get_distance(userPosition, dest);
                                            get_response(Constantes.ITINERAIRE_DRIVING, userPosition, dest);
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG, "Location update failure :  " + e);
                                    }
                                });
                    } else {
                        Log.e(TAG, "onMapReady : GPS Non Activated.");
                        gpsPermissionRequested = true;
                        Utils.activateGPS(ItineraireActivity.this);
                    }
                }
            }
        });
    }


    private void get_distance(LatLng origin, LatLng dest) {

        Call<GoogleDirections> call = service.getDistanceDuration(apiKey,"metric", origin.latitude + "," + origin.longitude,dest.latitude + "," + dest.longitude,Constantes.ITINERAIRE_WALKING);
        call.enqueue(new Callback<GoogleDirections>() {

            @Override
            public void onResponse(Call<GoogleDirections> call, Response<GoogleDirections> response) {
                //TODO: Separate the 2 TextViews and update them separately for a better visual
                Log.d("onResponse get_distance", response.toString());
                for (int i = 0; i < response.body().getRoutes().size(); i++) {
                    String distance = response.body().getRoutes().get(i).getLegs().get(i).getDistance().getText();
                    String time = response.body().getRoutes().get(i).getLegs().get(i).getDuration().getText();
                    time = time.replaceAll("hours","h");
                    time =  time.replaceAll("hour","h");
                    String timeDistance = time  + " \n (" + distance + ")";
                    ShowDistanceDurationWalking.setText(timeDistance);}
            }

            @Override
            public void onFailure(Call<GoogleDirections> call, Throwable t) {
                Log.d("onFailure", t.toString());
            }
        });
    }


    private void build_retrofit() {
        String url = "https://maps.googleapis.com/maps/api/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(ItineraireService.class);
    }


    private  void get_places(String searchTerm){
        Call<GoogleAutocompleteResponse> call = service.getAutoCompleteSearchResults(apiKey,searchTerm ,36.7525 +","+ 3.04197,500);
        call.enqueue(new Callback<GoogleAutocompleteResponse>() {
            ArrayAdapter<String> adapterPosition;

            @Override
            public void onResponse(Call<GoogleAutocompleteResponse> call, Response<GoogleAutocompleteResponse> response) {
                Log.d("onResponse get_places", response.toString());
                GoogleAutocompleteResponse places = response.body();

                predictions.clear();
                predictions.addAll(places.getPredictionList());
                positions.clear();
                for (Prediction p : predictions){
                    positions.add(p.getDescription());
                }

                adapterPosition = new ArrayAdapter<>(ItineraireActivity.this, android.R.layout.simple_list_item_1,positions);
                depart.setAdapter(adapterPosition);
            }

            @Override
            public void onFailure(Call<GoogleAutocompleteResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


    private void get_latlng(String api_key, String id_place){

        Call<GooglePlaceDetails> call = service.getLatLng(api_key,id_place);
        call.enqueue(new Callback<GooglePlaceDetails>() {

            @Override
            public void onResponse(Call<GooglePlaceDetails> call, Response<GooglePlaceDetails> response) {
                Log.d("onResponse get_latlng", response.toString());
                Location places = response.body().getResult().getGeometry().getLocation();
                origin = new LatLng(places.getLat(), places.getLng());
                get_distance(origin, dest);
                get_response(Constantes.ITINERAIRE_DRIVING, origin, dest);
            }

            @Override
            public void onFailure(Call<GooglePlaceDetails> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


    private void get_response(final String type, final LatLng origin, final LatLng dest) {
        final List<LatLng> listLatLng = new ArrayList<>();
        listLatLng.add(origin);
        listLatLng.add(dest);

        Call<GoogleDirections> call = service.getDistanceDuration(apiKey,"metric", origin.latitude + "," + origin.longitude,dest.latitude + "," + dest.longitude, type);
        call.enqueue(new Callback<GoogleDirections>() {
            @Override
            public void onResponse(Call<GoogleDirections> call, Response<GoogleDirections> response) {
                Log.d("onResponse get_response", response.toString());
                try {
                    if (line != null) {
                        line.remove();
                    }
                    String distance = response.body().getRoutes().get(0).getLegs().get(0).getDistance().getText();
                    String time = response.body().getRoutes().get(0).getLegs().get(0).getDuration().getText();
                    time = time.replaceAll("hours","h");
                    time =  time.replaceAll("hour","h");
                    String timeDistance = time  + " \n (" + distance + ")";

                    if (type.equals(Constantes.ITINERAIRE_WALKING)){
                        ShowDistanceDurationWalking.setText(timeDistance);
                    } else {
                        ShowDistanceDurationDriving.setText(timeDistance);
                    }

                    String encodedString = response.body().getRoutes().get(0).getOverviewPolyline().getPoints();
                    List<LatLng> list = decodePoly(encodedString);
                    line = mMap.addPolyline(new PolylineOptions()
                            .addAll(list)
                            .width(20)
                            .color(getResources().getColor(R.color.colorPrimary))
                            .geodesic(true)
                    );
                    originMarker.setPosition(origin);
                    destMarker.setPosition(dest);
                    zoomRoute(mMap, listLatLng);
                }
                catch (Exception e) {
                    Log.d("onResponse", "There is an error");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<GoogleDirections> call, Throwable t) {
                   Log.d("onFailure", t.toString());
            }
        });
    }


    public void zoomRoute(GoogleMap googleMap, List<LatLng> listLatLngRoute) {

        if (googleMap == null || listLatLngRoute == null || listLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : listLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 200;
        LatLngBounds latLngBounds = boundsBuilder.build();

        cameraUpdate = CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding);
        googleMap.animateCamera(cameraUpdate);
    }


    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            }

            while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);
        }

        return poly;
    }


    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,0).show();
            }
            return false;
        }
        return true;
    }
}