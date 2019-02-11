package ecoext.com.ecoext;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ItemRecordsAdapter extends BaseAdapter {

    /**
     * We will need to define the following global variables
     */
    private LayoutInflater mInflator;

    // Create a global listOfRecords that will hold the records from database
    private ArrayList<CreateRecord> listOfRecords;

    private List<String> logos;
    private List<String> title;
    private List<String> description;
    private List<String> date;
    private List<Double> price;

    /**
     * Create the constructor which will take the parameters
     * @param c corresponding to the inflator service
     * @param listOfRecords the list of records to be shown
     */
    public ItemRecordsAdapter(Context c, ArrayList<CreateRecord> listOfRecords) {
        mInflator = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.listOfRecords = listOfRecords;

        // create and fill the list of elements with data
        logos = new ArrayList<String>();
        title = new ArrayList<String>();

        for (CreateRecord item: this.listOfRecords) {
            logos.add(item.getLogo());
            title.add(item.getTitle());
        }

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

        View v = mInflator.inflate(R.layout.item_records, null);
        TextView titleTextView = v.findViewById(R.id.recordTitle);

        titleTextView.setText(listOfRecords.get(position).getTitle());

        return v;
    }
}
