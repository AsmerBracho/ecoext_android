package ecoext.com.ecoext;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class SelectBeforeScanningAdapter extends RecyclerView.Adapter<SelectBeforeScanningAdapter.ViewHolder> {
    // My Global Variables
    private ArrayList<GetUserTransactionsQuery.Purse> purses;
    private Context context;

    /**
     * Constructor HomePurseAdapter that takes the parameters as follow
     * @param context
     * @param purses
     */
    public SelectBeforeScanningAdapter(Context context, ArrayList<GetUserTransactionsQuery.Purse> purses) {
        this.context = context;
        this.purses = purses;
    }

    @NonNull
    @Override
    public SelectBeforeScanningAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.purse_horizontal, parent, false);
        return new SelectBeforeScanningAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull SelectBeforeScanningAdapter.ViewHolder holder, final int position) {
        // Set Purse Name
        holder.purseName.setText(purses.get(position).name());
        // Set Color
        String name = String.valueOf(purses.get(position).name().charAt(0)).toUpperCase();
        holder.purseBackground.setBackgroundColor(Color.parseColor(Utilities.setPurseColor(name)));
        holder.parent.setAlpha(1);


        // set onClick Listener
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.setPurseSelected(purses.get(position).purse_id());
                MainActivity.callScanner();
            }
        });

    }

    @Override
    public int getItemCount() {
        return purses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView purseName;
        CardView parent;
        RelativeLayout purseBackground;

        public ViewHolder(View itemView) {
            super(itemView);
            purseName = itemView.findViewById(R.id.purse_name);
            parent = itemView.findViewById(R.id.parent_layout_purse);
            purseBackground = itemView.findViewById(R.id.purse_background);

        }
    }
}
