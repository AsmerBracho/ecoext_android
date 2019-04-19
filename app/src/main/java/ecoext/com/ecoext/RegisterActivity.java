package ecoext.com.ecoext;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
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

public class RegisterActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    // GOOGLE
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton googleLoginButton;
    public static final int SIGN_IN_GOOGLE_CODE = 777;

    /*Facebook
    private LoginButton facebookLoginButton;
    private CallbackManager callbackManager;
    */

    // Firebase Variables
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    private ProgressBar progressBar;
    private ProgressDialog progressDialog;
    private CheckBox terms;

    // Start of the OnCreate Method

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize progress Bar
        //progressBar = findViewById(R.id.progr);
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


        // Register with Emmail and Password
        /*
        Button registerEmail = findViewById(R.id.btnRegisterEmail);
        registerEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerEmailPage = new Intent(getApplicationContext(), RegisterWithEmailActivity.class);
                registerEmailPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(registerEmailPage);
            }
        });
        */

    } // End of onCreate

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, SIGN_IN_GOOGLE_CODE);
    }

    private void goMainScreen() {
        Intent goMainScreen = new Intent(this, MainActivity.class);
        goMainScreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        goMainScreen.putExtra("loadInfo", "LOADINFO");
        startActivity(goMainScreen);
    }

    @Override
    protected  void onStart() {
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
                //Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        //Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        //set visibilities so progress bar is shown and loginButton hidden

        progressDialog.setMessage("Singing in ...");
        progressDialog.show();
        //progressBar.setVisibility(View.VISIBLE);
       // googleLoginButton.setVisibility(View.GONE);

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //og.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), R.string.login_error, Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                       // progressBar.setVisibility(View.GONE);
                       // googleLoginButton.setVisibility(View.VISIBLE);
                        // ...
                    }
                });
    }

        private void goRegisterScreen() {
            Intent goLoginScreen = new Intent(this, RegisterActivity.class);
            goLoginScreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(goLoginScreen);
        }
}
