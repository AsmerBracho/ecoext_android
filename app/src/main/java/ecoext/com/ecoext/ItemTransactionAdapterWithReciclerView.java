package ecoext.com.ecoext;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ItemTransactionAdapterWithReciclerView extends RecyclerView.Adapter<
        ItemTransactionAdapterWithReciclerView.TransactionHolder> {

    /**
     * We will need to define the following global variables
     */
    private Context context;
    private static final String TAG = ItemTransactionAdapterWithReciclerView.class.getSimpleName();

    //define the currance
    private String currance = "€";

    // Create a global listOfRecords that will hold the records from database
    private ArrayList<CreateTransaction> listOfRecords;

    public ItemTransactionAdapterWithReciclerView(Context c, ArrayList<CreateTransaction> listOfRecords) {
        this.context = c;
        this.listOfRecords = listOfRecords;
    }

    @NonNull
    @Override
    public TransactionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_records, parent, false);
        return new TransactionHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionHolder holder, int position) {
        final CreateTransaction transaction = listOfRecords.get(position);

        holder.titleTextView.setText(transaction.getTitle());
        holder.descriptionTextView.setText(transaction.getPurse());
        holder.dateTextView.setText(transaction.getDate());
        holder.priceTextView.setText(Double.toString(transaction.getPrice()));
    }

    @Override
    public int getItemCount() {
        return listOfRecords.size();
    }

    public class TransactionHolder extends RecyclerView.ViewHolder {

        /**
         * Create the Views and math the source with id that comes from the
         * Item_record layout
         */
        ImageView logoImageView;
        TextView titleTextView;
        TextView descriptionTextView;
        TextView priceTextView;
        TextView dateTextView;

        public TransactionHolder(View v) {
            super(v);
            logoImageView = v.findViewById(R.id.recordLogo);
            titleTextView = v.findViewById(R.id.recordTitle);
            descriptionTextView = v.findViewById(R.id.recordDescription);
            priceTextView = v.findViewById(R.id.recordPrice);
            dateTextView = v.findViewById(R.id.recordDate);
        }
    }
}
