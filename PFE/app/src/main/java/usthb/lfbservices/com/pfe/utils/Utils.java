package usthb.lfbservices.com.pfe.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;


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
     * Checks whether the network is available or not
     * @param context The current context of the application, which is neccessary to display
     *                the Toast message
     * @return true if network is available, false otherwise
     */
    public static boolean isNetworkAvailable(final Context context) {
        boolean networkavailable = false;

        try {
            ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

            if (connectivityManager != null && activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting()) {
                networkavailable = true;
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            return networkavailable;
        }
    }

}
