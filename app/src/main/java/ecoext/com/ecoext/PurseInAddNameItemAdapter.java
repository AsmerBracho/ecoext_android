package ecoext.com.ecoext;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class PurseInAddNameItemAdapter extends RecyclerView.Adapter<PurseInAddNameItemAdapter.ViewHolder> {

    private static final String TAG = "PurseInAddNameItemAdapter";

    private ArrayList<String> purses;
    private Context context;

    public PurseInAddNameItemAdapter(Context context,  ArrayList<String> purses) {
        this.purses = purses;
        this.context = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.purse_horizontal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.purseName.setText(purses.get(position));

        String name = String.valueOf(purses.get(position).charAt(0)).toUpperCase();
        if (("A").equals(name) || ("B").equals(name) || ("C").equals(name) || ("D").equals(name) || ("E").equals(name) || ("P").equals(name)) {
            holder.purseBackground.setBackgroundColor(Color.parseColor("#355ee4"));
        } else if (("F").equals(name) || ("G").equals(name) || ("H").equals(name) || ("I").equals(name) || ("K").equals(name) || ("Z").equals(name)) {
            holder.purseBackground.setBackgroundColor(Color.parseColor("#e49c35"));
        } else {
            holder.purseBackground.setBackgroundColor(Color.parseColor("#35e45e"));
        }


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
