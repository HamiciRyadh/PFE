package usthb.lfbservices.com.pfe.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import usthb.lfbservices.com.pfe.network.PfeAPI;
import usthb.lfbservices.com.pfe.utils.Constants;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getSharedPreferences(Constants.SHARED_PREFERENCES_USER,MODE_PRIVATE);
        String mailAddress = preferences.getString(Constants.SHARED_PREFERENCES_USER_EMAIL, null);
        String password = preferences.getString(Constants.SHARED_PREFERENCES_USER_PASSWORD, null);
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
