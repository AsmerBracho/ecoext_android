package ecoext.com.ecoext;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;

import static ecoext.com.ecoext.MainActivity.sCorner;
import static ecoext.com.ecoext.MainActivity.sMargin;

public class ItemTransactionAdapter extends BaseAdapter {

    /**
     * We will need to define the following global variables
     */
    private LayoutInflater mInflator;
    private Context context;

    // Create a global listOfRecords that will hold the records from database
    private ArrayList<CreateTransaction> listOfRecords;

    /**
     * Create the constructor which will take the parameters
     * @param c corresponding to the inflator service
     * @param listOfRecords the list of records to be shown
     */
    public ItemTransactionAdapter(Context c, ArrayList<CreateTransaction> listOfRecords) {
        mInflator = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.listOfRecords = listOfRecords;
        context = c;
    }

    @Override
    public int getCount() {
        return listOfRecords.size();
    }

    @Override
    public Object getItem(int position) {
        return listOfRecords.get(position).getTitle() ;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        // create a view that will allow us to access the resources
        View v = mInflator.inflate(R.layout.item_records, null);

        /**
         * Create the Views and math the source with id that comes from the
         * Item_record layout
         */
        ImageView logoImageView = v.findViewById(R.id.recordLogo);
        TextView titleTextView = v.findViewById(R.id.recordTitle);
        TextView descriptionTextView = v.findViewById(R.id.recordDescription);
        TextView priceTextView = v.findViewById(R.id.recordPrice);
        TextView dateTextView = v.findViewById(R.id.recordDate);

        //load logo with picasso
        String url = listOfRecords.get(position).getLogo();

        // Stylize and handle error in the picture using glide
        Glide.with(context).load(url)
                .error(R.drawable.error_logo)
                .override(60, 60)
                .bitmapTransform(new ecoext.com.ecoext.RoundedCornersTransformation(context, sCorner, sMargin))
                .into(logoImageView);
        //set the text view
        titleTextView.setText(listOfRecords.get(position).getTitle());
        descriptionTextView.setText(listOfRecords.get(position).getPurse());
        dateTextView.setText(listOfRecords.get(position).getDate());
        priceTextView.setText(Double.toString(listOfRecords.get(position).getPrice()));

        return v;
    }
}
