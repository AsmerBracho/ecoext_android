package ecoext.com.ecoext.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import ecoext.com.ecoext.R;
import ecoext.com.ecoext.login.RegisterActivity;

/**
 * Class Login Activity
 * Takes the layout activity_login and inflate it
 */
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView registerLink = findViewById(R.id.textViewSignUp);
        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerPage = new Intent(getApplicationContext(), RegisterActivity.class);
                registerPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(registerPage);
            }
        });
    }

}
