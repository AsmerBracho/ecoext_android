package ecoext.com.ecoext;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class RecordsFragment extends Fragment {

    ItemTransactionAdapter itemTransactionAdapter;
    ListView listOfRecords;
    ArrayList<CreateTransaction> myRecords = new ArrayList<CreateTransaction>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.records, container, false);

        listOfRecords = view.findViewById(R.id.listOfRecords);

        /**
         * HERE we are going to call database CONECTION AND populate the ListOfRecords
         *
         * SIMULATION NOW
         */

        // putting data in the Array

        CreateTransaction r1 = new CreateTransaction("https://pbs.twimg.com/profile_images/1083750510895742976/qXusMdTt_400x400.jpg", "This is a title", "Tesco the best supermarker",
                "07/12/18", 12.85);

        CreateTransaction r2 = new CreateTransaction("https://yt3.ggpht.com/a-/AAuE7mD1_8RTDAP3x93qrF-tbMtg8RbAMizHILb9_w=s900-mo-c-c0xffffffff-rj-k-no", "This is another title", "ALDI the best supermarker",
                "24/01/19", 22.10);

        CreateTransaction r3 = new CreateTransaction("url:/logo/2", "This is another title", "ALDI the best supermarker",
                "24/01/19", 22.10);

        CreateTransaction r4 = new CreateTransaction("url:/logo/2", "This is another title", "ALDI the best supermarker",
                "24/01/19", 22.10);

        CreateTransaction r5 = new CreateTransaction("url:/logo/2", "This is another title", "ALDI the best supermarker",
                "24/01/19", 22.10);

        CreateTransaction r6 = new CreateTransaction("url:/logo/2", "This is another title", "ALDI the best supermarker",
                "24/01/19", 22.10);


        CreateTransaction r7 = new CreateTransaction("url:/logo/2", "This is another title", "ALDI the best supermarker",
                "24/01/19", 22.10);

        myRecords.add(r1);
        myRecords.add(r2);
        myRecords.add(r3);
        myRecords.add(r4);


        itemTransactionAdapter = new ItemTransactionAdapter(this.getContext(), myRecords);
        listOfRecords.setAdapter(itemTransactionAdapter);

        // set the listener so we open up the receipt when the record is clicked
        listOfRecords.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent showReceipt = new Intent(getContext(), Receipt.class);
                startActivity(showReceipt);
            }
        });

        return view;
    }




}
