package ecoext.com.ecoext.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import ecoext.com.ecoext.R;
import ecoext.com.ecoext.general.MainActivity;

/**
 * Public Class RegisterActivity
 * First activity that runs in the application if there is not session
 * initiated
 */
public class RegisterActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    // GOOGLE
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton googleLoginButton;
    public static final int SIGN_IN_GOOGLE_CODE = 777;

    // Firebase Variables
    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;
    private CheckBox terms;

    /**
     * Method onCreate  is call every time the activity is loaded
     * Inside we define and initialize our main variables and instances
     * this is similar as what a constructor does
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        progressDialog = new ProgressDialog(this);

        googleLoginButton = findViewById(R.id.btnRegisterGoogle);
        googleLoginButton.setColorScheme(SignInButton.COLOR_DARK);

        // Initialize Firebase variables
        firebaseAuth = FirebaseAuth.getInstance();

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        terms = findViewById(R.id.checkBoxTermsAndConditions);


        findViewById(R.id.btnRegisterGoogle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btnRegisterGoogle:
                        if (terms.isChecked()) {
                            signIn();
                        } else {
                            Toast.makeText(getApplicationContext(), "You Must Agree to our Terms and Conditions", Toast.LENGTH_LONG).show();
                        }
                        break;
                    // ...
                }
            }
        });

    } // End of onCreate

    /**
     * Method signIn
     * It redirect to Google sing in service
     */
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, SIGN_IN_GOOGLE_CODE);
    }

    /**
     * Method goMainScreen
     * It redirects to the Main Screen (MAinActivity)
     */
    private void goMainScreen() {
        Intent goMainScreen = new Intent(this, MainActivity.class);
        goMainScreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        goMainScreen.putExtra("loadInfo", "LOADINFO");
        startActivity(goMainScreen);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            goMainScreen();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), R.string.login_error, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /// Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == SIGN_IN_GOOGLE_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        //set visibilities so progress bar is shown and loginButton hidden
        progressDialog.setMessage("Singing in ...");
        progressDialog.show();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext(), R.string.login_error, Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

}
