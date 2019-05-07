package ecoext.com.ecoext.records;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import ecoext.com.ecoext.GetAllUserTransactionsOrderByDateQuery;
import ecoext.com.ecoext.GetUserTransactionsQuery;
import ecoext.com.ecoext.R;

/**
 * Records Fragment contains information presented on the Records screen
 * this is a fragment since it allows allow the navigation in the same activity (Main Activity)
 * only by changing and inflating the specific content
 */
public class RecordsFragment extends Fragment {

    // Global Variables
    private RecyclerView listOfRecords;
    private ItemTransactionAdapterWithReciclerView itemTransactionAdapterWithReciclerView;
    private static final String TAG = "RecordFragment"; // Tag for testing
    private String currency = "€"; // currency for displaying in views

    // Variables for CalendarPicker
    private Calendar calendar;
    private DatePickerDialog datePickerDialog;
    private String dateForFilter = "";

    // Views for filters
    private TextView filterDate;
    private TextView filterDateClick;
    private TextView alertBackground;
    private TextView onFilters;
    private TextView onDate;
    private TextView onAccount;
    private ImageView cancelFilters;
    private TextView totalBalance;

    // list of user Transactions
    private ArrayList<GetAllUserTransactionsOrderByDateQuery.UserTransaction> userTransactions; // list of transaction
    private ArrayList<GetAllUserTransactionsOrderByDateQuery.UserTransaction> userTransactionsFiltered;

    // array of transactions
    private ArrayList<GetUserTransactionsQuery.Transaction> transactions = new ArrayList<>();
    private double balance;
    DecimalFormat df = new DecimalFormat("0.00");

    // Default Constructor
    public RecordsFragment() {
    }

    /**
     * Constructor for Records Fragment
     *
     * @param userTransactions a list of transactions
     */
    @SuppressLint("ValidFragment")
    public RecordsFragment(ArrayList<GetAllUserTransactionsOrderByDateQuery.UserTransaction> userTransactions) {
        this.userTransactions = userTransactions;
        this.userTransactionsFiltered = new ArrayList<>();

        for (int j = 0; j < userTransactions.size(); j++) {
            if (!userTransactions.get(j).label().contains("EcoExTAsMiGaCaEd2019dub")) {
                userTransactionsFiltered.add(userTransactions.get(j));
            }
            for (int k = 0; k < userTransactions.get(j).items().size(); k++) {
                double temp = userTransactions.get(j).items().get(k).price()
                        * userTransactions.get(j).items().get(k).quantity();
                balance += temp;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // set the option menu to be true (shows the search option menu)
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.records, container, false);
        listOfRecords = view.findViewById(R.id.listOfRecords);

        // create an instance of my recycler view Adapter
        itemTransactionAdapterWithReciclerView = new ItemTransactionAdapterWithReciclerView(
                this.getContext(), userTransactionsFiltered);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        listOfRecords.setLayoutManager(layoutManager);
        listOfRecords.setAdapter(itemTransactionAdapterWithReciclerView);

        // Set click Listener for the different option for filters
        // for each filter add the listener that call the specific action
        filterDate = view.findViewById(R.id.filterValue2);
        filterDateClick = view.findViewById(R.id.filterDateClick);

        // If filters are active then show the alert
        alertBackground = view.findViewById(R.id.backgroundAlert);
        onFilters = view.findViewById(R.id.onFilters);
        onDate = view.findViewById(R.id.onDate);
        onAccount = view.findViewById(R.id.onAccount);
        cancelFilters = view.findViewById(R.id.cancelFilters);
        totalBalance = view.findViewById(R.id.balance);
        //set the balance
        totalBalance.setText(currency + df.format(balance));
        // clear the filters when running for first time
        setFilters(0);

        filterDateClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callDatePicker();

            }
        });

        cancelFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFilters(0);
            }
        });
        return view;
    }


    /**
     * Handle the filter in the action search menu
     *
     * @param menu
     * @param inflater
     */

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.filter_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) searchItem.getActionView();

        // Change the IME to DONE rather that the default search
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                itemTransactionAdapterWithReciclerView.getFilter().filter(newText);
                return false;
            }
        });

    }

    /**
     * Method that call the Data Picker
     * to select date so the records can be filtered accordingly
     */
    public void callDatePicker() {
        calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int mDay, int mMonth, int mYear) {
                mMonth = mMonth + 1;
                String monther = Integer.toString(mMonth);
                if (monther.length() == 1) {
                    monther = "0" + monther;
                }
                dateForFilter = mYear + "/" + monther + "/" + mDay;
                filterDate.setText(dateForFilter);
                onDate.setText(dateForFilter);

                Log.d(TAG, "giveMeDate: " + dateForFilter);

                // apply filter for date
                itemTransactionAdapterWithReciclerView.getFilter().filter(dateForFilter);
                setFilters(1);
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    /**
     * Method setFilter
     * this method handle the filter banner Enable it or disable it
     *
     * @param i int
     */
    public void setFilters(int i) {
        // 1 if we want to show
        if (i == 1) {
            alertBackground.setBackgroundColor(Color.parseColor("#556ce2"));
            onFilters.setVisibility(View.VISIBLE);
            onDate.setVisibility(View.VISIBLE);
            onAccount.setVisibility(View.VISIBLE);
            // 0 if we want to hide
        } else {
            filterDate.setText("");
            itemTransactionAdapterWithReciclerView.getFilter().filter("");
            alertBackground.setBackgroundColor(Color.parseColor("#FFFFFF"));
            onFilters.setVisibility(View.GONE);
            onDate.setVisibility(View.GONE);
            onDate.setText("");
            onAccount.setVisibility(View.GONE);
            onAccount.setText("");
        }
    }
}
