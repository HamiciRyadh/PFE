package usthb.lfbservices.com.pfe.models;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import usthb.lfbservices.com.pfe.network.PfeRx;
import usthb.lfbservices.com.pfe.utils.Constantes;

/**
 * Created by ryadh on 10/05/18.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "Refreshed token: " + refreshedToken);
        sendRegistrationToServer(refreshedToken);
        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences(Constantes.SHARED_PREFERENCES_USER, MODE_PRIVATE).edit();
        editor.remove(Constantes.SHARED_PREFERENCES_FIREBASE_TOKEN_ID);
        editor.putString(Constantes.SHARED_PREFERENCES_FIREBASE_TOKEN_ID, refreshedToken);
        editor.apply();
    }

    /**
     * Persist token to third-party servers.
     * @param newToken The new token.
     */
    private void sendRegistrationToServer(final String newToken) {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences(Constantes.SHARED_PREFERENCES_USER, MODE_PRIVATE);
        String mailAddress = preferences.getString(Constantes.SHARED_PREFERENCES_USER_EMAIL, null);
        String password = preferences.getString(Constantes.SHARED_PREFERENCES_USER_PASSWORD, null);
        if (mailAddress != null && password != null) {
            final String previousToken = preferences.getString(Constantes.SHARED_PREFERENCES_FIREBASE_TOKEN_ID, null);
            if (previousToken != null) {
                PfeRx.updateFirebaseTokenId(previousToken, newToken);
            }
        }

    }
}