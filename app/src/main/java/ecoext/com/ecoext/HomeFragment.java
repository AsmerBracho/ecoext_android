package ecoext.com.ecoext;

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

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomePurseAdapter homePurseAdapter;
    private ArrayList<GetUserTransactionsQuery.Purse> purses;
    private ArrayList<GetAllUserTransactionsOrderByDateQuery.UserTransaction> userTransactions;
    private ArrayList<GetAllUserTransactionsOrderByDateQuery.UserTransaction> userTransactionsFiltered;
    private RecyclerView listOfPurses;
    private RecyclerView latestsRecords;
    private LinearLayout createPurseButton;
    private ItemTransactionAdapterWithReciclerView latestRecordAdapter;
    private int intLatest;
    private int latestSize = 3;
    private int accountId;

    public HomeFragment() {

    }

    @SuppressLint("ValidFragment")
    public HomeFragment(ArrayList<GetUserTransactionsQuery.Purse> purses,
                        ArrayList<GetAllUserTransactionsOrderByDateQuery.UserTransaction> userTransactions,
                        int accountId) {

        this.accountId = accountId;
        this.userTransactions = userTransactions;
        this.purses = purses;

        userTransactionsFiltered = new ArrayList<>();

        for (int j = 0; j < userTransactions.size(); j++) {
            if (!userTransactions.get(j).label().contains("EcoExTAsMiGaCaEd2019dub")) {
                userTransactionsFiltered.add(userTransactions.get(j));
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);

        homePurseAdapter = new HomePurseAdapter(this.getContext(), purses);

        // get a list with the latest records to pass to the adapter
        ArrayList<GetAllUserTransactionsOrderByDateQuery.UserTransaction> latest = new ArrayList<>();

        if (userTransactionsFiltered.size() < latestSize) {
            intLatest = userTransactionsFiltered.size();
        } else {
            intLatest = latestSize;
        }

        if (intLatest !=0) {
            TextView notFound = view.findViewById(R.id.text_not_found);
            notFound.setVisibility(View.GONE);
        }

        for (int i = 0; i < intLatest ; i++) {
            latest.add(userTransactionsFiltered.get(i));
        }
        latestRecordAdapter = new ItemTransactionAdapterWithReciclerView(this.getContext(), latest);

        initReciclerView(view);

        barChart(view);
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

    public void initReciclerView(View view) {
        latestsRecords = view.findViewById(R.id.home_latest_records);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        latestsRecords.setLayoutManager(linearLayoutManager);
        latestsRecords.setAdapter(latestRecordAdapter);

        listOfPurses = view.findViewById(R.id.home_list_purses_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
        listOfPurses.setLayoutManager(layoutManager);
        listOfPurses.setAdapter(homePurseAdapter);
    }

    // BarGraph
    public void barChart(View view) {
        BarChart barChart =  view.findViewById(R.id.graph);

        //// create BarEntry for incomes and expenses
        ArrayList<BarEntry> incomes = new ArrayList<>();
        ArrayList<BarEntry> expenses = new ArrayList<>();

        // create BarEntry for Bar Group 1
        incomes.add(new BarEntry(8f, 0));
        incomes.add(new BarEntry(2f, 1));
        incomes.add(new BarEntry(5f, 2));
        incomes.add(new BarEntry(20f, 3));
        incomes.add(new BarEntry(15f, 4));
        incomes.add(new BarEntry(19f, 5));

        // create BarEntry for Bar Group 2
        ArrayList<BarEntry> bargroup2 = new ArrayList<>();
        bargroup2.add(new BarEntry(6f, 0));
        bargroup2.add(new BarEntry(10f, 1));
        bargroup2.add(new BarEntry(5f, 2));
        bargroup2.add(new BarEntry(25f, 3));
        bargroup2.add(new BarEntry(4f, 4));
        bargroup2.add(new BarEntry(17f, 5));

        // creating dataset for Bar Group1
        BarDataSet barDataSet1 = new BarDataSet(incomes, "Bar Group 1");
        barDataSet1.setColor(Color.parseColor("#64f169"));

        BarDataSet barDataSet2 = new BarDataSet(bargroup2, "Bar Group 2");
        barDataSet2.setColor(Color.parseColor("#e45558"));

        // Labels
        ArrayList<String> labels = new ArrayList<String>();
        labels.add("1");
        labels.add("2");
        labels.add("3");
        labels.add("4");
        labels.add("5");
        labels.add("6");
        labels.add("7");
        labels.add("8");
        labels.add("9");
        labels.add("10");
        labels.add("11");
        labels.add("12");


        //combined Data Set
        ArrayList<BarDataSet> dataSets = new ArrayList<>();  // combined all dataset into an arraylist
        dataSets.add(barDataSet1);
        dataSets.add(barDataSet2);

        BarData data = new BarData(labels, dataSets);
        barChart.setData(data);
        barChart.animateY(1000);

    }

    public void pieChart(View view) {
        PieChart pieChart = (PieChart) view.findViewById(R.id.piechart);
        pieChart.setUsePercentValues(true);


        // IMPORTANT: In a PieChart, no values (Entry) should have the same
        // xIndex (even if from different DataSets), since no values can be
        // drawn above each other.
        ArrayList<Entry> yvalues = new ArrayList<Entry>();
        yvalues.add(new Entry(8f, 0));
        yvalues.add(new Entry(15f, 1));
        yvalues.add(new Entry(12f, 2));
        yvalues.add(new Entry(25f, 3));
        yvalues.add(new Entry(23f, 4));
        yvalues.add(new Entry(17f, 5));

        PieDataSet dataSet = new PieDataSet(yvalues, "Election Results");

        ArrayList<String> xVals = new ArrayList<String>();

        xVals.add("January");
        xVals.add("February");
        xVals.add("March");
        xVals.add("April");
        xVals.add("May");
        xVals.add("June");

        PieData data = new PieData(xVals, dataSet);

        data.setValueFormatter(new PercentFormatter());

        // setData
        pieChart.setData(data);
        pieChart.animateX(1000);
    }
}
