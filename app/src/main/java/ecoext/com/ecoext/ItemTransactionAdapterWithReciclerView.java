package ecoext.com.ecoext;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
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
    private ArrayList<CreateTransaction> listOfRecords;
    private ArrayList<CreateTransaction> listOfRecordsFull;

    public ItemTransactionAdapterWithReciclerView(Context c, ArrayList<CreateTransaction> listOfRecords) {
        this.context = c;
        this.listOfRecords = listOfRecords;
        // create a copy of records in order to use with filters
        this.listOfRecordsFull = new ArrayList<>(listOfRecords);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_records, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final CreateTransaction transaction = listOfRecords.get(position);

        //get Url logo
        String url = transaction.getLogo();

        // Stylize and handle error in the picture using glide
        Glide.with(context).load(url)
                .error(R.drawable.error_logo)
                .override(40, 40)
                .bitmapTransform(new ecoext.com.ecoext.RoundedCornersTransformation(context, sCorner, sMargin))
                .into(holder.logoImageView);

        holder.titleTextView.setText(transaction.getTitle());
        holder.descriptionTextView.setText(transaction.getPurse());
        holder.dateTextView.setText(transaction.getDate());
        holder.priceTextView.setText(currance + Double.toString(transaction.getPrice()));

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

    @Override
    public int getItemCount() {
        return listOfRecords.size();
    }

    @Override
    public Filter getFilter() {
        return recordsFilter;
    }

    private Filter recordsFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<CreateTransaction> filterList = new ArrayList<>();

            // if there is not input in search box then return the whole list
            if (charSequence == null || charSequence.length() == 0) {
                filterList.addAll(listOfRecordsFull);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (CreateTransaction transaction: listOfRecordsFull) {
                    if (transaction.getTitle().toLowerCase().contains(filterPattern) ||
                            transaction.getDate().contains(filterPattern)) {
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
            listOfRecords.clear();
            listOfRecords.addAll((Collection<? extends CreateTransaction>) filterResults.values);
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
