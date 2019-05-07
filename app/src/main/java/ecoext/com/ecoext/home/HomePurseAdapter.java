package ecoext.com.ecoext.home;

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
import ecoext.com.ecoext.GetUserTransactionsQuery;
import ecoext.com.ecoext.PurseDetails;
import ecoext.com.ecoext.R;
import ecoext.com.ecoext.general.Utilities;

/**
 * Class HomePurseAdapter
 * implementation of the Adapter for the Home Screen where the views for purses
 * have a different beauvoir than the rest of then in the application
 */
public class HomePurseAdapter extends RecyclerView.Adapter<HomePurseAdapter.ViewHolder> {

    // My Global Variables
    private ArrayList<GetUserTransactionsQuery.Purse> purses;
    private Context context;

    /**
     * Constructor HomePurseAdapter that takes the parameters as follow
     * @param context
     * @param purses
     */
    public HomePurseAdapter(Context context, ArrayList<GetUserTransactionsQuery.Purse> purses) {
        this.context = context;
        this.purses = purses;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.purse_horizontal, parent, false);
        return new HomePurseAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
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

                // Calculate Balance
                double balance = 0;
                for (int i = 0 ; i < purses.get(position).transaction().size(); i ++) {
                    for (int j = 0 ; j < purses.get(position).transaction().get(i).items().size(); j ++) {
                        balance += purses.get(position).transaction().get(i).items().get(j).price();
                    }
                }
                Intent purseDetails = new Intent(context, PurseDetails.class);
                purseDetails.putExtra("PurseID", Integer.toString(purses.get(position).purse_id()));
                purseDetails.putExtra("PurseName", purses.get(position).name());
                purseDetails.putExtra("Description", purses.get(position).description());
                purseDetails.putExtra("Balance", Double.toString(balance));
                context.startActivity(purseDetails);
            }
        });

    }

    @Override
    public int getItemCount() {
        return purses.size();
    }

    /**
     * Inner Class ViewHolder that initialize the views handle
     * by the purse Adapter
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView purseName;
        private CardView parent;
        private RelativeLayout purseBackground;

        public ViewHolder(View itemView) {
            super(itemView);
            purseName = itemView.findViewById(R.id.purse_name);
            parent = itemView.findViewById(R.id.parent_layout_purse);
            purseBackground = itemView.findViewById(R.id.purse_background);

        }
    }
}
