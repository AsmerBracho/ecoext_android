package ecoext.com.ecoext.receipt;

import android.os.Parcel;
import android.os.Parcelable;

public class Item implements Parcelable {


    private int transactionId;
    private String name;
    private double price;
    private int quantity;
    private double tax;

    public Item(int transactionId, String name, double price, int quantity, double tax) {
        this.transactionId = transactionId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.tax = tax;
    }

    protected Item(Parcel in) {
        transactionId = in.readInt();
        name = in.readString();
        price = in.readDouble();
        quantity = in.readInt();
        tax = in.readDouble();
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(transactionId);
        parcel.writeString(name);
        parcel.writeDouble(price);
        parcel.writeInt(quantity);
        parcel.writeDouble(tax);
    }
}
