package ecoext.com.ecoext;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterWithEmailActivity extends AppCompatActivity {

    private Button buttonRegister;
    private EditText editTextEmmail;
    private EditText editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_with_email);

        TextView loginLink = findViewById(R.id.textViewLogin);
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginPage = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(loginPage);
            }
        });
    }
}
