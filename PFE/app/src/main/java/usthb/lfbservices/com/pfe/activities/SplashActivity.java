package usthb.lfbservices.com.pfe.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import usthb.lfbservices.com.pfe.network.PfeAPI;
import usthb.lfbservices.com.pfe.utils.Constantes;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getSharedPreferences(Constantes.SHARED_PREFERENCES_USER,MODE_PRIVATE);
        String mailAddress = preferences.getString(Constantes.SHARED_PREFERENCES_USER_EMAIL, null);
        String password = preferences.getString(Constantes.SHARED_PREFERENCES_USER_PASSWORD, null);
        if (mailAddress != null && password != null) {
            PfeAPI.getInstance().setAuthorization(mailAddress, password);
        }

        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

        /*
        String android_id = FirebaseInstanceId.getInstance().getToken();
            Log.e("Android ID", "android id " + android_id.toString());
            */
    }
}
