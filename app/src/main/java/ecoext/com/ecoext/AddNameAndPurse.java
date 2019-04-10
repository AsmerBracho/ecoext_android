package ecoext.com.ecoext;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class AddNameAndPurse extends AppCompatActivity {

    private static final String TAG = "AddNameAndPurse";
    private String currency = "€";
    //My Global Variables
    private PurseInAddNameItemAdapter purseAdapter;
    private ArrayList<String> purses;
    private RecyclerView listOfPurses;
    private TextView amount;

    private TextInputLayout inputTransactionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_purse_and_name);

        // Find Views
        amount = findViewById(R.id.amount_to_add);
        inputTransactionName = findViewById(R.id.input_transaction_name);

        // get the Extras
        ArrayList<String> p = getIntent().getStringArrayListExtra("purseNames");
        String amountString = getIntent().getStringExtra("amount");
        purses = new ArrayList<>(p);

        // Set Amount Field TextView
        amount.setText(currency + amountString);

        Log.d(TAG, "giveMethePurse" + purses.size());

        ArrayList<String> pursesMok = new ArrayList<>();
        pursesMok.add("Airst One");
        pursesMok.add("Second One");
        pursesMok.add("Third One");
        pursesMok.add("Fouth One");
        pursesMok.add("Zifth One");

        purseAdapter = new PurseInAddNameItemAdapter(getApplicationContext(), pursesMok);

        // call internal methods
        initReciclerView();
    }

    public void initReciclerView() {
        listOfPurses = findViewById(R.id.list_of_purses_names);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        listOfPurses.setLayoutManager(layoutManager);
        listOfPurses.setAdapter(purseAdapter);
    }

    private boolean validateName() {
        String input = inputTransactionName.getEditText().getText().toString().trim();

        if (input.isEmpty()) {
            inputTransactionName.setError("Field can't be empty");
            return false;
        } else {
            inputTransactionName.setError(null);
            return true;
        }
    }

    public void confirmInput(View v) {
        if (!validateName()) {
            return;
        }

        // get Info to send to Database
        String name = inputTransactionName.getEditText().getText().toString().trim();
    }


}
