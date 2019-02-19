package ecoext.com.ecoext;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toolbar;

import java.util.ArrayList;

public class RecordsFragment extends Fragment {

    ItemRecordsAdapter itemRecordsAdapter;
    ListView listOfRecords;
    ArrayList<CreateRecord> myRecords = new ArrayList<CreateRecord>();

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

        CreateRecord r1 = new CreateRecord("https://pbs.twimg.com/profile_images/1083750510895742976/qXusMdTt_400x400.jpg", "This is a title", "Tesco the best supermarker",
                "07/12/18", 12.85);

        CreateRecord r2 = new CreateRecord("https://yt3.ggpht.com/a-/AAuE7mD1_8RTDAP3x93qrF-tbMtg8RbAMizHILb9_w=s900-mo-c-c0xffffffff-rj-k-no", "This is another title", "ALDI the best supermarker",
                "24/01/19", 22.10);

        CreateRecord r3 = new CreateRecord("url:/logo/2", "This is another title", "ALDI the best supermarker",
                "24/01/19", 22.10);

        CreateRecord r4 = new CreateRecord("url:/logo/2", "This is another title", "ALDI the best supermarker",
                "24/01/19", 22.10);

        CreateRecord r5 = new CreateRecord("url:/logo/2", "This is another title", "ALDI the best supermarker",
                "24/01/19", 22.10);

        CreateRecord r6 = new CreateRecord("url:/logo/2", "This is another title", "ALDI the best supermarker",
                "24/01/19", 22.10);


        CreateRecord r7 = new CreateRecord("url:/logo/2", "This is another title", "ALDI the best supermarker",
                "24/01/19", 22.10);

        myRecords.add(r1);
        myRecords.add(r2);
        myRecords.add(r3);
        myRecords.add(r4);


        itemRecordsAdapter = new ItemRecordsAdapter(this.getContext(), myRecords);
        listOfRecords.setAdapter(itemRecordsAdapter);

        return view;
    }




}
