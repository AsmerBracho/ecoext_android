package ecoext.com.ecoext.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import ecoext.com.ecoext.CreatePurse;
import ecoext.com.ecoext.GetAllUserTransactionsOrderByDateQuery;
import ecoext.com.ecoext.GetUserTransactionsQuery;
import ecoext.com.ecoext.records.ItemTransactionAdapterWithReciclerView;
import ecoext.com.ecoext.R;

/**
 * Home Fragment contains information presented on the home screen
 * this is a fragment since will allow the navigation in the same activity (Main Activity)
 * only by changing and inflating the specific content
 */
public class HomeFragment extends Fragment {

    private HomePurseAdapter homePurseAdapter; // Adapter to set the purses in Home Fragment
    private ArrayList<GetUserTransactionsQuery.Purse> purses; // list of purses
    private ArrayList<GetAllUserTransactionsOrderByDateQuery.UserTransaction> userTransactions; // list of User Transactions
    private ArrayList<GetAllUserTransactionsOrderByDateQuery.UserTransaction> userTransactionsFiltered; // taken values no wanted

    // Views
    private RecyclerView listOfPurses;
    private RecyclerView latestsRecords;
    private LinearLayout createPurseButton;

    private ItemTransactionAdapterWithReciclerView latestRecordAdapter; // Adapter for latest Records
    private ArrayList<GetAllUserTransactionsOrderByDateQuery.UserTransaction> latest; // list with latest Records

    private int intLatest;
    private int latestSize = 3; // number of record to show in latest
    private int accountId;

    // Default Constructor for Home Fragment
    public HomeFragment() {
    }

    /**
     * Constructor for Home Fragments
     *
     * @param purses a list of purses
     * @param userTransactions a list of transactions
     * @param accountId user account id
     */
    @SuppressLint("ValidFragment")
    public HomeFragment(ArrayList<GetUserTransactionsQuery.Purse> purses,
                        ArrayList<GetAllUserTransactionsOrderByDateQuery.UserTransaction> userTransactions,
                        int accountId) {

        // assign parameters to global variables
        this.accountId = accountId;
        this.userTransactions = userTransactions;
        this.purses = purses;

        userTransactionsFiltered = new ArrayList<>();

        // checked if the record containts the given salt
        // if that's the case it means it is the first record of each purse which is the balance
        // so in that case exclude that from the list
        for (int j = 0; j < userTransactions.size(); j++) {
            if (!userTransactions.get(j).label().contains("EcoExTAsMiGaCaEd2019dub")) {
                userTransactionsFiltered.add(userTransactions.get(j));
            }
        }

        // get a list with the latest records to pass to the adapter
        latest = new ArrayList<>();

        // Handle the latest records
        if (userTransactionsFiltered.size() < latestSize) {
            intLatest = userTransactionsFiltered.size();
        } else {
            intLatest = latestSize;
        }

        for (int i = 0; i < intLatest; i++) {
            latest.add(userTransactionsFiltered.get(i));
        }
    }


    /**
     * Method onCreateView  is call every time the application loads and inside we define
     * and initialize our main variables and instances
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return a view
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        // create an adapter for purses
        homePurseAdapter = new HomePurseAdapter(this.getContext(), purses);

        if (intLatest != 0) {
            TextView notFound = view.findViewById(R.id.text_not_found);
            notFound.setVisibility(View.GONE);
        }

        latestRecordAdapter = new ItemTransactionAdapterWithReciclerView(this.getContext(), latest);
        initRecyclerView(view);
        pieChart(view);

        createPurseButton = view.findViewById(R.id.create_new_purse_button);
        createPurseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newPurse = new Intent(getContext(), CreatePurse.class);
                newPurse.putExtra("accountId", Integer.toString(accountId));
                startActivity(newPurse);
            }
        });

        return view;
    }

    /**
     * Method that initialize the views for the different components
     * in the Home Screen
     *
     * @param view a view
     */
    public void initRecyclerView(View view) {
        // Records views
        latestsRecords = view.findViewById(R.id.home_latest_records);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        latestsRecords.setLayoutManager(linearLayoutManager);
        latestsRecords.setAdapter(latestRecordAdapter);

        // purses views
        listOfPurses = view.findViewById(R.id.home_list_purses_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
        listOfPurses.setLayoutManager(layoutManager);
        listOfPurses.setAdapter(homePurseAdapter);
    }

    /**
     * Method that initialize the view and handle the necessary data to put in
     * the graph (pie chart)
     *
     * @param view
     */
    public void pieChart(View view) {
        PieChart pieChart = (PieChart) view.findViewById(R.id.piechart);
        pieChart.setUsePercentValues(true);

        ArrayList<Entry> values = new ArrayList<Entry>();
        // Here we calculate all the sum of expenses and incomes
        float income = 0;
        float expenses = 0;
        for (GetAllUserTransactionsOrderByDateQuery.UserTransaction ut : userTransactions) {
            // Here we calculate the total then we check if it is positive or negative
            double total = 0;
            for (int i = 0; i < ut.items().size(); i++) {
                total += ut.items().get(i).price();
            }
            if (total > 0) {
                income += total;
            } else {
                expenses += total;
            }
        }
        values.add(new Entry(income, 0));
        values.add(new Entry(expenses, 1));
        PieDataSet dataSet = new PieDataSet(values, "");
        int[] colors = {
                Color.rgb(100, 241, 105), Color.rgb(228, 85, 88),
        };
        dataSet.setColors(ColorTemplate.createColors(colors));
        ArrayList<String> label = new ArrayList<>();
        label.add("Income");
        label.add("Expenses");
        PieData data = new PieData(label, dataSet);
        data.setValueFormatter(new PercentFormatter());

        // setData
        pieChart.setData(data);
        pieChart.setDescription("Transactions Balance");
        pieChart.animateX(1000);
    }
}

