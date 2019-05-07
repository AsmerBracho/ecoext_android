package ecoext.com.ecoext;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PurseDetails extends AppCompatActivity {

    // Extras Variables
    private String purseID;
    private String purseName;
    private String purseDescription;
    private String purseBalance;

    private FirebaseAuth firebaseAuth; // firabase instance
    // create array that contains all records from database query
    private ArrayList<GetAllUserTransactionsOrderByDateQuery.UserTransaction> userTransactions = new ArrayList<GetAllUserTransactionsOrderByDateQuery.UserTransaction>();
    // array to filter those results
    private ArrayList<GetAllUserTransactionsOrderByDateQuery.UserTransaction> userTransactionsFilteredByPurse = new ArrayList<GetAllUserTransactionsOrderByDateQuery.UserTransaction>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_purse_fragment_container);

        // initialize firebase Instance
        firebaseAuth = FirebaseAuth.getInstance();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_details,
                new LoaderHomeFragment()).commit();

        // Get Extras from  previous Activity
        this.purseID = getIntent().getStringExtra("PurseID"); // purse ID
        this.purseName = getIntent().getStringExtra("PurseName"); // purse Name
        this.purseDescription = getIntent().getStringExtra("Description"); // purse Description
        this.purseBalance = getIntent().getStringExtra("Balance"); // purse Balance

        //Get Transactions for this Purse
        filterTransactionforSpecificPurse(Integer.parseInt(purseID));

    }

    /**
     * This Method will do a call to Database in order to get all User Transaction
     * and after that filter the transaction for this specific purse which id is being passed as parameter
     *
     * @param purseId
     */
    public void filterTransactionforSpecificPurse(final int purseId) {

        final FirebaseUser user = firebaseAuth.getCurrentUser();
        // get the user transaction corresponding to this specific purse

        // Get All user Transactions
        MyApolloClient.getMyApolloClient().query(
                GetAllUserTransactionsOrderByDateQuery.builder().user_id(user.getUid())
                        .build()).enqueue(new ApolloCall.Callback<GetAllUserTransactionsOrderByDateQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<GetAllUserTransactionsOrderByDateQuery.Data> response) {
                userTransactions.addAll(response.data().userTransactions());

                // filtered results
                for (int i = 0; i < userTransactions.size(); i++) {
                    if (userTransactions.get(i).purse_id == purseId) {

                        if (!userTransactions.get(i).label().contains("EcoExTAsMiGaCaEd2019dub")) {
                            userTransactionsFilteredByPurse.add(userTransactions.get(i));
                        }

                    }
                }

                // load the Corresponding Fragment
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_details,
                        new DetailsPurseFragment(userTransactionsFilteredByPurse, purseName, purseDescription,
                                purseBalance, Integer.parseInt(purseID))).commit();
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {

            }
        });


    }

}
