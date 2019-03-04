package ecoext.com.ecoext;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class ItemReceiptAdapter extends BaseAdapter {

    //define the currance
    private String currance = "€";

    private LayoutInflater mInflator;
    private Context context;

    // list of items
    ArrayList<CreateItem> listOfItems;

    public ItemReceiptAdapter(Context c, ArrayList<CreateItem> items) {
        mInflator = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.listOfItems = items;
        this.context = c;
    }

    @Override
    public int getCount() {
        return listOfItems.size();
    }

    @Override
    public Object getItem(int position) {
        return listOfItems.get(position).getDescription();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        View v = mInflator.inflate(R.layout.receipt_item, null);

        /**
         * Create the Views and math the source with id that comes from the
         * receipt_item layout
         */

        TextView qty = v.findViewById(R.id.qtyTextView);
        TextView description = v.findViewById(R.id.descriptionTextView);
        TextView unitPrice = v.findViewById(R.id.unitPriceTextView);
        TextView totalPrice = v.findViewById(R.id.totalPriceTextView);

        //set the views

        qty.setText(Integer.toString(listOfItems.get(position).getQty()));
        description.setText(listOfItems.get(position).getDescription());
        unitPrice.setText(currance + Double.toString(listOfItems.get(position).getUnitPrice()));
        if (listOfItems.get(position).getQty() == 1) {
            unitPrice.setVisibility(View.GONE);
        }
        totalPrice.setText(currance + Double.toString(listOfItems.get(position).getTotalprice()));

        return v;
    }
}
