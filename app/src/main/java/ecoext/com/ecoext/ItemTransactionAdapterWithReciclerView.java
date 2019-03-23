package ecoext.com.ecoext;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    // Create a global listOfRecords that will hold the records from database
    private ArrayList<GetUserTransactionsQuery.Purse> listOfPurses;

    private ArrayList<GetUserTransactionsQuery.Transaction> listOfTransactions = new ArrayList<>();
    private ArrayList<GetUserTransactionsQuery.Transaction> listOfTransactionsFull = new ArrayList<>();

    public ItemTransactionAdapterWithReciclerView(Context c, ArrayList<GetUserTransactionsQuery.Purse> listOfPurses) {
        this.context = c;
        this.listOfPurses = listOfPurses;
        // create a copy of records in order to use with filters
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_records, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final GetUserTransactionsQuery.Purse purse = listOfPurses.get(position);

        // put the transaction in a single list of transaction for filters
        listOfTransactions.addAll(purse.transaction());
        listOfTransactionsFull = listOfTransactions;

        Log.d(TAG, "outSideMy: " + purse.name());

        for (int i = 0; i < purse.transaction().size(); i ++) {
            final GetUserTransactionsQuery.Transaction transaction = listOfTransactions.get(i);

            Log.d(TAG, "inSideMy: " + transaction.label());
            //get Url logo
            //String url = transaction.getLogo();
        /*
        // Stylize and handle error in the picture using glide
        Glide.with(context).load(url)
                .error(R.drawable.error_logo)
                .override(40, 40)
                .bitmapTransform(new ecoext.com.ecoext.RoundedCornersTransformation(context, sCorner, sMargin))
                .into(holder.logoImageView);
        */

            holder.titleTextView.setText(transaction.label());
            holder.descriptionTextView.setText(purse.name());
            holder.dateTextView.setText(transaction.date());
            holder.priceTextView.setText(currance + "23");

            holder.parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(context,listOfRecords.get(position).getTitle(), Toast.LENGTH_SHORT).show();
                    Intent showReceipt = new Intent(context, ReceiptActivity.class);

                    //put extras to pass to next activity and know with receipt are we currently clicking
                    context.startActivity(showReceipt);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listOfPurses.size();
    }

    @Override
    public Filter getFilter() {
        return recordsFilter;
    }

    private Filter recordsFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            return null;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            ArrayList<GetUserTransactionsQuery.Transaction> filterList = new ArrayList<>();


            // if there is not input in search box then return the whole list
            if (charSequence == null || charSequence.length() == 0) {
                filterList.addAll(listOfTransactionsFull);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (GetUserTransactionsQuery.Transaction transaction: listOfTransactions) {
                    if (transaction.label().toLowerCase().contains(filterPattern) ||
                            transaction.date.contains(filterPattern)) {
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
        ImageView logoImageView;
        TextView titleTextView;
        TextView descriptionTextView;
        TextView priceTextView;
        TextView dateTextView;
        android.support.v7.widget.CardView parentLayout;

        public ViewHolder(View v) {
            super(v);
            logoImageView = v.findViewById(R.id.recordLogo);
            titleTextView = v.findViewById(R.id.recordTitle);
            descriptionTextView = v.findViewById(R.id.recordDescription);
            priceTextView = v.findViewById(R.id.recordPrice);
            dateTextView = v.findViewById(R.id.recordDate);
            parentLayout = v.findViewById(R.id.parent_layout);
        }
    }
}
