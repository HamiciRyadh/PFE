package usthb.lfbservices.com.pfe.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import usthb.lfbservices.com.pfe.R;
import usthb.lfbservices.com.pfe.network.PfeRx;
import usthb.lfbservices.com.pfe.utils.FormValidation;

public class RegisterActivity extends AppCompatActivity {

    private EditText mailAddress;
    private EditText password;
    private EditText passwordConfirmation;
    private Button signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initVariables();
    }

    public void initVariables() {
        mailAddress = findViewById(R.id.signup_mail_address);
        password = findViewById(R.id.signup_password);
        passwordConfirmation = findViewById(R.id.signup_password_confirmation);
        signup = findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean valid = true;
                final String sMailAddress = mailAddress.getText().toString();
                final String sPassword = password.getText().toString();
                final String sPasswordConfirmation = passwordConfirmation.getText().toString();
                if (!FormValidation.isMailAddress(sMailAddress)) {
                    mailAddress.setError(getResources().getString(R.string.invalid_mail_address));
                    valid = false;
                }
                if (!FormValidation.isPassword(sPassword)) {
                    password.setError(getResources().getString(R.string.invalid_password));
                    valid = false;
                }
                if (!sPassword.equals(sPasswordConfirmation)) {
                    passwordConfirmation.setError(getResources().getString(R.string.passwords_not_matching));
                    valid = false;
                }
                if (valid) PfeRx.register(RegisterActivity.this, sMailAddress, sPassword);
            }
        });
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
        finish();
    }
}
