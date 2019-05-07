package ecoext.com.ecoext.records;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import ecoext.com.ecoext.GetAllUserTransactionsOrderByDateQuery;
import ecoext.com.ecoext.receipt.Item;
import ecoext.com.ecoext.R;
import ecoext.com.ecoext.receipt.ReceiptActivity;

/**
 * Class ItemTransactionAdapterWithReciclerView
 * implementation of the Adapter for the records
 * This adapter bind and set what the the record view contains
 */
public class ItemTransactionAdapterWithReciclerView extends RecyclerView.Adapter<
        ItemTransactionAdapterWithReciclerView.ViewHolder> implements Filterable {

    // Global Variables
    private Context context;
    private static final String TAG = ItemTransactionAdapterWithReciclerView.class.getSimpleName();

    //define the currance
    private String currance = "€";
    private static DecimalFormat df = new DecimalFormat("0.00");

    private ArrayList<GetAllUserTransactionsOrderByDateQuery.UserTransaction> userTransactions;
    private ArrayList<GetAllUserTransactionsOrderByDateQuery.UserTransaction> userTransactionsFull;

    public ItemTransactionAdapterWithReciclerView(Context c, ArrayList<GetAllUserTransactionsOrderByDateQuery.UserTransaction>
            userTransactions) {

        // set parameter as our global variables
        this.context = c;
        this.userTransactions = userTransactions;
        userTransactionsFull = new ArrayList<>(userTransactions);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_records, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
//        final GetUserTransactionsQuery.Transaction transaction = listOfTransactions.get(position);
        final GetAllUserTransactionsOrderByDateQuery.UserTransaction transaction = userTransactions.get(position);

        Log.d(TAG, "inSideMy: " + transaction.label());

        String l = transaction.label().toUpperCase();
        final String bLogo = String.valueOf(l.charAt(0));
        holder.logo.setText(bLogo);

        Log.d(TAG, "chart: " + l.charAt(0));

        if (("A").equals(bLogo) || ("B").equals(bLogo) || ("C").equals(bLogo) || ("D").equals(bLogo) || ("E").equals(bLogo) || ("P").equals(bLogo)) {
            holder.logo.setBackground(ContextCompat.getDrawable(context, R.drawable.logo_background));
        } else if (("F").equals(bLogo) || ("G").equals(bLogo) || ("H").equals(bLogo) || ("I").equals(bLogo) || ("K").equals(bLogo) || ("Z").equals(bLogo)) {
            holder.logo.setBackground(ContextCompat.getDrawable(context, R.drawable.logo_background2));
        } else {
            holder.logo.setBackground(ContextCompat.getDrawable(context, R.drawable.logo_background3));
        }

        holder.titleTextView.setText(transaction.label());
        holder.descriptionTextView.setText(transaction.purses().get(0).name());

        final SimpleDateFormat format = new SimpleDateFormat("dd/MM/YYYY");
        final Date date = new Date(Long.parseLong(transaction.date()));
        holder.dateTextView.setText(format.format(date));

        // Loop the items to extract the price
        double total = 0;
        for (int i = 0; i < transaction.items().size(); i++) {
            total += transaction.items().get(i).price() * transaction.items().get(i).quantity();
        }

        holder.priceTextView.setText(currance + df.format(total));
        // Set Color Amount accordingly
        if (Double.toString(total).contains("-") == false) {
            holder.priceTextView.setTextColor(Color.parseColor("#32bf1f"));
        }

        // to be pass as extra
        final String finalTotal = df.format(total);
        final double[] totalTax = {0};
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if ("User-Input-EcoExT".equals(transaction.items().get(0).product())) {

                    Log.d("printing IDs Before", "theIDSBefore" + transaction.transaction_id() + transaction.purse_id());
                    // Show No Receipt Available
                    Intent goNoReceipt = new Intent(context, NoRecords.class);
                    goNoReceipt.putExtra("TransactionID", Integer.toString(transaction.transaction_id()));
                    goNoReceipt.putExtra("PurseID", Integer.toString(transaction.purse_id()));
                    context.startActivity(goNoReceipt);
                } else {
                    double tax = 0;
                    totalTax[0] = 0;
                    Intent showReceipt = new Intent(context, ReceiptActivity.class);
                    ArrayList<Item> listOfItems = new ArrayList<>();
                    for (int j = 0; j < transaction.items().size(); j++) {

                        tax = (transaction.items().get(j).quantity()
                                * transaction.items().get(j).price()
                                * transaction.items().get(j).tax()) / 100;

                        totalTax[0] += tax;
                        listOfItems.add(new Item(
                                transaction.items().get(j).transaction_id(),
                                transaction.items().get(j).product(),
                                transaction.items().get(j).price(),
                                transaction.items().get(j).quantity(),
                                transaction.items().get(j).tax()
                        ));
                    }
                    //put extras to pass to next activity and know with receipt are we currently clicking
                    showReceipt.putParcelableArrayListExtra("listOfItems", listOfItems);
                    showReceipt.putExtra("date", format.format(date));
                    showReceipt.putExtra("number", transaction.transaction_id().toString());
                    showReceipt.putExtra("total", finalTotal);
                    showReceipt.putExtra("name", bLogo);
                    showReceipt.putExtra("tax", (df.format(totalTax[0])));
                    context.startActivity(showReceipt);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return userTransactions.size();
    }


    @Override
    public Filter getFilter() {
        return recordsFilter;
    }

    private Filter recordsFilter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            ArrayList<GetAllUserTransactionsOrderByDateQuery.UserTransaction> filterList = new ArrayList<>();

            // if there is not input in search box then return the whole list
            if (charSequence == null || charSequence.length() == 0) {
                filterList.addAll(userTransactionsFull);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (GetAllUserTransactionsOrderByDateQuery.UserTransaction transaction : userTransactionsFull) {

                    // parse the date
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/YYYY");
                    Date date = new Date(Long.parseLong(transaction.date()));

                    if (transaction.label().toLowerCase().contains(filterPattern) ||
                            (format.format(date)).contains(filterPattern)) {
                        filterList.add(transaction);
                        Log.d(TAG, "giveMeDate2: " + filterPattern);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filterList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            userTransactions.clear();
            userTransactions.addAll((Collection<? extends GetAllUserTransactionsOrderByDateQuery.UserTransaction>) filterResults.values);
            notifyDataSetChanged();
        }
    };

    /**
     * Inner Class ViewHolder that initialize the views handle
     * for the ItemTransactionAdapter
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * Create the Views and math the source with id that comes from the
         * item_record layout
         */
        TextView logo;
        TextView titleTextView;
        TextView descriptionTextView;
        TextView priceTextView;
        TextView dateTextView;
        android.support.v7.widget.CardView parentLayout;

        public ViewHolder(View v) {
            super(v);
            logo = v.findViewById(R.id.recordLogo);
            titleTextView = v.findViewById(R.id.recordTitle);
            descriptionTextView = v.findViewById(R.id.recordDescription);
            priceTextView = v.findViewById(R.id.recordPrice);
            dateTextView = v.findViewById(R.id.recordDate);
            parentLayout = v.findViewById(R.id.parent_layout);
        }
    }
}
