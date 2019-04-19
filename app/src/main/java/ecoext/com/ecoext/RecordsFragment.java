package ecoext.com.ecoext;

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

public class RecordsFragment extends Fragment {

    /**
     * Variables needed
     */
    private RecyclerView listOfRecords;
    private ItemTransactionAdapterWithReciclerView itemTransactionAdapterWithReciclerView;
    private static final String TAG = "RecordFradment";
    private String currency = "€";
    //Variables for CalendarPicker
    private Calendar calendar;
    private DatePickerDialog datePickerDialog;
    private String dateForFilter = "";

    private TextView filterAccount;
    private TextView filterAccountClick;
    private TextView filterDate;
    private TextView filterDateClick;

    private TextView alertBackground;
    private TextView onFilters;
    private TextView onDate;
    private TextView onAccount;
    private ImageView cancelFilters;
    private TextView totalBalance;

    // array of purses
    private ArrayList<GetUserTransactionsQuery.Purse> purses;

    // array of transactions
    private ArrayList<GetUserTransactionsQuery.Transaction> transactions = new ArrayList<>();
    private double balance;
    DecimalFormat df = new DecimalFormat("0.00");

    public RecordsFragment() {

    }

    @SuppressLint("ValidFragment")
    public RecordsFragment(ArrayList<GetUserTransactionsQuery.Purse> purses) {
        this.purses = purses;
        for (int i = 0; i < purses.size(); i++) {
            transactions.addAll(purses.get(i).transaction());
        }

        for (int j = 0; j < transactions.size(); j++) {
            for (int k = 0; k < transactions.get(j).items().size(); k++) {
                double temp = transactions.get(j).items().get(k).price()
                        *transactions.get(j).items().get(k).quantity();
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
                this.getContext(), purses, transactions);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        listOfRecords.setLayoutManager(layoutManager);
        listOfRecords.setAdapter(itemTransactionAdapterWithReciclerView);

        /**
         * Set click Listener for the different option for filters
         * for each filter add the listener that call the specific action
         */

        filterAccount = view.findViewById(R.id.filterValue1);
        filterAccountClick = view.findViewById(R.id.filterAccountClick);
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
        totalBalance.setText("Σ " + currency + df.format(balance));
        // clear the filters when running for first time
        setFilters(0);

        filterAccountClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterAccount.setText("Clicked");
                onAccount.setText("Clicked");
                setFilters(1);
            }
        });

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

    // Set the visibility of the filter Alert
    public void setFilters(int i) {
        // 1 if we want to show
        if (i == 1) {
            alertBackground.setBackgroundColor(Color.parseColor("#556ce2"));
            onFilters.setVisibility(View.VISIBLE);
            onDate.setVisibility(View.VISIBLE);
            onAccount.setVisibility(View.VISIBLE);
            // 0 if we want to hide
        } else {
            filterAccount.setText("");
            // clear field that contains date filter
            filterDate.setText("");
            // clear the actual filter
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
