package ecoext.com.ecoext;

import android.animation.Animator;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {


    private ImageView photoImageView;
    private TextView nameTextView;
    private TextView emailTextView;
    private TextView idTextView;

    private GoogleApiClient googleApiClient;

    // Firebase Variables
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    // Values used for given round corners to images
    public static int sCorner = 50;
    public static int sMargin = 1;

    IntentIntegrator integrator;

    //Database Variables
    private final String HASH_VALIDATOR = "0";

    /**
     * Variables for Floating Menu
     * 2 submenus
     */

    FloatingActionButton fabMain;
    FloatingActionButton fabOne;
    FloatingActionButton fabTwo;
    TextView labelQR;
    TextView labelCreate;
    View clickOut;
    Float translationY = 100f;
    OvershootInterpolator interpolator = new OvershootInterpolator();
    private static final String TAG = "MainActivity";
    Boolean isMenuOpen = false;

    //*********************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Start Application in the MainActivity Page
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        // Pulling data from header to our views
        View nagView = navigationView.getHeaderView(0);
        photoImageView = nagView.findViewById(R.id.photoUser);
        nameTextView = nagView.findViewById(R.id.username);
        emailTextView = nagView.findViewById(R.id.email);
        //idTextView =  findViewById(R.id.idTextView);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        // Initialize Firebase variables
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    setUserData(user);
                } else {
                    goRegisterScreen();
                }
            }
        };

        // Initiate the Fab Menu
        initFabMenu();

        /**
         * Get the information from database and process it accordingly into the application
         * by creating lists that will contain the user data
         */
        GetUserInformationFromDataBase task = new GetUserInformationFromDataBase();
        task.execute("https://samples.openweathermap.org/data/2.5/weather?q=London,uk&appid=b6907d289e10d714a6e88b30761fae22");
    }

    // onActivityResult we are going to manage the QRScanner actions
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Scan Cancelled", Toast.LENGTH_LONG).show();
            } else {
                if (result.getContents().contains(HASH_VALIDATOR)) {

                    // get the transaction ID
                    String s = result.getContents();
                    String[] transactionID = s.split(" ");
                    String finalID = "";
                    for (int i = 1; i < transactionID.length; i++) {
                        finalID = finalID.concat(transactionID[i] + " ");
                    }
                    new AlertDialog.Builder(this)
                            .setTitle("RECEIPT SCANNED")
                            .setMessage("You have successfully scanned your receipt \n\nID: " + finalID)
                            .setPositiveButton("SEE RECEIPT", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setNegativeButton("EXIT", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d("MainActivity", "Aborting...");
                                }
                            })
                            .show();

                } else {
                    new AlertDialog.Builder(this, R.style.CustomDialogTheme)
                            .setTitle("WRONG QR CODE")
                            .setMessage("This is not a valid EcoExT QR Code, please SCAN a VALID code")
                            .setPositiveButton("SCAN", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    integrator.initiateScan();
                                }
                            })
                            .show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void onBackPressed() {
        //check if drawer is closed if not close it
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            //close fab menu
            closeMenu();
        } else {
            super.onBackPressed();
            // close fab menu
            closeMenu();
        }
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the MainActivity/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    */

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_records) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new RecordsFragment()).commit();
        } else if (id == R.id.nav_reports) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ReportsStatisticsFragment()).commit();
        } else if (id == R.id.nav_notifications) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new NotificationsFragment()).commit();
        } else if (id == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
        } else if (id == R.id.logout) {
            logout();
        }

        closeMenu();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * This methos will be called to set the userData from firebase
     * It takes a firebase instance as argument
     *
     * @param user
     */
    private void setUserData(FirebaseUser user) {
        nameTextView.setText(user.getDisplayName());
        emailTextView.setText(user.getEmail());
        // idTextView.setText(user.getUid());

        /**
         * In order to stylize the picture we will apply a rounded corners style
         * provided by javier Gonzales in the class RoundedCornersTransformation
         */
        Glide.with(this).load(user.getPhotoUrl())
                .bitmapTransform(new RoundedCornersTransformation(MainActivity.this, sCorner, sMargin))
                .into(photoImageView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(firebaseAuthListener);

    }

    private void goRegisterScreen() {
        Intent goLoginScreen = new Intent(this, RegisterActivity.class);
        goLoginScreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(goLoginScreen);
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    goRegisterScreen();
                } else {
                    Toast.makeText(getApplicationContext(), "Logout could Not been Done", Toast.LENGTH_SHORT).show();
                }
            }
        });
        goRegisterScreen();
    }

    public void revoke(View view) {
        // revoke firebase Instance
        FirebaseAuth.getInstance().signOut();
        // revoke Facebook instance
        // LoginManager.getInstance().logOut();

        // revoke Google Instance
        Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    goRegisterScreen();
                } else {
                    Toast.makeText(getApplicationContext(), "Access could NOT been revoked", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    /**
     * Methods to follow are used for the animation and functionality
     * of the Fab button
     */
    private void initFabMenu() {
        // find the views
        fabMain = findViewById(R.id.fabMain);
        fabOne = findViewById(R.id.fabOne);
        fabTwo = findViewById(R.id.fabTwo);
        labelCreate = findViewById(R.id.label_create);
        labelQR = findViewById(R.id.label_QR);

        //set alphas
        fabOne.setAlpha(0f);
        fabTwo.setAlpha(0f);
        labelCreate.setAlpha(0f);
        labelQR.setAlpha(0f);

        //set translations
        fabOne.setTranslationY(translationY);
        fabTwo.setTranslationY(translationY);
        labelCreate.setTranslationY(translationY);
        labelQR.setTranslationY(translationY);

        //set Listeners
        fabMain.setOnClickListener(this);

        fabOne.setOnClickListener(this);
        fabTwo.setOnClickListener(this);
        labelCreate.setOnClickListener(this);
        labelQR.setOnClickListener(this);

    }

    private void openMenu() {
        isMenuOpen = !isMenuOpen;

        fabMain.animate().setInterpolator(interpolator).rotation(45f).setDuration(300).start();

        fabOne.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        fabTwo.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        labelCreate.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        labelQR.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();

        View layoutMain = findViewById(R.id.layout_main);
        View layoutBackground = findViewById(R.id.layout_background);

        int x = layoutBackground.getRight();
        int y = layoutBackground.getBottom();

        int startRadius = 0;
        int endRadius = (int) Math.hypot(layoutMain.getWidth(), layoutMain.getHeight());

        Animator anim = ViewAnimationUtils.createCircularReveal(layoutBackground, x, y, startRadius, endRadius);

        layoutBackground.setVisibility(View.VISIBLE);
        anim.start();

    }

    private void closeMenu() {
        isMenuOpen = !isMenuOpen;

        fabMain.animate().setInterpolator(interpolator).rotation(0f).setDuration(300).start();

        fabOne.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        fabTwo.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        labelCreate.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        labelQR.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();


        View layoutMain = findViewById(R.id.layout_main);
        final View layoutBackground = findViewById(R.id.layout_background);

        int x = layoutBackground.getRight();
        int y = layoutBackground.getBottom();

        int startRadius = Math.max(layoutMain.getWidth(), layoutMain.getHeight());
        int endRadius = 0;

        Animator anim = ViewAnimationUtils.createCircularReveal(layoutBackground, x, y, startRadius, endRadius);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                layoutBackground.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        anim.start();

        layoutBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeMenu();
            }
        });
    }

    private void handleFabTwo() {
        Log.i(TAG, "handleFabTwo: ");
        final Activity activity = this;

        //call the QR Scanner
        fabTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("EcoExT QR Scanner");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fabMain:
                Log.i(TAG, "onClick: fab main");
                if (isMenuOpen) {
                    closeMenu();
                } else {
                    openMenu();
                }
                break;
            case R.id.fabOne:
                Log.i(TAG, "onClick: fab one");
                //handleFabOne();

            case R.id.fabTwo:
                Log.i(TAG, "onClick: fab two");
                handleFabTwo();
                break;


        }
    }

}
