package ecoext.com.ecoext;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class RecordsFragment extends Fragment {

    /**
     * Variables needed
     */
    private RecyclerView listOfRecords;
    ItemTransactionAdapterWithReciclerView itemTransactionAdapterWithReciclerView;

    private ArrayList<CreateTransaction> myRecords = new ArrayList<CreateTransaction>();

    //Variables for CalendarPicker
    Calendar calendar;
    DatePickerDialog datePickerDialog;
    String dateForFilter = "";

    TextView filterAccount;
    TextView filterAccountClick;
    TextView filterDate;
    TextView filterDateClick;

    TextView alertBackground;
    TextView onFilters;
    TextView onDate;
    TextView onAccount;
    ImageView cancelFilters;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // set the option menu to be true
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.records, container, false);
        listOfRecords = view.findViewById(R.id.listOfRecords);

        /**
         * HERE we are going to call database CONECTION AND populate the ListOfRecords
         *
         * SIMULATION NOW
         */

        // putting data in the Array

        CreateTransaction r1 = new CreateTransaction("https://pbs.twimg.com/profile_images/1083750510895742976/qXusMdTt_400x400.jpg", "This is a title", "Tesco the best supermarker",
                "07/12/18", 12.85);

        CreateTransaction r2 = new CreateTransaction("https://yt3.ggpht.com/a-/AAuE7mD1_8RTDAP3x93qrF-tbMtg8RbAMizHILb9_w=s900-mo-c-c0xffffffff-rj-k-no", "This is another title", "ALDI the best supermarker",
                "24/01/19", 22.10);

        CreateTransaction r3 = new CreateTransaction("url:/logo/2", "This is another title", "ALDI the best supermarker",
                "24/01/19", 22.10);

        CreateTransaction r4 = new CreateTransaction("url:/logo/2", "This is another title", "ALDI the best supermarker",
                "24/01/19", 22.10);

        CreateTransaction r5 = new CreateTransaction("url:/logo/2", "This is another title", "ALDI the best supermarker",
                "24/01/19", 22.10);

        CreateTransaction r6 = new CreateTransaction("url:/logo/2", "This is another title", "ALDI the best supermarker",
                "24/01/19", 22.10);


        CreateTransaction r7 = new CreateTransaction("url:/logo/2", "This is another title", "ALDI the best supermarker",
                "24/01/19", 22.10);

        myRecords.add(r1);
        myRecords.add(r2);
        myRecords.add(r3);
        myRecords.add(r3);
        myRecords.add(r2);
        myRecords.add(r1);
        myRecords.add(r3);
        myRecords.add(r2);
        myRecords.add(r1);
        myRecords.add(r1);
        myRecords.add(r2);
        myRecords.add(r3);
        myRecords.add(r3);
        myRecords.add(r2);
        myRecords.add(r1);
        myRecords.add(r2);
        myRecords.add(r3);
        myRecords.add(r3);
        myRecords.add(r2);

        // create an instance of my recycler view Adapter
       itemTransactionAdapterWithReciclerView = new ItemTransactionAdapterWithReciclerView(
                this.getContext(), myRecords);

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
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.filter_menu, menu);
        super.onCreateOptionsMenu(menu,inflater);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                itemTransactionAdapterWithReciclerView.getFilter().filter(s);
                return false;
            }
        });
    }

    public void callDatePicker() {
        calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int mDay, int mMonth, int mYear) {
                dateForFilter = mDay + "/" + mMonth + "/" + mYear;
                filterDate.setText(dateForFilter);
                onDate.setText(dateForFilter);
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
            filterDate.setText("");
            alertBackground.setBackgroundColor(Color.parseColor("#FFFFFF"));
            onFilters.setVisibility(View.GONE);
            onDate.setVisibility(View.GONE);
            onDate.setText("");
            onAccount.setVisibility(View.GONE);
            onAccount.setText("");
        }
    }
}
