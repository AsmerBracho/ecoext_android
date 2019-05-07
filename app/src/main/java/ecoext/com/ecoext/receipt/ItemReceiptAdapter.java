package ecoext.com.ecoext.receipt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import ecoext.com.ecoext.R;

/**
 * Class ItemReceiptAdapter
 * implementation of the Adapter for the Receipt activity
 */
public class ItemReceiptAdapter extends BaseAdapter {

    //define the currance
    private String currance = "€";
    private static DecimalFormat df = new DecimalFormat(".##");

    private LayoutInflater mInflator;
    private Context context;

    // list of items
    private ArrayList<Item> listOfItems;

    /**
     * Constructor of ItemReceiptAdapter
     *
     * @param c     context
     * @param items list of items
     */
    public ItemReceiptAdapter(Context c, ArrayList<Item> items) {
        mInflator = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // set up the parameter as our global variables
        this.listOfItems = items;
        this.context = c;
    }

    @Override
    public int getCount() {
        return listOfItems.size();
    }

    @Override
    public Object getItem(int position) {
        return listOfItems.get(position).getName();
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


            // Create the Views and math the source with id that comes from the
            // receipt_item layout

            viewHolder.qty = view.findViewById(R.id.qtyTextView);
            viewHolder.description = view.findViewById(R.id.descriptionTextView);
            viewHolder.unitPrice = view.findViewById(R.id.unitPriceTextView);
            viewHolder.totalPrice = view.findViewById(R.id.totalPriceTextView);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        //set the views
        viewHolder.qty.setText(Integer.toString(listOfItems.get(position).getQuantity()));
        viewHolder.description.setText(listOfItems.get(position).getName());
        viewHolder.unitPrice.setText(currance + Math.abs(listOfItems.get(position).getPrice()));
        // if there is not more that 1 item don't show the unit price
        if (listOfItems.get(position).getQuantity() == 1) {
            viewHolder.unitPrice.setVisibility(View.GONE);
        }
        double total = Math.abs((listOfItems.get(position).getQuantity())
                * listOfItems.get(position).getPrice());

        viewHolder.totalPrice.setText(currance + df.format(total));

        return view;
    }

    /**
     * Private Inner Class ViewHolder
     * It contains the list of views that our outer class uses
     */
    private static class ViewHolder {
        private TextView qty;
        private TextView description;
        private TextView unitPrice;
        private TextView totalPrice;
    }
}
