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

import java.util.ArrayList;

public class ReceiptActivity extends AppCompatActivity {

    private static final String TAG = ItemTransactionAdapterWithReciclerView.class.getSimpleName();
    ItemReceiptAdapter itemReceiptAdapter;

    ListView listOfItems;

    ArrayList<Item> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        listOfItems = findViewById(R.id.items_receipt);

        /**
         * Create a Record here for te Item
         */

        items = (ArrayList<Item>) getIntent().getExtras().getSerializable("listOfItems");

        Log.d(TAG, "miguel" + items.get(0).getName());

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
