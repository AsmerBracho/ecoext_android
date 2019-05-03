package ecoext.com.ecoext;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class DetailsPurseAdapter extends RecyclerView.Adapter<DetailsPurseAdapter.ViewHolder> {

    private GetAllUserTransactionsOrderByDateQuery.UserTransaction transactions;
    private Context context;

    public DetailsPurseAdapter(Context context, GetAllUserTransactionsOrderByDateQuery.UserTransaction transactions) {
        this.context = context;
        this.transactions = transactions;
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
