package ecoext.com.ecoext;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomePurseAdapter homePurseAdapter;
    private ArrayList<GetUserTransactionsQuery.Purse> purses;
    private RecyclerView listOfPurses;

    public HomeFragment() {

    }

    @SuppressLint("ValidFragment")
    public HomeFragment(ArrayList<GetUserTransactionsQuery.Purse> purses) {
        this.purses = purses;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);

        homePurseAdapter = new HomePurseAdapter(this.getContext(), purses);
        initReciclerView(view);

        return view;
    }

    public void initReciclerView(View view) {
        listOfPurses = view.findViewById(R.id.home_list_purses_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
        listOfPurses.setLayoutManager(layoutManager);
        listOfPurses.setAdapter(homePurseAdapter);
    }
}
