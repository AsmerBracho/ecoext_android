package ecoext.com.ecoext;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import ecoext.com.ecoext.general.CustomLoader;
import ecoext.com.ecoext.general.MyApolloClient;
import ecoext.com.ecoext.purse.PurseInAddNameItemAdapter;

/**
 * AddNameAndPurse Class
 * It is used to select name and description for a transaction as well as
 * the purse where you want to store it
 */
public class AddNameAndPurse extends AppCompatActivity {

    private static final String TAG = "AddNameAndPurse";
    private String currency = "€";

    //My Global Variables
    private PurseInAddNameItemAdapter purseAdapter;
    private ArrayList<String> purses;
    private ArrayList<Integer> purseId;
    private RecyclerView listOfPurses;
    private TextView amount;
    private Button ok;
    private TextInputLayout inputTransactionName;
    private String amountString;
    private int PurseId = -1;
    private ProgressDialog progressDialog;

    public int getPurseId() {
        return PurseId;
    }

    public void setPurseId(int purseId) {
        PurseId = purseId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_purse_and_name);

        // Add back button
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // progress Dialog
        progressDialog = new ProgressDialog(this);

        // Find Views
        amount = findViewById(R.id.amount_to_add);
        inputTransactionName = findViewById(R.id.input_transaction_name);

        // get the Extras
        ArrayList<String> p = getIntent().getStringArrayListExtra("purseNames");
        purses = new ArrayList<>(p);
        amountString = getIntent().getStringExtra("amount");
        ArrayList<Integer> pI = getIntent().getIntegerArrayListExtra("purseId");
        purseId = new ArrayList<>(pI);

        // Set Amount Field TextView
        amount.setText(currency + amountString);

        Log.d(TAG, "giveMethePurseAddName" + purses.size());

        purseAdapter = new PurseInAddNameItemAdapter(getApplicationContext(), this, purses, purseId);

        // call internal methods
        initReciclerView();

        ok = findViewById(R.id.okCreate);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setTitle("EcoExTing");
                progressDialog.setMessage("Creating Transaction");
                progressDialog.show();
                ok.setEnabled(false);
                confirmInput(view);
            }
        });
    }

    /**
     * Method initRecyclerViews
     * It initiates the views for the purse adapter
     */
    public void initReciclerView() {
        listOfPurses = findViewById(R.id.list_of_purses_names);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        listOfPurses.setLayoutManager(layoutManager);
        listOfPurses.setAdapter(purseAdapter);
    }

    /**
     * Validate Name
     * @return true or false whether the name is empty or not
     */
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

    /**
     * Validate Purse
     * It shows an error if a purse has NOT been selected
     * @return
     */
    private boolean validatePurse() {
        if (getPurseId() == -1) {
            Toast.makeText(this, "Please Select a Purse", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    /**
     * Confirm Input and send info to Database
     * I takes the parameters
     * @param v a view
     */
    public void confirmInput(View v) {
        Log.d(TAG, "SendingRecordSeePurseId" + getPurseId());
        if (!validateName() | !validatePurse()) {
            return;
        } else {
            Log.d(TAG, "SendingRecord" + " OK");
            // get Info to send to Database
            String name = inputTransactionName.getEditText().getText().toString().trim();

            // Write to DataBase
            MyApolloClient.getMyApolloClient().mutate(
                    AddTransactionMutation.builder()
                            .name(name)
                            .amount(Double.parseDouble(amountString))
                            .build()).enqueue(new ApolloCall.Callback<AddTransactionMutation.Data>() {
                @Override
                public void onResponse(@NotNull Response<AddTransactionMutation.Data> response) {
                    MyApolloClient.getMyApolloClient().mutate(
                            AddTransactionToPurseMutation.builder()
                                    .pid(getPurseId())
                                    .token(response.data().addTransaction().token_id)
                                    .build()).enqueue(new ApolloCall.Callback<AddTransactionToPurseMutation.Data>() {
                        @Override
                        public void onResponse(@NotNull Response<AddTransactionToPurseMutation.Data> response) {
//
                            AddNameAndPurse.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //Toast.makeText(AddNameAndPurse.this, "Record Created", Toast.LENGTH_SHORT).show();
                                    Intent load = new Intent(getApplicationContext(), CustomLoader.class);
                                    load.putExtra("newOperation", "EcoExTCreateTransaction");
                                    load.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(load);
                                }
                            });
                        }

                        @Override
                        public void onFailure(@NotNull ApolloException e) {

                        }
                    });
                }

                @Override
                public void onFailure(@NotNull ApolloException e) {

                }
            });
        }
    }
}
