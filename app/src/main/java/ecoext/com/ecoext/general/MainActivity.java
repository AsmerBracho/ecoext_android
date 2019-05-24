package ecoext.com.ecoext.general;

import android.animation.Animator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
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

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ecoext.com.ecoext.AddTransactionToPurseMutation;
import ecoext.com.ecoext.AddUserMutation;
import ecoext.com.ecoext.records.CreateNewRecord;
import ecoext.com.ecoext.GetAllUserTransactionsOrderByDateQuery;
import ecoext.com.ecoext.GetTransactionByTokenQuery;
import ecoext.com.ecoext.GetUserQuery;
import ecoext.com.ecoext.GetUserTransactionsQuery;
import ecoext.com.ecoext.home.HomeFragment;
import ecoext.com.ecoext.receipt.Item;
import ecoext.com.ecoext.home.LoaderHomeFragment;
import ecoext.com.ecoext.notification.NotificationsFragment;
import ecoext.com.ecoext.R;
import ecoext.com.ecoext.receipt.ReceiptActivity;
import ecoext.com.ecoext.records.RecordsFragment;
import ecoext.com.ecoext.login.RegisterActivity;
import ecoext.com.ecoext.statistics.ReportsStatisticsFragment;

/**
 * Main Activity Class
 * This class is the Main source of resource where most connections and processes
 * are handled. This activity is the first class loaded every time the application load,
 * and according to the different cases an action is taken. (see details within)
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final String TAG = "MainActivity"; // Private TAG for Logs

    // Global Views
    private Context context; // Our application context
    private ImageView photoImageView; // User photo to be displayed
    private TextView nameTextView; // User Name
    private TextView emailTextView; // User email

    // Google API Client
    private GoogleApiClient googleApiClient; // GOOGLE CLIENT

    // Firebase Variables
    private FirebaseAuth firebaseAuth; // For Authentication
    private FirebaseAuth.AuthStateListener firebaseAuthListener; // Listener that track changes

    // Values used for given round corners to images
    // A contribution created by javier gonzalez cabezas

    private static int sCorner = 50; // corner radius
    private static int sMargin = 1; // margin

    /**
     * Method to gets the corner radius parameter froma  different classe
     * if needed
     *
     * @return an int sCorner => corner radius
     */
    public static int getsCorner() {
        return sCorner;
    }

    /**
     * Method to gets the margin parameter from a different classe
     * if needed
     *
     * @return an int sMargin => margin radius
     */
    public static int getsMargin() {
        return sMargin;
    }

    // QR Code Integration
    // Barcode scanning implementation in the open source ZXing project.
    // https://github.com/zxing/zxing
    private static IntentIntegrator integrator; // Allows to integrate an call QR Scanner
    private static Activity activity; // parameter needed for integrator

    //create a progress Dialog
    private ProgressDialog progressDialog;

    // Floating Menu and sub menus
    private FloatingActionButton fabMain; // main floating menu
    private FloatingActionButton fabOne; // primary floating menu
    private FloatingActionButton fabTwo; // secondary floating menu
    private TextView labelQR; // label to be displayed for Scanning
    private TextView labelCreate; // label to be displayed for create a new record
    private Float translationY = 100f;
    private OvershootInterpolator interpolator = new OvershootInterpolator();
    private Boolean isMenuOpen = false; // boolean to check whether menu is open or not

    // Data Lists
    private ArrayList<GetUserTransactionsQuery.Purse> purses = new ArrayList<>(); // Array of Purses
    private ArrayList<String> pursesNames = new ArrayList<>(); // Array of purses Names
    private ArrayList<Integer> purseId = new ArrayList<>();
    private ArrayList<GetAllUserTransactionsOrderByDateQuery.UserTransaction> userTransactions = new ArrayList<GetAllUserTransactionsOrderByDateQuery.UserTransaction>(); // Array that contains all the user transactions

    // Validator for my Scan
    private boolean validation;
    private String operation;

    // Navigation Menus
    private NavigationView navigationView; // Main Navigation Menu
    private BottomNavigationView bottomNav; // Bottom navigation Menu

    // User Transaction id
    private int accountId; // User Account ID

    // Purse selected when scanning
    private RecyclerView selectPurseBeforeScanning; // Pop up menu to select purse
    private CardView purseSelectedCard; // Pop up Background
    private static int purseSelected; // Int that stored the value of the selected purse
    private SelectBeforeScanningAdapter selectBeforeScanningAdapter; // Adapter to implement for the pop up


    //*---------------------------------STARTING MAIN APPLICATION ----------------------------------------

    /**
     * Method onCreate  is call every time the application loads and inside we define
     * and initialize our main variables and instances
     * this is similar as what a constructor does
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar); // get the view for the toolbar
        setSupportActionBar(toolbar); // Define this as the Main toolbar

        activity = this; // define the activity as MainActivity
        context = this; // context is in  MainActivity

        progressDialog = new ProgressDialog(context); // Initialize progress Bar in this context

        // Create a Drawer for our side menu
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Side Navigation Menu
        navigationView = findViewById(R.id.nav_view); //
        navigationView.setNavigationItemSelectedListener(this);

        //Bottom Nav Menu
        bottomNav = findViewById(R.id.bottom_nav_menu);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        initFabMenu(); // Initiate the Fab Menu

        fabTwo.setClickable(false); // Disable fabTwo functionality
        fabOne.setClickable(false); // Disable fabOne functionality

        // Start Application in the MainActivity Page
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new LoaderHomeFragment()).commit();

            // While the loader is loading Disable elements so no action can be performed
            navigationView.setEnabled(false);
            navigationView.setVisibility(View.GONE);
            bottomNav.setEnabled(false);
            bottomNav.setVisibility(View.GONE);
            fabMain.setVisibility(View.GONE);
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            navigationView.setCheckedItem(R.id.nav_home);
        }

        // Pulling data from header to our views
        View nagView = navigationView.getHeaderView(0); // create a ngView
        photoImageView = nagView.findViewById(R.id.photoUser); // use nagView to set up  photo
        nameTextView = nagView.findViewById(R.id.username);
        emailTextView = nagView.findViewById(R.id.email);

        // Create a SignIn google Instance
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Use the google API to build the google instance
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Initialize Firebase variables
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // Create a Firebase User
                FirebaseUser user = firebaseAuth.getCurrentUser();
                // If user is different than Null
                if (user != null) {  // => then set up the user Data
                    setUserData(user);
                } else { // Else
                    goRegisterScreen(); // It means there is not session so redirect to register/login Screen
                }
            }
        };

        // Get info database
        getInfoDataBase();

        // If an operation have been executed in another screen and redirected
        // to this one, we need to take the extra info and check the source of
        // to determine the precedent and show the right dialog message
        operation = getIntent().getStringExtra("newOperation");

        // If operation is a New Transaction
        if ("EcoExTCreateTransaction".equals(operation)) {
            operation = null;
            new AlertDialog.Builder(context)
                    .setTitle("TRANSACTION ADDED")
                    .setMessage("You have successfully added a new transaction")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Dismiss
                        }
                    })
                    .show();
        } else if ("EcoExTCreatePurse".equals(operation)) {
            operation = null;
            new AlertDialog.Builder(context)
                    .setTitle("PURSE ADDED")
                    .setMessage("You have successfully added a new purse")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Dismiss
                        }
                    })
                    .show();
        } else if ("EcoExTTransactionDeleted".equals(operation)) {
            operation = null;
            new AlertDialog.Builder(context)
                    .setTitle("TRANSACTION DELETED")
                    .setMessage("You have successfully deleted the transaction")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Dismiss
                        }
                    })
                    .show();

        } else if ("EcoExTPurseDeleted".equals(operation)) {
            operation = null;
            new AlertDialog.Builder(context)
                    .setTitle("PURSE DELETED")
                    .setMessage("You have successfully deleted the purse and all transaction attached to it")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Dismiss
                        }
                    })
                    .show();
        }
    }

    /**
     * After an operation that generate a result id called this method comes into place
     * this is going to be used to handle the actions after a EcoExT QR code is scanned
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data); // create a result
        // If result is no null it means the scanned has been initiated
        if (result != null) {
            if (result.getContents() == null) { // Scan cancelled
                Toast.makeText(this, "Scan Cancelled", Toast.LENGTH_LONG).show();

                // ELSE, MANAGE THE ACTIONS
            } else {
                // Get the token from the Pi
                final String token = result.getContents();
                //create a transaction
                final GetTransactionByTokenQuery.TransactionByToken[] transaction = new GetTransactionByTokenQuery.TransactionByToken[1];
                //Create an Array of Items
                final ArrayList<Item> listOfItems = new ArrayList<>();
                // create Intent to sent info to receipt
                final Intent showReceipt = new Intent(getApplicationContext(), ReceiptActivity.class);

                // This Thread will Run after the verification is done
                // Jump to line 386
                final Runnable progressRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (validation) {
                            // showed OK
                            progressDialog.dismiss();
                            new AlertDialog.Builder(context)
                                    .setTitle("RECEIPT SCANNED")
                                    .setMessage("You have successfully scanned your receipt")
                                    .setPositiveButton("SEE RECEIPT", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            getInfoDataBase();
                                            // start Intent with receipt
                                            getApplicationContext().startActivity(showReceipt);
                                        }
                                    })
                                    .setNegativeButton("EXIT", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            getInfoDataBase();
                                            Log.d("MainActivity", "Aborting...");
                                        }
                                    })
                                    .show();
                            // If a token is not
                        } else {
                            progressDialog.dismiss();
                            new AlertDialog.Builder(context, R.style.CustomDialogTheme)
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
                };

                progressDialog.setTitle("EcoExTing");
                progressDialog.setMessage("Please wait while we retrieve your Receipt...");
                progressDialog.show();

                // Query DataBase
                MyApolloClient.getMyApolloClient().query(GetTransactionByTokenQuery.builder().
                        token(token).build()).enqueue(new ApolloCall.Callback<GetTransactionByTokenQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<GetTransactionByTokenQuery.Data> response) {
                        Log.d(TAG, "responseResults: " + response.data());
                        Log.d(TAG, "msg:" + validation);

                        if (response.data().transactionByToken() != null) {
                            validation = true;
                            transaction[0] = response.data().transactionByToken();

                            Log.d(TAG, "onScannedQR: " + response.data().transactionByToken());
                            Log.d(TAG, "onScannedQRValidation: " + validation);

                            final SimpleDateFormat format = new SimpleDateFormat("dd/MM/YYYY");
                            final Date date = new Date(Long.parseLong(transaction[0].date()));
                            String l = transaction[0].label().toUpperCase();
                            final String bLogo = String.valueOf(l.charAt(0));
                            final double[] totalTax = {0};
                            double total = 0;
                            for (int i = 0; i < transaction[0].items().size(); i++) {
                                total += transaction[0].items().get(i).price() * transaction[0].items().get(i).quantity();
                            }
                            DecimalFormat df = new DecimalFormat(".##");
                            final String finalTotal = df.format(total);
                            for (int j = 0; j < transaction[0].items().size(); j++) {

                                double tax = (transaction[0].items().get(j).quantity()
                                        * transaction[0].items().get(j).price()
                                        * transaction[0].items().get(j).tax()) / 100;

                                totalTax[0] += tax;
                                listOfItems.add(new Item(
                                        transaction[0].items().get(j).transaction_id(),
                                        transaction[0].items().get(j).product(),
                                        transaction[0].items().get(j).price(),
                                        transaction[0].items().get(j).quantity(),
                                        transaction[0].items().get(j).tax()
                                ));
                            }

                            // put extras to pass to next activity and know with receipt are we currently clicking
                            showReceipt.putParcelableArrayListExtra("listOfItems", listOfItems);
                            showReceipt.putExtra("date", format.format(date));
                            showReceipt.putExtra("number", transaction[0].transaction_id().toString());
                            showReceipt.putExtra("total", finalTotal);
                            showReceipt.putExtra("name", bLogo);
                            showReceipt.putExtra("tax", (df.format(totalTax[0])));

                            // Add Transaction to Records
                            MyApolloClient.getMyApolloClient().mutate(
                                    AddTransactionToPurseMutation.builder()
                                            .token(token)
                                            .pid(purseSelected)
                                            .build())
                                    .enqueue(new ApolloCall.Callback<AddTransactionToPurseMutation.Data>() {
                                        @Override
                                        public void onResponse(@NotNull Response<AddTransactionToPurseMutation.Data> response) {
                                            // Call the Thread Jump to line 339
                                            Handler pdCanceller = new Handler(Looper.getMainLooper());
                                            pdCanceller.postDelayed(progressRunnable, 1000);
                                        }

                                        @Override
                                        public void onFailure(@NotNull ApolloException e) {

                                        }
                                    });

                        } else {
                            // Call the Thread Jump to line 339
                            validation = false;
                            Handler pdCanceller = new Handler(Looper.getMainLooper());
                            pdCanceller.postDelayed(progressRunnable, 1000);

                            Log.d(TAG, "onScannedQRValidation: " + validation);
                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                    }
                });

                closeMenu(); // close menu after scanning
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Method that Handle the click of the Back bottom in your android device
     * It does not takes parameters and it is void. but it is called automatically
     * when back bottom is pressed
     */
    @Override
    public void onBackPressed() {
        //check if drawer is closed if not close it
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            //close fab menu
            closeMenu();
        } else if (isMenuOpen) {
            closeMenu();
        } else {
            super.onBackPressed();
            // close fab menu
            closeMenu();
        }
    }

    /**
     * Bottom Navigation Menu Functionality
     * Method tat handle the clicks and actions performed in the bottom navigation menu
     */
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.botton_home:
                            navigationView.setCheckedItem(R.id.nav_home);
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    new HomeFragment(purses, userTransactions, accountId)).commit();
                            break;
                        case R.id.bottom_records:
                            navigationView.setCheckedItem(R.id.nav_records);
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    new RecordsFragment(userTransactions)).commit();
                            break;
                    }
                    return true;
                }
            };

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_records) {
            bottomNav.setSelectedItemId(R.id.bottom_records);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new RecordsFragment(userTransactions)).commit();
        } else if (id == R.id.nav_reports) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ReportsStatisticsFragment(userTransactions)).commit();
        } else if (id == R.id.nav_notifications) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new NotificationsFragment()).commit();
        } else if (id == R.id.nav_home) {
            bottomNav.setSelectedItemId(R.id.botton_home);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment(purses, userTransactions, accountId)).commit();
        } else if (id == R.id.logout) {
            // show dialog confirmation
            new AlertDialog.Builder(context)
                    .setTitle("CONFIRM")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            logout();
                        }
                    })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
        }

        closeMenu(); // close menu
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * This methods will be called to set the user Data from firebase
     * It takes a firebase instance as argument
     *
     * @param user an firebase user
     */
    private void setUserData(FirebaseUser user) {
        nameTextView.setText(user.getDisplayName());
        emailTextView.setText(user.getEmail());

        // In order to stylize the picture we will apply a rounded corners style
        // provided by javier Gonzales in the class RoundedCornersTransformation

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

    /**
     * Void method that Initiate a new Intent to redirect user to
     * the Login Register screen
     */
    private void goRegisterScreen() {
        Intent goLoginScreen = new Intent(this, RegisterActivity.class);
        goLoginScreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(goLoginScreen);
    }

    /**
     * Logout Method that kill the session by logging out the Firebase Instance
     * and Google Instance
     */
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

    /**
     * Method that revoke the credential for Firebase and Google
     *
     * @param view
     */
    public void revoke(View view) {
        FirebaseAuth.getInstance().signOut();
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

    /**
     * Method that handdle conections problems
     *
     * @param connectionResult
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // CODE HERE
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

    /**
     * Open Floating Menu and present the different options
     */
    private void openMenu() {
        fabTwo.setClickable(true);
        fabOne.setClickable(true);
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

    /**
     * Close Floating Menu and disable functionality
     * for the sub menus and labels
     */
    private void closeMenu() {
        //
        selectPurseBeforeScanning.setEnabled(false);
        selectPurseBeforeScanning.setVisibility(View.GONE);

        purseSelectedCard.setEnabled(false);
        purseSelectedCard.setVisibility(View.GONE);

        fabTwo.setClickable(false);
        fabOne.setClickable(false);
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

    /**
     * Method that handle the actions taken when pressing
     * first floating bottom
     */
    private void handleFabOne() {
        // Redirect to create record activity
        Intent createNewRecords = new Intent(getApplicationContext(), CreateNewRecord.class);
        createNewRecords.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        createNewRecords.putStringArrayListExtra("purseNames", pursesNames);
        createNewRecords.putIntegerArrayListExtra("purseId", purseId);
        startActivity(createNewRecords);
    }


    /**
     * Method that handle the actions taken when pressing
     * second floating bottom
     */
    private void handleFabTwo() {
        Log.i(TAG, "handleFabTwo: ");

        selectPurseBeforeScanning.setEnabled(true);
        selectPurseBeforeScanning.setVisibility(View.VISIBLE);
        purseSelectedCard.setEnabled(true);
        purseSelectedCard.setVisibility(View.VISIBLE);
    }

    /**
     * This method is a static method since it will be called from
     * different classes.
     * <p>
     * What it does is call an integrator that opens up the camara as resource to scan the QR
     */
    public static void callScanner() {
        Log.d(TAG, "Which one" + purseSelected);

        integrator = new IntentIntegrator(activity);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        // set the title for the Scanner
        integrator.setPrompt("EcoExT QR Scanner");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();

    }

    /**
     * Set Static value purseSelected
     *
     * @param id takes an int and set it as the purse current selected
     */
    public static void setPurseSelected(int id) {
        purseSelected = id;
    }

    /**
     * Since this Activity implements the OnClickListener interface we need to implements
     * the on Click method that handles all the different clicks
     *
     * @param view
     */
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
                handleFabOne();
                break;

            case R.id.fabTwo:
                Log.i(TAG, "onClick: fab two");
                handleFabTwo();
                break;
        }
    }

    /**
     * This method is the Core of the application since it handle the queries that brings the information
     * that fill the list to be displayed in the entire application.
     */
    private void getInfoDataBase() {
        // Clean the Data so we make sure we do NOT duplicate it
        purses.clear();
        pursesNames.clear();
        userTransactions.clear();

        // Create a firebase user
        final FirebaseUser user = firebaseAuth.getCurrentUser();

        try {
            // Try to get an user where user id is equal to firebase UID
            MyApolloClient.getMyApolloClient().query(
                    GetUserQuery.builder().id(user.getUid())
                            .build()).enqueue(new ApolloCall.Callback<GetUserQuery.Data>() {
                @Override
                public void onResponse(@NotNull Response<GetUserQuery.Data> response) {
                    Log.d(TAG, "onResponseUser: " + response.data().user());

                    // If user. size == 0 it means there is no user in database
                    if (response.data().user().size() == 0) { // => then
                        // Create a new User
                        Log.d(TAG, "onResponseUserInside: I am inside If");

                        // Create an User
                        MyApolloClient.getMyApolloClient().mutate(
                                AddUserMutation.builder()
                                        .first(user.getDisplayName())
                                        .last(user.getDisplayName())
                                        .email(user.getEmail())
                                        .uid(user.getUid())
                                        .password("EcoExT")
                                        .gender("m")
                                        .dob(11111)
                                        .build()).enqueue(new ApolloCall.Callback<AddUserMutation.Data>() {
                            @Override
                            public void onResponse(@NotNull Response<AddUserMutation.Data> response) {
                                Log.d(TAG, "onResponseUserInside: DONE WRITING USER");
                                // recursion is applied to bypass this step now and go to next one
                                getInfoDataBase();
                            }

                            @Override
                            public void onFailure(@NotNull ApolloException e) {

                            }
                        });
                    } else {

                        // Get User Transaction Order By Date
                        MyApolloClient.getMyApolloClient().query(
                                GetAllUserTransactionsOrderByDateQuery.builder().user_id(user.getUid())
                                        .build()).enqueue(new ApolloCall.Callback<GetAllUserTransactionsOrderByDateQuery.Data>() {
                            @Override
                            public void onResponse(@NotNull Response<GetAllUserTransactionsOrderByDateQuery.Data> response) {
                                userTransactions.addAll(response.data().userTransactions());
                            }

                            @Override
                            public void onFailure(@NotNull ApolloException e) {

                            }
                        });

                        // query the data and get user transactions with extra Info
                        // such as purses info
                        MyApolloClient.getMyApolloClient().query(
                                GetUserTransactionsQuery.builder().id(user.getUid())
                                        .build()).enqueue(new ApolloCall.Callback<GetUserTransactionsQuery.Data>() {
                            @Override
                            public void onResponse(@NotNull Response<GetUserTransactionsQuery.Data> response) {
                                for (int i = 0; i < response.data().user().size(); i++) {
                                    accountId = response.data().user().get(0).account_id();
                                    for (int j = 0; j < response.data().user().get(i).account().purse().size(); j++) {
                                        // add PurseName to list of Purses String.
                                        pursesNames.add(response.data().user().get(i).account().purse().get(j).name());
                                        purseId.add(response.data().user().get(i).account().purse().get(j).purse_id());
                                        purses.add(new GetUserTransactionsQuery.Purse(
                                                "Purse + j",
                                                response.data().user().get(i).account().purse().get(j).purse_id(),
                                                response.data().user().get(i).account().purse().get(j).name(),
                                                response.data().user().get(i).account().purse().get(j).description(),
                                                response.data().user().get(i).account().purse().get(j).transaction()
                                        ));
                                    }
                                }

                                // After performing the background activity we te up the views on the main thread
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //Enable elements
                                        navigationView.setEnabled(true);
                                        navigationView.setVisibility(View.VISIBLE);
                                        bottomNav.setEnabled(true);
                                        bottomNav.setVisibility(View.VISIBLE);
                                        fabMain.setVisibility(View.VISIBLE);

                                        DrawerLayout drawer = findViewById(R.id.drawer_layout);
                                        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

                                    }
                                });

                                // Recycler view and adapter
                                selectBeforeScanningAdapter = new SelectBeforeScanningAdapter(context, purses);

                                purseSelectedCard = findViewById(R.id.purse_selected_card);
                                selectPurseBeforeScanning = findViewById(R.id.select_purse_before_scan);
                                LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                                selectPurseBeforeScanning.setLayoutManager(layoutManager);
                                selectPurseBeforeScanning.setAdapter(selectBeforeScanningAdapter);

                                // After performance the query Load the Activity with the data
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                        new HomeFragment(purses, userTransactions, accountId)).commit();

                            }

                            @Override
                            public void onFailure(@NotNull ApolloException e) {

                            }
                        });
                    }
                }

                @Override
                public void onFailure(@NotNull ApolloException e) {

                }
            });
        } catch (Exception e) {
            // Error occurs
            Log.d(TAG, "Not Able to connect todatabase");
        }
    }
}
