package ecoext.com.ecoext;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DetailsPurseFragment extends Fragment {

    private ArrayList<GetAllUserTransactionsOrderByDateQuery.UserTransaction> purseTransactions;
    private ItemTransactionAdapterWithReciclerView listRecordAdapter;
    private RecyclerView listRecordsRecyclerView;

    // Views
    private TextView name;
    private TextView description;
    private TextView balance;
    private TextView initialAmount;
    private RelativeLayout background;

    // Extras Variables
    private String purseName;
    private String purseDescription;
    private String purseBalance;
    private String purseInitialAmount;


    // Default Constructor
    public DetailsPurseFragment() {}


    @SuppressLint("ValidFragment")
    public DetailsPurseFragment(ArrayList<GetAllUserTransactionsOrderByDateQuery.UserTransaction> purseTransactions,
                                String purseName, String purseDescription, String purseBalance) {

        this.purseTransactions = purseTransactions;
        this.purseName = purseName;
        this.purseDescription = purseDescription;
        this.purseBalance = purseBalance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.purse_details, container, false);

        listRecordAdapter = new ItemTransactionAdapterWithReciclerView(getContext(), purseTransactions);

        // init Views
        initViews(view);

        // set Up the Info
        setUpViewsInfo(purseName, purseDescription, purseBalance, "0");


        // If there are no record show No Records else DO NOT show this message ==>
        if (purseTransactions.size() !=0) {
            TextView notFound = view.findViewById(R.id.purse_details_text_not_found);
            notFound.setVisibility(View.GONE);
        }


        return view;
    }


    public void initViews(View view) {
        name = view.findViewById(R.id.purse_details_name);
        description = view.findViewById(R.id.purse_details_description);
        balance = view.findViewById(R.id.purse_details_balance);
        initialAmount = view.findViewById(R.id.purse_details_initial_amount);
        background = view.findViewById(R.id.purse_details_background);

        // Recycler View
        listRecordsRecyclerView = view.findViewById(R.id.purse_details_list_of_records);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        listRecordsRecyclerView.setLayoutManager(linearLayoutManager);
        listRecordsRecyclerView.setAdapter(listRecordAdapter);
    }

    public void setUpViewsInfo(String name, String desc, String bal, String initA) {
        this.name.setText(name);
        this.description.setText(desc);
        this.balance.setText("€" + bal);
        this.initialAmount.setText("€" + initA);
        this.background.setBackgroundColor(Color.parseColor(Utilities.setPurseColor(name)));
    }
}