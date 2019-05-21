package ecoext.com.ecoext.categories;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class CategoriesAdapter extends RecyclerView.Adapter<
        CategoriesAdapter.ViewHolder> {

    // Define the Context
    private Context context;
    ArrayList<String> listOfCategories;

    public CategoriesAdapter(Context context, ArrayList<String> categoriesList) {
        this.context = context;
        this.listOfCategories = categoriesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

}
