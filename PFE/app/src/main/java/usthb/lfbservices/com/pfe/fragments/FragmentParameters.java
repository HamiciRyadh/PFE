package usthb.lfbservices.com.pfe.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import usthb.lfbservices.com.pfe.R;
import usthb.lfbservices.com.pfe.itinerary.autocomplete.GoogleAutocompleteResponse;
import usthb.lfbservices.com.pfe.itinerary.autocomplete.Prediction;
import usthb.lfbservices.com.pfe.itinerary.place.GooglePlaceDetails;
import usthb.lfbservices.com.pfe.itinerary.place.Location;
import usthb.lfbservices.com.pfe.models.Singleton;
import usthb.lfbservices.com.pfe.network.PfeAPI;
import usthb.lfbservices.com.pfe.utils.Constants;
import usthb.lfbservices.com.pfe.utils.Utils;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by ryadh on 06/05/18.
 */

public class FragmentParameters extends Fragment {

    private static final String TAG = "FragmentParameters";

    private ParametersActions implementation;
    private View rootView;
    private FragmentActivity fragmentBelongActivity;
    private TextView history;
    private  Switch notification;
    private AutoCompleteTextView userPosition;
    private String apiKey;
    private List<Prediction> predictions ;
    private PfeAPI pfeAPI;
    private List<String> positions;
    private FusedLocationProviderClient mFusedLocationClient;
    private RadioGroup radioStyleMapGroup;
    private ImageView location;


    public FragmentParameters() {
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");
        rootView = inflater.inflate(R.layout.fragment_parameters, container, false);
        fragmentBelongActivity = getActivity();
        if (rootView != null) {
            initVariables();
            initPositionParameter();
            init();
        }

        implementation.setToolbarTitleForFragmentParameters();

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        Log.e(TAG, "onAttach");
        super.onAttach(context);

        try {
            implementation = (ParametersActions) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement ParametersActions");
        }
    }

    @Override
    public void onDetach() {
        Log.e(TAG, "OnDetach");
        super.onDetach();
        implementation = null;
    }

    public void initVariables() {
        apiKey = getResources().getString(R.string.google_maps_key);
        predictions = new ArrayList<>();
        positions = new ArrayList<>();
        pfeAPI = PfeAPI.getInstance();
        notification= rootView.findViewById(R.id.switch_notification);
        userPosition =rootView.findViewById(R.id.position_parameter);
        history=rootView.findViewById(R.id.history_parameter);
        location = rootView.findViewById(R.id.geoloc_parameter);
        radioStyleMapGroup = rootView.findViewById(R.id.radio_btn_map);


    }

    public void init() {
        SharedPreferences sharedPreferences = fragmentBelongActivity.getSharedPreferences(Constants.SHARED_PREFERENCES_POSITION, MODE_PRIVATE);
        String sMapStyle = sharedPreferences.getString(Constants.SHARED_PREFERENCES_USER_MAP_STYLE, null);
        if (sMapStyle != null) {
            if (sMapStyle.equalsIgnoreCase(Constants.SATELLITE)) {
                radioStyleMapGroup.check(R.id.radio_satellite_map);
            } else if (sMapStyle.equalsIgnoreCase(Constants.STANDARD)) {
                radioStyleMapGroup.check(R.id.radio_standard_map);
            }
        }

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                mBuilder.setMessage(R.string.dialog_history);
                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        File file = new File(fragmentBelongActivity.getFilesDir(), Constants.HISTORY_FILE_NAME);
                        try {
                            FileOutputStream fos = new FileOutputStream(file);
                            fos.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                mBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });

        notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (! isChecked) {
                    notification.setText(getResources().getString(R.string.turn_on_notifications));
                    try {
                        FirebaseInstanceId.getInstance().deleteInstanceId();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    notification.setText(getResources().getString(R.string.turn_off_notifications));
                    FirebaseInstanceId.getInstance().getToken();
                }
            }

        });

        radioStyleMapGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = group.findViewById(checkedId);
                final GoogleMap mMap = Singleton.getInstance().getMap();
                boolean isChecked = checkedRadioButton.isChecked();
                if (isChecked) {
                    SharedPreferences.Editor editor = fragmentBelongActivity.getSharedPreferences(Constants.SHARED_PREFERENCES_POSITION, MODE_PRIVATE).edit();
                    if(checkedRadioButton.getText().equals("Satellite")) {
                        Log.e(TAG, "Satellite Checked ");
                        editor.putString(Constants.SHARED_PREFERENCES_USER_MAP_STYLE, Constants.SATELLITE);
                        if (mMap != null) mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    }
                    if(checkedRadioButton.getText().equals("Standard")) {
                        Log.e(TAG, "Standard Checked ");
                        editor.putString(Constants.SHARED_PREFERENCES_USER_MAP_STYLE, Constants.STANDARD);
                        if (mMap != null) mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    }
                    editor.apply();
                }
            }
        });
    }

    public void initPositionParameter() {
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isNetworkAvailable(fragmentBelongActivity)) {
                    Log.e(TAG, "UserLocationClick");
                    Log.e(TAG, "onClick : UserLocation");
                    if (!Utils.checkGPSPermission(getActivity())) {
                        Log.e(TAG, " No GPS Permissions.");
                        Utils.requestGPSPermission(getActivity());
                    }
                    if (Utils.checkGPSPermission(getActivity())) {
                        Log.e(TAG, "  GPS Permissions Ok.");
                        if (Utils.isGPSActivated(getActivity())) {
                            Log.e(TAG, "  GPS Activated.");
                            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
                            mFusedLocationClient.getLastLocation()
                                    .addOnSuccessListener(getActivity(), new OnSuccessListener<android.location.Location>() {
                                        @Override
                                        public void onSuccess(android.location.Location location) {
                                            userPosition.setText(getResources().getString(R.string.your_position));
                                            if (location != null) {
                                                LatLng userLatLng =new LatLng(location.getLatitude(),location.getLongitude());

                                                SharedPreferences.Editor editor = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_POSITION, MODE_PRIVATE).edit();
                                                editor.putString(Constants.SHARED_PREFERENCES_POSITION_LATITUDE, ""+userLatLng.latitude);
                                                editor.putString(Constants.SHARED_PREFERENCES_POSITION_LONGITUDE, ""+userLatLng.longitude);
                                                editor.apply();


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
                            Log.e(TAG, " GPS Non Activated.");
                            Utils.activateGPS(getActivity());
                        }
                    } else {
                        Toast.makeText(fragmentBelongActivity, getString(R.string.no_internet), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });



        userPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Utils.isNetworkAvailable(fragmentBelongActivity)) {

                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            userPosition.showDropDown();
                        }
                    }, 500);
                    userPosition.setText("");
                } else {
                    Toast.makeText(fragmentBelongActivity, getString(R.string.no_internet), Toast.LENGTH_LONG).show();
                }
            }
        });

        userPosition.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                displaySuggestions(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        userPosition.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                Log.e(TAG, "onClick : userPosition");
                get_latlng(apiKey, predictions.get(position).getPlaceID());
            }
        });
    }



    private  void displaySuggestions(String searchTerm) {
        Call<GoogleAutocompleteResponse> call = pfeAPI.getAutoCompleteSearchResults(apiKey, searchTerm, 36.7525 + "," + 3.04197, 500);
        call.enqueue(new Callback<GoogleAutocompleteResponse>() {
            ArrayAdapter<String> adapterPosition;

            @Override
            public void onResponse(Call<GoogleAutocompleteResponse> call, Response<GoogleAutocompleteResponse> response) {
                Log.d("onResponse get_places", response.toString());
                GoogleAutocompleteResponse places = response.body();

                predictions.clear();
                predictions.addAll(places.getPredictionList());
                positions.clear();
                for (Prediction p : predictions) {
                    positions.add(p.getDescription());
                }

                adapterPosition = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, positions);
                userPosition.setAdapter(adapterPosition);
            }

            @Override
            public void onFailure(Call<GoogleAutocompleteResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


    private void get_latlng(String api_key, String id_place){

        Call<GooglePlaceDetails> call = pfeAPI.getLatLng(api_key,id_place);
        call.enqueue(new Callback<GooglePlaceDetails>() {

            @Override
            public void onResponse(Call<GooglePlaceDetails> call, Response<GooglePlaceDetails> response) {
                Log.d("onResponse get_latlng", response.toString());
                Location places = response.body().getResult().getGeometry().getLocation();
                LatLng userLatLng = new LatLng(places.getLat(), places.getLng());

                SharedPreferences.Editor editor = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_POSITION, MODE_PRIVATE).edit();
                editor.putString(Constants.SHARED_PREFERENCES_POSITION_LATITUDE, ""+userLatLng.latitude);
                editor.putString(Constants.SHARED_PREFERENCES_POSITION_LONGITUDE, ""+userLatLng.longitude);
                editor.apply();
            }

            @Override
            public void onFailure(Call<GooglePlaceDetails> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public interface ParametersActions {

        void setToolbarTitleForFragmentParameters();
    }
}