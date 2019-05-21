package ecoext.com.ecoext.purse;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ecoext.com.ecoext.R;
import ecoext.com.ecoext.general.Utilities;

/**
 * Class PurseInAddNameItemAdapter
 * implementation of the Adapter for the create purse class where the views for purses
 * have a different behaviour than the rest of then in the application
 */
public class PurseInAddNameItemAdapter extends RecyclerView.Adapter<PurseInAddNameItemAdapter.ViewHolder> {

    private static final String TAG = "PurseInAddName";
    private AddNameAndPurse addNameAndPurse;
    private ArrayList<String> purses;
    private ArrayList<Integer> purseId;
    private Context context;
    private int selected_position = RecyclerView.NO_POSITION;

    /**
     * Constructor for PurseInAddNameItemAdapter
     * It takes the parameters
     *
     * @param context context where it is called
     * @param c       a reference of the AddNameAndPurse activity
     * @param purses  a list of purses names
     * @param purseId a list of purses ID's
     */
    public PurseInAddNameItemAdapter(Context context, AddNameAndPurse c, ArrayList<String> purses,
                                     ArrayList<Integer> purseId) {

        // set up the parameter as global variables
        this.purses = purses;
        this.purseId = purseId;
        this.context = context;
        this.addNameAndPurse = c;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.purse_horizontal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        // Set Purse Name
        holder.purseName.setText(purses.get(position));
        // Set Color
        String name = String.valueOf(purses.get(position).charAt(0)).toUpperCase();
        holder.purseBackground.setBackgroundColor(Color.parseColor(Utilities.setPurseColor(name)));

        // set alpha
        if (selected_position == position) {
            holder.parent.setAlpha(1);
        } else {
            holder.parent.setAlpha(0.2f);
        }

        holder.parent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // create an instance of AddNameAndPurse to be able to change the purseId

                addNameAndPurse.setPurseId(purseId.get(position));

                Log.d(TAG, "ChangingPurseId" + addNameAndPurse.getPurseId());

                if (holder.getAdapterPosition() == RecyclerView.NO_POSITION) return;

                // Updating old as well as new positions
                notifyItemChanged(selected_position);
                selected_position = holder.getAdapterPosition();
                notifyItemChanged(selected_position);

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
