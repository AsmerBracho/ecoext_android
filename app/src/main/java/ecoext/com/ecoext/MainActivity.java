package ecoext.com.ecoext;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (AccessToken.getCurrentAccessToken() == null) {
            goRegisterScreen();
        }
    }

    private void goRegisterScreen() {
        Intent goLoginScreen = new Intent(this, RegisterActivity.class);
        goLoginScreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(goLoginScreen);
    }

    public void logout(View view) {
        LoginManager.getInstance().logOut();
        goRegisterScreen();
    }
}
