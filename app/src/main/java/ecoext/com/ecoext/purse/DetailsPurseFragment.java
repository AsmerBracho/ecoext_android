package ecoext.com.ecoext.purse;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;

import ecoext.com.ecoext.DeletePurseMutation;
import ecoext.com.ecoext.GetAllUserTransactionsOrderByDateQuery;
import ecoext.com.ecoext.R;
import ecoext.com.ecoext.general.CustomLoader;
import ecoext.com.ecoext.general.MyApolloClient;
import ecoext.com.ecoext.general.Utilities;
import ecoext.com.ecoext.records.ItemTransactionAdapterWithReciclerView;

/**
 * DetailPurseFragment contains information presented when clicking specific purses
 * this fragment is loaded after the activity that contains it (Purse Detail) has requested
 * the info to the database and store it in the app
 */
public class DetailsPurseFragment extends Fragment {

    private ArrayList<GetAllUserTransactionsOrderByDateQuery.UserTransaction> purseTransactions;
    private ItemTransactionAdapterWithReciclerView listRecordAdapter;
    private RecyclerView listRecordsRecyclerView;
    private Context context;

    // Views
    private TextView name;
    private TextView description;
    private TextView balance;
    private TextView initialAmount;
    private RelativeLayout background;
    private ImageView deletePurse;

    // Decimal Formater
    private static DecimalFormat df = new DecimalFormat("0.00");

    // Extras Variables
    private int purseId;
    private String purseName;
    private String purseDescription;
    private String purseBalance;
//    private String purseInitialAmount;

    // Default Constructor
    public DetailsPurseFragment() {}

    /**
     * Constructor for Details Purse Fragment
     * It creates an instance that contains all necessary information to display
     * the purse details
     *
     * @param purseTransactions list of transaction belong to the specific purse
     * @param purseName purse name
     * @param purseDescription purse description
     * @param purseBalance balance
     * @param purseId id
     */
    @SuppressLint("ValidFragment")
    public DetailsPurseFragment(ArrayList<GetAllUserTransactionsOrderByDateQuery.UserTransaction> purseTransactions,
                                String purseName, String purseDescription, String purseBalance, int purseId) {

        // set the parameter as our global variables
        this.purseTransactions = purseTransactions;
        this.purseName = purseName;
        this.purseDescription = purseDescription;
        this.purseBalance = purseBalance;
        this.purseId = purseId;
    }

    /**
     * Method onCreateView  is call every time this activity loads and inside we define
     * and initialize our main variables and instances
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.purse_details, container, false);

        context = getContext();
        listRecordAdapter = new ItemTransactionAdapterWithReciclerView(getContext(), purseTransactions);
        // init Views
        initViews(view);
        // set Up the Info
        setUpViewsInfo(purseName, purseDescription, purseBalance, "0");

        // If there are no record, show No Records else DO NOT show this message ==>
        if (purseTransactions.size() !=0) {
            TextView notFound = view.findViewById(R.id.purse_details_text_not_found);
            notFound.setVisibility(View.GONE);
        }

        // set delete purse alpha
        deletePurse.setAlpha(1f);
        // Click Listener
        deletePurse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("DELETE PURSE")
                        .setMessage("Are you sure you want to delete this Purse, All the record will be deleted with it")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MyApolloClient.getMyApolloClient().mutate(DeletePurseMutation.builder()
                                        .purseId(purseId).build()).enqueue(new ApolloCall.Callback<DeletePurseMutation.Data>() {
                                    @Override
                                    public void onResponse(@NotNull Response<DeletePurseMutation.Data> response) {
                                        Intent goLoader = new Intent(context, CustomLoader.class);
                                        goLoader.putExtra("newOperation", "EcoExTPurseDeleted");
                                        startActivity(goLoader);
                                    }

                                    @Override
                                    public void onFailure(@NotNull ApolloException e) {

                                    }
                                });
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Dismiss
                            }
                        })
                        .show();
            }
        });

        return view;
    }

    /**
     * Init Views Method
     * It initiate the different views
     * @param view
     */
    public void initViews(View view) {
        name = view.findViewById(R.id.purse_details_name);
        description = view.findViewById(R.id.purse_details_description);
        balance = view.findViewById(R.id.purse_details_balance);
//        initialAmount = view.findViewById(R.id.purse_details_initial_amount);
        background = view.findViewById(R.id.purse_details_background);
        deletePurse = view.findViewById(R.id.delete_purse);

        // Recycler View
        listRecordsRecyclerView = view.findViewById(R.id.purse_details_list_of_records);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        listRecordsRecyclerView.setLayoutManager(linearLayoutManager);
        listRecordsRecyclerView.setAdapter(listRecordAdapter);
    }

    /**
     * Set up Views
     *
     * It set up the Info  for the views
     *
     * @param name purse name
     * @param desc prse description
     * @param bal purse balance
     * @param initA purse initial amount
     */
    public void setUpViewsInfo(String name, String desc, String bal, String initA) {
        this.name.setText(name);
        this.description.setText(desc);
        this.balance.setText("Balance: €" + df.format(Double.parseDouble(bal)));
//        this.initialAmount.setText("€" + initA);
        this.background.setBackgroundColor(Color.parseColor(Utilities.setPurseColor(name)));
    }
}