package usthb.lfbservices.com.pfe.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


/**
 * Created by ryadh on 31/01/18.
 */

public class Utils
{

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
