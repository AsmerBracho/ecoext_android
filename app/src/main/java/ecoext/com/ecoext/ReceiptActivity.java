package ecoext.com.ecoext;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ReceiptActivity extends AppCompatActivity {

    ItemReceiptAdapter itemReceiptAdapter;
    ListView listOfItems;
    ArrayList<CreateItem> myItems = new ArrayList<CreateItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        listOfItems = findViewById(R.id.items_receipt);


        /**
         * Create a Record here for te Item
         */

        CreateItem i1 = new CreateItem(2, "Cherry Tomatos", 2.35, 4.70);
        CreateItem i2 = new CreateItem(1, "Milk", 1.69, 1.69);
        CreateItem i3 = new CreateItem(1, "Bag Mix Peppers", 4.15, 4.15);

        myItems.add(i1);
        myItems.add(i2);
        myItems.add(i3);
        myItems.add(i3);
        myItems.add(i3);
        myItems.add(i3);
        myItems.add(i3);
        myItems.add(i3);
        myItems.add(i3);
        myItems.add(i3);
        myItems.add(i3);
        myItems.add(i3);
        myItems.add(i3);

        itemReceiptAdapter = new ItemReceiptAdapter(this, myItems);
        listOfItems.setAdapter(itemReceiptAdapter);

        // call method to allow scroll of ListView inside ScrollView
        setListViewHeightBasedOnChildren(listOfItems);

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
    }


}
