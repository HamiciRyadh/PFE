package usthb.lfbservices.com.pfe.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import usthb.lfbservices.com.pfe.network.PfeAPI;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getSharedPreferences("user",MODE_PRIVATE);
        String mailAddress = preferences.getString("email", null);
        String password = preferences.getString("password", null);
        if (mailAddress != null && password != null) {
            PfeAPI.getInstance().setAuthorization(mailAddress, password);
            Intent intent = new Intent(SplashActivity.this, MapsActivity.class);
            startActivity(intent);
            finish();
        }
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
