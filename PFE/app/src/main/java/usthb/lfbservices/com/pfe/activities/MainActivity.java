package usthb.lfbservices.com.pfe.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import usthb.lfbservices.com.pfe.network.PfeRx;
import usthb.lfbservices.com.pfe.R;
import usthb.lfbservices.com.pfe.utils.DisposableManager;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PfeRx.getProducts(this);
    }

    @Override
    protected void onDestroy() {
        DisposableManager.dispose();
        super.onDestroy();
    }
}
