package ecoext.com.ecoext.general;

import android.content.Context;
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
import ecoext.com.ecoext.R;

/**
 * Adapter that renders a list of purses right before scanning a QR
 */
public class SelectBeforeScanningAdapter extends RecyclerView.Adapter<SelectBeforeScanningAdapter.ViewHolder> {

    // My Global Variables
    private ArrayList<GetUserTransactionsQuery.Purse> purses; // list of purses
    private Context context; // context

    /**
     * Constructor SelectBeforeScanningAdapter
     *
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
        holder.purseBackground.setBackgroundColor(Color.parseColor(Utilities.setPurseColorbyPurseId(purses.get(position).purse_id())));
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

    /**
     * Inner Class ViewHolder that initialize the views handle
     * by the Adapter
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
