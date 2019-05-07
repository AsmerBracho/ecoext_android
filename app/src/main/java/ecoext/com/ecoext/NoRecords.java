package ecoext.com.ecoext;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import org.jetbrains.annotations.NotNull;


public class NoRecords extends AppCompatActivity {

    private Context context;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.no_records_availables);

        context = this;
        // Get the Extras
        final String purseId = getIntent().getStringExtra("PurseID");
        final String transactionID = getIntent().getStringExtra("TransactionID");

        Log.d("printing IDs", "theIDS" + purseId + transactionID);

        // get the view
        ImageView delete = findViewById(R.id.delete);

        // set listener
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(context)
                        .setTitle("DELETE TRANSACTION")
                        .setMessage("Are you sure you want to delete this transaction")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MyApolloClient.getMyApolloClient().mutate(DeleteTransactionMutation.builder()
                                        .purseId(Integer.parseInt(purseId))
                                        .transactionId(Integer.parseInt(transactionID)).build()).enqueue(new ApolloCall.Callback<DeleteTransactionMutation.Data>() {
                                    @Override
                                    public void onResponse(@NotNull Response<DeleteTransactionMutation.Data> response) {
                                        Intent goLoader = new Intent(context, CustomLoader.class);
                                        goLoader.putExtra("newOperation", "EcoExTTransactionDeleted");
                                        startActivity(goLoader);
                                    }

                                    @Override
                                    public void onFailure(@NotNull ApolloException e) {

                                    }
                                });
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Dismiss
                            }
                        })
                        .show();


            }
        });
    }

}
