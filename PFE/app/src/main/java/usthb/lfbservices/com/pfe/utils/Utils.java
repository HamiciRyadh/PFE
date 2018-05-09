package usthb.lfbservices.com.pfe.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import usthb.lfbservices.com.pfe.R;
import usthb.lfbservices.com.pfe.activities.LoginActivity;
import usthb.lfbservices.com.pfe.network.PfeAPI;

import static android.content.Context.MODE_PRIVATE;

/**
 * List of public and static constants and methods
 */

public class Utils
{
    /**
     * Checks whether the network is available or not
     * @param context The current context of the application, which is neccessary to display
     *                the Toast message
     * @return true if network is available, false otherwise
     */
    public static boolean isNetworkAvailable(final Context context) {
        boolean networkAvailable = false;

        try {
            ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

            if (connectivityManager != null && activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting()) {
                networkAvailable = true;
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            return networkAvailable;
        }
    }

    public static void hideKeyboard(final Context context) {
    try {
        ((Activity) context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if ((((Activity) context).getCurrentFocus() != null) && (((Activity) context).getCurrentFocus().getWindowToken() != null)) {
            ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), 0);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}

    public static void showKeyboard(Context context) {
        ((InputMethodManager) (context).getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public static Uri buildGooglePictureUri(final Context context, final String photoReference) {
        return Uri.parse(Constantes.GOOGLE_PHOTO_BASE_URL
                + "maxwidth=" + getDeviceWidthInPixel(context)
                + "&photoreference=" + photoReference
                + "&key="+context.getResources().getString(R.string.google_maps_key));
    }

    public static int getDeviceWidthInPixel(final Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getDeviceHeightInPixel(final Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static void requestGPSPermissions(final Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                123);
    }

    public static boolean checkPermission(final Activity activity) {
        return (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        );
    }

    public static boolean isGPSActivated(final Context context) {
        LocationManager service = (LocationManager)context.getSystemService(context.LOCATION_SERVICE);
        return service.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static void activateGPS(final Context context) {
        if (!isGPSActivated(context)) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
            mBuilder.setMessage(R.string.dialog_message_activate_gps);

            mBuilder.setCancelable(false);
            mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(intent);
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
    }

    public static boolean isUserConnected(final Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Constantes.SHARED_PREFERENCES_USER, MODE_PRIVATE);
        String mailAddress = preferences.getString(Constantes.SHARED_PREFERENCES_USER_EMAIL, null);
        String password = preferences.getString(Constantes.SHARED_PREFERENCES_USER_PASSWORD, null);
        return  (mailAddress != null && password != null);
    }

    public static void showConnectDialog(final Context context) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        mBuilder.setMessage(R.string.dialog_message_require_connexion);

        mBuilder.setCancelable(false);
        mBuilder.setPositiveButton(R.string.go_to_connect_dialog_option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);
            }
        });
        mBuilder.setNegativeButton(R.string.dismiss_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    public static boolean isInsidePerimeter(LatLng center, LatLng position, double radius) {
        float[] distance = new float[2];

        Location.distanceBetween( position.latitude, position.longitude,
                center.latitude, center.longitude, distance);

        return distance[0] < radius;
    }

    /**
     * Returns a List containing up to the n closest positions to another position called center, if center
     * is null, returns up to the n closest positions.
     * @param positions The list of positions.
     * @param center The center of the result.
     * @param n The number of positions to keep.
     * @return A List containing up to the n closest positions to the center.
     */
    public static List<LatLng> getClosestPositions(Iterator<LatLng> positions, LatLng center, int n) {
        List<LatLng> result = new ArrayList<LatLng>();
        float[] distanceFutureElement;
        float[][] distances = new float[n][2];
        int count = 0;

        if (positions.hasNext()) {
            if (center == null) {
                center = positions.next();
            }

            while (positions.hasNext()) {
                LatLng latLng = positions.next();

                if (result.size() < n) {
                    result.add(latLng);
                    Location.distanceBetween(latLng.latitude, latLng.longitude,
                            center.latitude, center.longitude, distances[count]);
                    count++;
                } else {
                    distanceFutureElement = new float[2];
                    Location.distanceBetween(latLng.latitude, latLng.longitude,
                            center.latitude, center.longitude, distanceFutureElement);

                    float distanceMax = distances[0][0];
                    int positionMax = 0;
                    for (int i = 1; i < n; i++) {
                        if (distances[i][0] > distanceMax) {
                            distanceMax = distances[i][0];
                            positionMax = i;
                        }
                    }

                    result.remove(positionMax);
                    result.add(latLng);
                    distances[positionMax] = distanceFutureElement;
                }
            }
        }

        result.add(center);

        for (int i = 0; i < result.size(); i++)
            Log.e("Utils", result.get(i).latitude + "     " + result.get(i).longitude);
        return result;
    }
}
