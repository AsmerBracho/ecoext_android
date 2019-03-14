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
        ViewHolder viewHolder;
        if (view == null) {
            mInflator = LayoutInflater.from(context);
            view = mInflator.inflate(R.layout.receipt_item, null);
            viewHolder = new ViewHolder();

            /**
             * Create the Views and math the source with id that comes from the
             * receipt_item layout
             */
            viewHolder.qty = view.findViewById(R.id.qtyTextView);
            viewHolder.description = view.findViewById(R.id.descriptionTextView);
            viewHolder.unitPrice = view.findViewById(R.id.unitPriceTextView);
            viewHolder.totalPrice = view.findViewById(R.id.totalPriceTextView);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        //set the views

        viewHolder.qty.setText(Integer.toString(listOfItems.get(position).getQty()));
        viewHolder.description.setText(listOfItems.get(position).getDescription());
        viewHolder.unitPrice.setText(currance + Double.toString(listOfItems.get(position).getUnitPrice()));
        // if there is not more that 1 item don't show the unit price
        if (listOfItems.get(position).getQty() == 1) {
            viewHolder.unitPrice.setVisibility(View.GONE);
        }
        viewHolder.totalPrice.setText(currance + Double.toString(listOfItems.get(position).getTotalprice()));

        return view;
    }

    private static class ViewHolder {
        TextView qty;
        TextView description;
        TextView unitPrice;
        TextView totalPrice;
    }
}
