package ecoext.com.ecoext;

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


/**
 * Class: Main Activity
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private Context context;
    private ImageView photoImageView;
    private TextView nameTextView;
    private TextView emailTextView;

    private GoogleApiClient googleApiClient;

    // Firebase Variables
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    // Values used for given round corners to images
    private static int sCorner = 50;
    private static int sMargin = 1;
    public static int getsCorner() { return sCorner; }
    public static int getsMargin() { return sMargin; }

    private IntentIntegrator integrator;

    //create a progress Dialog
    private ProgressDialog progressDialog;
    /**
     * Variables for Floating Menu
     * and 2 submenus
     */

    private FloatingActionButton fabMain;
    private FloatingActionButton fabOne;
    private FloatingActionButton fabTwo;
    private TextView labelQR;
    private TextView labelCreate;
    private Float translationY = 100f;
    private OvershootInterpolator interpolator = new OvershootInterpolator();
    private static final String TAG = "MainActivity";
    private Boolean isMenuOpen = false;

    // Data Lists
    private ArrayList<GetUserTransactionsQuery.Purse> purses = new ArrayList<>();
    private ArrayList<String> pursesNames = new ArrayList<>();
    private ArrayList<Integer> purseId = new ArrayList<>();
    private ArrayList<GetAllUserTransactionsOrderByDateQuery.UserTransaction> userTransactions = new ArrayList<GetAllUserTransactionsOrderByDateQuery.UserTransaction>();

    // Validator for my Scan
    private  boolean validation;
    private String isThereReceipt;

    private String loadInfo;
    //*********************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;
        progressDialog = new ProgressDialog(context);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Botton Nav Menu
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_menu);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // Start Application in the MainActivity Page
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new LoaderHomeFragment()).commit();
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
        // Dissable fabTwo
        fabTwo.setClickable(false);

        // get Extra from CreateReceipt if Exits
        String i = getIntent().getStringExtra("newRecord");
        isThereReceipt = i;

        // get extra from Register
        loadInfo = getIntent().getStringExtra("loadInfo");
//        if ("LOADINFO".equals(loadInfo)) {
            getInfoDataBase();
//            loadInfo = null;
//        } else {
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                    new HomeFragment(purses)).commit();
//        }

        if ("EcoExT".equals(isThereReceipt)) {
            isThereReceipt = null;
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
        }
    }

    // onActivityResult we are going to manage the QRScanner actions
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Scan Cancelled", Toast.LENGTH_LONG).show();
            } else {
                // Get the token from the Pi
                final String token = result.getContents();
                //create a transaction
                final GetTransactionByTokenQuery.TransactionByToken[] transaction = new GetTransactionByTokenQuery.TransactionByToken[1];
                //Create an Array of Items
                final ArrayList<Item> listOfItems = new ArrayList<>();
                // create Intent to sent info to receipt
                final Intent showReceipt = new Intent(getApplicationContext(), ReceiptActivity.class);

                final Runnable progressRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (validation) {
                            // showed OK
//                            getInfoDataBase();
                            progressDialog.dismiss();
                            new AlertDialog.Builder(context)
                                    .setTitle("RECEIPT SCANNED")
                                    .setMessage("You have successfully scanned your receipt")
                                    .setPositiveButton("SEE RECEIPT", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            getInfoDataBase();
                                            // start Intent
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
                                        *transaction[0].items().get(j).price()
                                        *transaction[0].items().get(j).tax())/100;

                                totalTax[0] += tax;
                                listOfItems.add(new Item(
                                        transaction[0].items().get(j).transaction_id(),
                                        transaction[0].items().get(j).product(),
                                        transaction[0].items().get(j).price(),
                                        transaction[0].items().get(j).quantity(),
                                        transaction[0].items().get(j).tax()
                                ));
                            }
                            //put extras to pass to next activity and know with receipt are we currently clicking
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
                                            .pid(purses.get(0).purse_id)
                                            .build())
                                    .enqueue(new ApolloCall.Callback<AddTransactionToPurseMutation.Data>() {
                                        @Override
                                        public void onResponse(@NotNull Response<AddTransactionToPurseMutation.Data> response) {
                                            Handler pdCanceller = new Handler(Looper.getMainLooper());
                                            pdCanceller.postDelayed(progressRunnable, 1000);
                                        }

                                        @Override
                                        public void onFailure(@NotNull ApolloException e) {

                                        }
                                    });

                        } else {
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
                closeMenu();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        //check if drawer is closed if not close it
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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

    // Botton Navigation Menu Functionallity
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.botton_home:
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    new HomeFragment(purses)).commit();
                            break;
                        case R.id.bottom_records:
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    new RecordsFragment(purses)).commit();
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
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new RecordsFragment(purses)).commit();
        } else if (id == R.id.nav_reports) {


            // THIS NEED TO BE TAKEN OFF
            getInfoDataBase();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ReportsStatisticsFragment()).commit();
        } else if (id == R.id.nav_notifications) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new NotificationsFragment()).commit();
        } else if (id == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment(purses)).commit();
        } else if (id == R.id.logout) {
            logout();
        }

        closeMenu();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * This methods will be called to set the userData from firebase
     * It takes a firebase instance as argument
     *
     * @param user
     */
    private void setUserData(FirebaseUser user) {
        nameTextView.setText(user.getDisplayName());
        emailTextView.setText(user.getEmail());

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
        fabTwo.setClickable(true);
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
        fabTwo.setClickable(false);
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
                handleFabOne();
                break;

            case R.id.fabTwo:
                Log.i(TAG, "onClick: fab two");
                handleFabTwo();
                break;
        }
    }

    private void handleFabOne() {
        Intent createNewRecords = new Intent(getApplicationContext(), CreateNewRecord.class);
        createNewRecords.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        createNewRecords.putStringArrayListExtra("purseNames", pursesNames);
        createNewRecords.putIntegerArrayListExtra("purseId", purseId);
        startActivity(createNewRecords);

    }

    private void getInfoDataBase() {
        // Clean the Data
        purses.clear();
        pursesNames.clear();
        userTransactions.clear();

        final FirebaseUser user = firebaseAuth.getCurrentUser();

        MyApolloClient.getMyApolloClient().query(
                GetUserQuery.builder().id(user.getUid())
                        .build()).enqueue(new ApolloCall.Callback<GetUserQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<GetUserQuery.Data> response) {
                Log.d(TAG, "onResponseUser: " + response.data().user());

                if (response.data().user().size() == 0) {
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


                    // query the data and get user transactions
                    MyApolloClient.getMyApolloClient().query(
                            GetUserTransactionsQuery.builder().id(user.getUid())
                                    .build()).enqueue(new ApolloCall.Callback<GetUserTransactionsQuery.Data>() {
                        @Override
                        public void onResponse(@NotNull Response<GetUserTransactionsQuery.Data> response) {
                            for (int i = 0; i < response.data().user().size(); i++) {
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
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    new HomeFragment(purses)).commit();

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


    }


}
