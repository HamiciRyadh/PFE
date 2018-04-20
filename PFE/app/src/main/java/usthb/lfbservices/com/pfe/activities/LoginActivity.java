package usthb.lfbservices.com.pfe.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import usthb.lfbservices.com.pfe.R;
import usthb.lfbservices.com.pfe.network.PfeAPI;
import usthb.lfbservices.com.pfe.network.PfeRx;
import usthb.lfbservices.com.pfe.utils.FormValidation;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getName();

    private EditText mailAddress;
    private EditText password;
    private Button signin;
    private Button signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences preferences = getSharedPreferences("user",MODE_PRIVATE);
        String mailAddress = preferences.getString("email", null);
        String password = preferences.getString("password", null);
        if (mailAddress != null && password != null) {
            PfeAPI.getInstance().setAuthorization(mailAddress, password);
            Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
            startActivity(intent);
        }
        initVariables();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void initVariables() {
        mailAddress = findViewById(R.id.signin_mail_address);
        password = findViewById(R.id.signin_password);
        signin = findViewById(R.id.signin);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String sMailAddress = mailAddress.getText().toString();
                final String sPassword = password.getText().toString();
                if (!FormValidation.isMailAddress(sMailAddress)) {
                   mailAddress.setError(getResources().getString(R.string.invalid_mail_address));
                } else {
                   if (!FormValidation.isPassword(sPassword)) {
                       password.setError(getResources().getString(R.string.invalid_password));
                    } else {
                       PfeRx.connect(LoginActivity.this, sMailAddress, sPassword);
                    }
                }
            }
        });
        signup = findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }
}
