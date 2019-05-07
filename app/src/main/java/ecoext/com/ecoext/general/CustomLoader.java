package ecoext.com.ecoext.general;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

import ecoext.com.ecoext.R;
import ecoext.com.ecoext.general.MainActivity;

/**
 * Class Custom Loader
 * It contains a custom loader created for EcoExT
 * In this activity we get a reference of the operation we just performed
 * and redirect it to the MainActivity class
 */
public class CustomLoader extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_loader);

        int timeout = 2000; // make the activity visible for 4 seconds

        final String operation = getIntent().getStringExtra("newOperation");

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            // Run a Thread
            @Override
            public void run() {
                finish();
                Intent goHome = new Intent(getApplicationContext(), MainActivity.class);
                goHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                goHome.putExtra("newOperation", operation);
                startActivity(goHome);
            }
        }, timeout);
    }

}
