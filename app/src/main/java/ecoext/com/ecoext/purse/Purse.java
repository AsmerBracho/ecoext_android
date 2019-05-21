package ecoext.com.ecoext.purse;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import ecoext.com.ecoext.GetUserTransactionsQuery;

public class Purse implements Parcelable {

    private int purseId;
    private String name = null;
    private String description = null;
    private ArrayList<GetUserTransactionsQuery.Transaction> transactions;

    public Purse(int id, String name, String description, ArrayList<GetUserTransactionsQuery.Transaction> transactions) {
        this.purseId = id;
        this.name = name;
        this.description = description;
        this.transactions = transactions;
    }

    protected Purse(Parcel in) {
        purseId = in.readInt();
        name = in.readString();
        description = in.readString();
    }

    public static final Creator<Purse> CREATOR = new Creator<Purse>() {
        @Override
        public Purse createFromParcel(Parcel in) {
            return new Purse(in);
        }

        @Override
        public Purse[] newArray(int size) {
            return new Purse[size];
        }
    };

    public int getPurseId() {
        return purseId;
    }

    public void setPurseId(int purseId) {
        this.purseId = purseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<GetUserTransactionsQuery.Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(ArrayList<GetUserTransactionsQuery.Transaction> transactions) {
        this.transactions = transactions;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(purseId);
        parcel.writeString(name);
        parcel.writeString(description);
    }
}
