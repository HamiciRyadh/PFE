package usthb.lfbservices.com.pfe.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

import usthb.lfbservices.com.pfe.R;


/**
 * Created by ryadh on 31/01/18.
 * List of public and static constants and methods
 */

public class Utils
{

    /**
     * Name of the file in which the history searches will be saved
     */
    public static final String HISTORY_FILE_NAME = "History.dat";
    /**
     * TAG for the fragment "SearchFragment" which is used to identify it
     */
    public static final String FRAGMENT_SEARCH = "FRAGMENT_SEARCH";
    /**
     * TAG for the fragment "ProductsFragment" which is used to identify it
     */
    public static final String FRAGMENT_PRODUCTS = "FRAGMENT_PRODUCTS";
    /**
     * Base url for retrieving photos from google.
     */
    public static final String GOOGLE_PHOTO_BASE_URL = "https://maps.googleapis.com/maps/api/place/photo?";







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
        return Uri.parse(GOOGLE_PHOTO_BASE_URL
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
}
