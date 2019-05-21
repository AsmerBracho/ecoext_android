package ecoext.com.ecoext.receipt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.text.DecimalFormat;
import java.util.ArrayList;

import ecoext.com.ecoext.R;
import ecoext.com.ecoext.receipt.Item;
import ecoext.com.ecoext.receipt.ItemReceiptAdapter;

/**
 * Class Receipt Activity
 * Activity that renders a Receipt
 */
public class ReceiptActivity extends AppCompatActivity {

    // Global Variables
    private ItemReceiptAdapter itemReceiptAdapter;

    private ListView listOfItems; // list of items

    private ArrayList<Item> items; // array list with object Items
    private String currance = "€"; // currency to be displayed
    private DecimalFormat df = new DecimalFormat("0.00");


    /**
     * Method onCreate  is call every time this activity loads and inside we define
     * and initialize our main variables and instances
     * this is similar as what a constructor does
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        // get the views
        listOfItems = findViewById(R.id.items_receipt);
        TextView name = findViewById(R.id.userNameReceipt);
        TextView email = findViewById(R.id.userEmailReceipt);
        TextView total = findViewById(R.id.totalReceipt);
        TextView date = findViewById(R.id.dateReceipt);
        TextView number = findViewById(R.id.receiptNumber);
        TextView bLogo = findViewById(R.id.logoInsideReceipt);
        TextView headerTotal = findViewById(R.id.headerTotal);
        TextView tax = findViewById(R.id.taxReceipt);
        TextView subTotal = findViewById(R.id.subTotalReceipt);

        // get the list from the previous activity
        items = (ArrayList<Item>) getIntent().getExtras().getSerializable("listOfItems");
        String t = getIntent().getStringExtra("total");
        String num = getIntent().getStringExtra("number");
        String dat = getIntent().getStringExtra("date");
        String bL = getIntent().getStringExtra("name");
        String taX = getIntent().getStringExtra("tax");

        //set the views
        name.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        total.setText(currance + df.format(Math.abs(Double.parseDouble(t))));
        date.setText(dat);
        bLogo.setText(bL);

        tax.setText(currance + df.format(Math.abs(Double.parseDouble(taX))));
        double subtot = Math.abs(Double.parseDouble(t) - Double.parseDouble(taX));
        subTotal.setText(currance + df.format(Math.abs(subtot)));
        headerTotal.setText(currance + df.format(Math.abs(Double.parseDouble(t))));

        // add leading zeros to receipt number
        String formatted = String.format("%05d", Integer.parseInt(num));
        number.setText("Receipt #" + formatted);

        itemReceiptAdapter = new ItemReceiptAdapter(this, items);
        listOfItems.setAdapter(itemReceiptAdapter);

        // call method to allow scroll of ListView inside ScrollView
        setListViewHeightBasedOnChildren(listOfItems);

        // Get ScrollView and scroll up to top in case the list is too big
        ScrollView scrollView = findViewById(R.id.scrollView3);
        scrollView.smoothScrollTo(0, 0);
    }

    /**
     * Method for Setting the Height of the ListView dynamically.
     * Hack to fix the issue of not showing all the items of the ListView
     * when placed inside a ScrollView
     * Contribution by https://stackoverflow.com/users/2660283/arshu
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.setSelection(0);
    }

}
