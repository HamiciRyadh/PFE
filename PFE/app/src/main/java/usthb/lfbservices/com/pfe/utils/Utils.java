package usthb.lfbservices.com.pfe.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import usthb.lfbservices.com.pfe.R;
import usthb.lfbservices.com.pfe.activities.LoginActivity;
import usthb.lfbservices.com.pfe.roomDatabase.AppRoomDatabase;

import static android.content.Context.MODE_PRIVATE;

/**
 * Contains a list of static methods of general purpose.
 */

public class Utils
{
    private static final String TAG = "Utils";
    /**
     * Checks whether the network is available or not.
     * @param context The current context of the application, which is neccessary to display
     *                the Toast message.
     * @return true if network is available, false otherwise.
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

    /**
     * Hides the virtual keyboard.
     * @param context The Context of the activity in which the keyboard will be hidden.
     */
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

    /**
     * Shows the virtual keyboard.
     * @param context The Context of the activity in which the keyboard will be shown.
     */
    public static void showKeyboard(Context context) {
        ((InputMethodManager) (context).getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * Builds a {@link Uri} to the associated Google photo reference.
     * @param context The Context of the current Activity, will be used to get the device width.
     * @param photoReference A reference to a Google photo.
     * @return The {@link Uri} associated with the Google photo reference.
     */
    public static Uri buildGooglePictureUri(final Context context, final String photoReference) {
        return Uri.parse(Constants.GOOGLE_PHOTO_BASE_URL
                + "maxwidth=" + getDeviceWidthInPixel(context)
                + "&photoreference=" + photoReference
                + "&key="+context.getResources().getString(R.string.google_maps_key));
    }

    /**
     * Gets the device width in pixels.
     * @param context The Context of the current activity.
     * @return The device width in pixels.
     */
    public static int getDeviceWidthInPixel(final Context context) {
        Log.e(TAG, "" + context.getResources().getDisplayMetrics().widthPixels);
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * Gets the device height in pixels.
     * @param context The Context of the current activity.
     * @return The device height in pixels.
     */
    public static int getDeviceHeightInPixel(final Context context) {
        Log.e(TAG, "" + context.getResources().getDisplayMetrics().heightPixels);
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * Requests the GPS permissions @link Manifest.permission#ACCESS_FINE_LOCATION}, {@link Manifest.permission#ACCESS_COARSE_LOCATION}.
     * @param activity The current Activity.
     */
    public static void requestGPSPermission(final Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                Constants.REQUEST_GPS_PERMISSION);
    }

    /**
     * Checks if the GPS permissions {@link Manifest.permission#ACCESS_FINE_LOCATION}, {@link Manifest.permission#ACCESS_COARSE_LOCATION}
     * are granted (for API >= 23).
     * @param activity The current Activity.
     * @return true if the permissions are granted, false otherwise.
     */
    public static boolean checkGPSPermission(final Activity activity) {
        return (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        );
    }

    /**
     * Requests the Camera permission {@link Manifest.permission#CAMERA}.
     * @param activity The current Activity.
     */
    public static void requestCameraPermission(final Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, Constants.REQUEST_CAMERA_PERMISSION);
    }

    /**
     * Checks if the Camera permission {@link Manifest.permission#CAMERA} is granted.
     * @param activity The current Activity.
     * @return true if the permission is granted, false otherwise.
     */
    public static boolean checkCameraPermission(final Activity activity) {
        return (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * Checks if the device's GPS is currently activated.
     * @param context The Context of the current Activity.
     * @return true if the GPS is activated, false otherwise.
     */
    public static boolean isGPSActivated(final Context context) {
        LocationManager service = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        return service.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * Checks if the GPS is activated using {@link Utils#isGPSActivated(Context)}, if it is , does nothing,
     * if it is not, shows an {@link AlertDialog} that can send an {@link Intent} to the {@link Settings#ACTION_LOCATION_SOURCE_SETTINGS}
     * so that the user can activate the GPS.
     * @param context The Context of the current Activity.
     */
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

    /**
     * Checks if the user is logged in.
     * @param context The Context of the current Activity.
     * @return true if the user is logged in, false otherwise.
     */
    public static boolean isUserConnected(final Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_USER, MODE_PRIVATE);
        String mailAddress = preferences.getString(Constants.SHARED_PREFERENCES_USER_EMAIL, null);
        String password = preferences.getString(Constants.SHARED_PREFERENCES_USER_PASSWORD, null);
        return  (mailAddress != null && password != null);
    }

    /**
     * Retrieves the Firebase token id of the device that is a unique id that can be used to send
     * messages and notifications to the device.
     * @param context The Context of the current Activity.
     * @return A {@link String} representing the Firebase token id associated with the device.
     */
    public static String getStoredFirebaseTokenId(final Context context) {
        SharedPreferences preferences =context.getSharedPreferences(Constants.SHARED_PREFERENCES_USER, MODE_PRIVATE);
        return preferences.getString(Constants.SHARED_PREFERENCES_FIREBASE_TOKEN_ID, null);
    }

    /**
     * Shows an {@link AlertDialog} that can be cancelled or redirect the user to the log in Activity.
     * @param context The Context of the current Activity.
     */
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

    /**
     * Performs a network operation to get the Google Image associated with the photo reference and insert it
     * into the database for the corresponding sales point.
     * @param context The Context of the current Activity.
     * @param salesPointId The id of the {@link usthb.lfbservices.com.pfe.models.SalesPoint} for which the
     *                     photo will be inserted.
     * @param photoReference The reference for the photo to download.
     */
    public static void addPhoto(final Context context, final String salesPointId, final String photoReference) {
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

            try {
                URL url = new URL(Utils.buildGooglePictureUri(context, photoReference).toString());
                Bitmap bitmap= BitmapFactory.decodeStream(url.openConnection().getInputStream());
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                bitmap.recycle();
                AppRoomDatabase db = AppRoomDatabase.getInstance(context);
                db.salesPointDao().updatePhoto(salesPointId, byteArray);

            } catch(IOException e) {
                Log.e(TAG, "image " +e );
            }
        }
    }

    /**
     * Checks if the given position inside the given perimeter.
     * @param center The position of the center of the perimeter.
     * @param position The position to test.
     * @param radius The radius of the perimeter.
     * @return true if position is inside the perimeter defined by the given center and radius,
     * false otherwise.
     */
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
        List<LatLng> result = new ArrayList<>();
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
