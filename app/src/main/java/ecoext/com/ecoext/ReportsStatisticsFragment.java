package ecoext.com.ecoext;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

/**
 * ReportsStatisticsFragment contains the Graphs to be displayed in the Reports Page
 * this is a fragment since will allow the navigation in the same activity (Main Activity)
 * only by changing and inflating the specific content
 */
public class ReportsStatisticsFragment extends Fragment {

    private ArrayList<GetAllUserTransactionsOrderByDateQuery.UserTransaction> userTransactions; // list of transactions

    // Default Constructor
    public ReportsStatisticsFragment() {
    }

    /**
     * Constructor ReportsStatisticsFragment
     *
     * @param userTransactions a list of transactions
     */
    @SuppressLint("ValidFragment")
    public ReportsStatisticsFragment(ArrayList<GetAllUserTransactionsOrderByDateQuery.UserTransaction> userTransactions) {
        this.userTransactions = userTransactions;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reports_statistics, container, false);

        barChart(view);
        pieChart(view);

        return view;
    }

    /**
     * Method BarChart
     * Method that initialize the view and handle the necessary data to put in
     * the graph (bar chart)
     *
     * @param view
     */
    public void barChart(View view) {
        BarChart barChart = view.findViewById(R.id.graph);

        //// create BarEntry for incomes and expenses
        ArrayList<BarEntry> incomes = new ArrayList<>();
        ArrayList<BarEntry> expenses = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<String>();

        int i = 0;
        for (GetAllUserTransactionsOrderByDateQuery.UserTransaction ut : userTransactions) {
            labels.add(String.valueOf(i + 1));
            // Here we calculate the total then we check if it is positive or negative
            double total = 0;
            for (int j = 0; j < ut.items().size(); j++) {
                total += ut.items().get(j).price();
            }
            if (total > 0) {
                incomes.add(new BarEntry((float) total, i));
            } else {

                expenses.add(new BarEntry((float) Math.abs(total), i));
            }
            i++;
        }

        // creating dataset for incomes
        BarDataSet barIncomes = new BarDataSet(incomes, "Income");
        barIncomes.setColor(Color.parseColor("#64f169"));

        BarDataSet barExpenses = new BarDataSet(expenses, "Expense");
        barExpenses.setColor(Color.parseColor("#e45558"));

        //combined Data Set
        ArrayList<BarDataSet> dataSets = new ArrayList<>();  // combined all dataset into an arraylist
        dataSets.add(barIncomes);
        dataSets.add(barExpenses);

        BarData data = new BarData(labels, dataSets);
        barChart.setDescription("Transactions");
        barChart.setData(data);
        barChart.animateY(1000);

    }

    /**
     * Method that initialize the view and handle the necessary data to put in
     * the graph (pie chart)
     *
     * @param view
     */
    public void pieChart(View view) {
        PieChart pieChart = (PieChart) view.findViewById(R.id.piechart2);
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
