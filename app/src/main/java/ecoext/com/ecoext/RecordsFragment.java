package ecoext.com.ecoext;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

public class RecordsFragment extends Fragment {

    ItemRecordsAdapter itemRecordsAdapter;
    Context thisContext;
    ListView listOfRecords;
    ArrayList<CreateRecord> myRecords = new ArrayList<CreateRecord>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.records, container, false);

        listOfRecords = view.findViewById(R.id.listOfRecords);
        thisContext = getContext();

        /**
         * HERE we are going to call database CONECTION AND populate the ListOfRecords
         *
         * SIMULATION NOW
         */

        // putting data in the Array

        CreateRecord r1 = new CreateRecord("url:/logo", "This is a title", "Tesco the best supermarker",
                "07/12/18", 12.85);

        CreateRecord r2 = new CreateRecord("url:/logo/2", "This is another title", "ALDI the best supermarker",
                "24/01/19", 22.10);

        myRecords.add(r1);
        myRecords.add(r2);

        itemRecordsAdapter = new ItemRecordsAdapter(thisContext, myRecords);
        listOfRecords.setAdapter(itemRecordsAdapter);

        return view;
    }




}
