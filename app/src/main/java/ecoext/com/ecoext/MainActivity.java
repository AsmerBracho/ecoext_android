package ecoext.com.ecoext;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class MainActivity extends AppCompatActivity {

    LoginButton LoginBtnFacebook;
    CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // find the Facebook button
        LoginBtnFacebook = findViewById(R.id.btnRegisterFacebook);
        callbackManager = CallbackManager.Factory.create();

        LoginBtnFacebook.setReadPermissions("email");

        //
        TextView loginLink = findViewById(R.id.textViewLogin);
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginPage = new Intent(getApplicationContext(), Login.class);
                startActivity(loginPage);
            }
        });

        Button registerEmail = findViewById(R.id.btnRegisterEmail);
        registerEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerEmailPage = new Intent(getApplicationContext(), EcoExTRegister.class);
                startActivity(registerEmailPage);
            }
        });

    } // End of onCreate

    public void loginWithFacebook(View v) {
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "User Canceled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
