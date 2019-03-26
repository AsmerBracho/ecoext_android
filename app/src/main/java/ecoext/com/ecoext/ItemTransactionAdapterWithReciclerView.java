package ecoext.com.ecoext;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static ecoext.com.ecoext.MainActivity.sCorner;
import static ecoext.com.ecoext.MainActivity.sMargin;

public class ItemTransactionAdapterWithReciclerView extends RecyclerView.Adapter<
        ItemTransactionAdapterWithReciclerView.ViewHolder> implements Filterable {

    /**
     * We will need to define the following global variables
     */
    private Context context;
    private static final String TAG = ItemTransactionAdapterWithReciclerView.class.getSimpleName();

    //define the currance
    private String currance = "€";
    private static DecimalFormat df = new DecimalFormat(".##");

    // Create a global listOfRecords that will hold the records from database
    private ArrayList<GetUserTransactionsQuery.Purse> listOfPurses;

    private ArrayList<GetUserTransactionsQuery.Transaction> listOfTransactions;
    private ArrayList<GetUserTransactionsQuery.Transaction> listOfTransactionsFull;

    private ArrayList<String> purseNames = new ArrayList<>();

    public ItemTransactionAdapterWithReciclerView(Context c, ArrayList<GetUserTransactionsQuery.Purse> listOfPurses,
                                                  ArrayList<GetUserTransactionsQuery.Transaction> transactions) {
        this.context = c;
        this.listOfPurses = listOfPurses;
        this.listOfTransactions = transactions;
        // create a copy of transaction for filters
        listOfTransactionsFull = new ArrayList<>(listOfTransactions);

        for (int j = 0; j < listOfPurses.size(); j++) {
            for (int i = 0; i < listOfPurses.get(j).transaction().size(); i++) {
                purseNames.add(listOfPurses.get(j).name());
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_records, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final GetUserTransactionsQuery.Transaction transaction = listOfTransactions.get(position);

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
        holder.descriptionTextView.setText(purseNames.get(position));

        final SimpleDateFormat format = new SimpleDateFormat("dd/MM/YYYY");
        final Date date = new Date(Long.parseLong(transaction.date()));
        holder.dateTextView.setText(format.format(date));

        // Loop the items to extract the price
        double total = 0;
        for (int i = 0; i < transaction.items().size(); i++) {
            total += transaction.items().get(i).price() * transaction.items().get(i).quantity();
        }
        holder.priceTextView.setText(currance + df.format(total));

        // to be pass as extra
        final double finalTotal = total;
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent showReceipt = new Intent(context, ReceiptActivity.class);
                ArrayList<Item> listOfItems = new ArrayList<>();
                for (int j = 0; j < transaction.items().size(); j++) {
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
                showReceipt.putExtra("total", Double.toString(finalTotal));
                showReceipt.putExtra("name", bLogo);
                context.startActivity(showReceipt);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listOfTransactions.size();
    }


    @Override
    public Filter getFilter() {
        return recordsFilter;
    }

    private Filter recordsFilter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            ArrayList<GetUserTransactionsQuery.Transaction> filterList = new ArrayList<>();

            // if there is not input in search box then return the whole list
            if (charSequence == null || charSequence.length() == 0) {
                filterList.addAll(listOfTransactionsFull);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (GetUserTransactionsQuery.Transaction transaction : listOfTransactionsFull) {

                    // parse the date
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/YYYY");
                    Date date = new Date(Long.parseLong(transaction.date()));

                    if (transaction.label().toLowerCase().contains(filterPattern) ||
                            (format.format(date)).contains(filterPattern)) {
                        filterList.add(transaction);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filterList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            listOfTransactions.clear();
            listOfTransactions.addAll((Collection<? extends GetUserTransactionsQuery.Transaction>) filterResults.values);
            notifyDataSetChanged();
        }
    };


    public class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * Create the Views and math the source with id that comes from the
         * Item_record layout
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
