package ecoext.com.ecoext;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ReceiptActivity extends AppCompatActivity {

    ItemReceiptAdapter itemReceiptAdapter;

    ListView listOfItems;

    ArrayList<Item> items;
    private String currance = "€";

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
        subTotal.setText(currance + t);
        double tot = Double.parseDouble(t) + Double.parseDouble(taX);
        total.setText(currance + Double.toString(tot));
        date.setText(dat);
        bLogo.setText(bL);
        tax.setText(currance + taX);
        headerTotal.setText(currance + Double.toString(tot));

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

    /**** Method for Setting the Height of the ListView dynamically.
     **** Hack to fix the issue of not showing all the items of the ListView
     **** when placed inside a ScrollView
     **** Contribution by https://stackoverflow.com/users/2660283/arshu ****/
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
