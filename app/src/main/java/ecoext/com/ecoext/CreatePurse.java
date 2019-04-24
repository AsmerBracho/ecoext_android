package ecoext.com.ecoext;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import org.jetbrains.annotations.NotNull;

public class CreatePurse extends AppCompatActivity {

    private String TAG = "CreatePurse";
    private TextInputLayout name;
    private TextInputLayout description;
    private TextInputLayout initialAmount;
    private int accountId;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_purse);

        // Init Views
        name = findViewById(R.id.text_input_layout_name);
        description = findViewById(R.id.text_input_layout_description);
        initialAmount = findViewById(R.id.text_input_layout_amount);

        // Get account Id from intent
        String i = getIntent().getStringExtra("accountId");
        accountId = Integer.parseInt(i);
    }

    private boolean validateName() {
        String input = name.getEditText().getText().toString().trim();
        if (input.isEmpty()) {
            name.setError("Name can't be empty");
            return false;
        } else {
            name.setError(null);
            return true;
        }
    }

//    private boolean validateAmount() {
//        String input = initialAmount.getEditText().getText().toString().trim();
//        if (input.isEmpty()) {
//            initialAmount.setError("amount can't be empty");
//            return false;
//        } else {
//            initialAmount.setError(null);
//            return true;
//        }
//    }

    public void createPurseButton(View view) {
        if (!validateName()) {
            return;
        } else {
            // get Info to send to Database
            final String purseName = name.getEditText().getText().toString().trim();
            String purseDescription = description.getEditText().getText().toString().trim();
            final String amount = initialAmount.getEditText().getText().toString().trim();

            MyApolloClient.getMyApolloClient().mutate(
                    CreatePurseMutation.builder().name(purseName)
                            .account_id(accountId)
                            .description(purseDescription).build()).enqueue(new ApolloCall.Callback<CreatePurseMutation.Data>() {
                @Override
                public void onResponse(@NotNull Response<CreatePurseMutation.Data> response) {
                    final int purseId = response.data().addPurse.purse_id();

                    if (amount == null || amount.isEmpty() || "0".equals(amount)) {
                        CreatePurse.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent load = new Intent(getApplicationContext(), CustomLoader.class);
                                load.putExtra("newOperation", "EcoExTCreatePurse");
                                load.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(load);

                            }
                        });
                        return;
                    } else {
                        final double initialAmount = Double.parseDouble(amount);
                        // Create a Transaction
                        MyApolloClient.getMyApolloClient().mutate(
                                AddTransactionMutation.builder()
                                        .name(purseName + "First Record-EcoExTAsMiGaCaEd2019dub")
                                        .amount(initialAmount)
                                        .build()).enqueue(new ApolloCall.Callback<AddTransactionMutation.Data>() {
                            @Override
                            public void onResponse(@NotNull Response<AddTransactionMutation.Data> response) {
                                MyApolloClient.getMyApolloClient().mutate(
                                        AddTransactionToPurseMutation.builder()
                                                .pid(purseId)
                                                .token(response.data().addTransaction().token_id)
                                                .build()).enqueue(new ApolloCall.Callback<AddTransactionToPurseMutation.Data>() {
                                    @Override
                                    public void onResponse(@NotNull Response<AddTransactionToPurseMutation.Data> response) {
                                        CreatePurse.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Intent load = new Intent(getApplicationContext(), CustomLoader.class);
                                                load.putExtra("newOperation", "EcoExTCreatePurse");
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

                @Override
                public void onFailure(@NotNull ApolloException e) {

                }

            });
        }
    }
}


