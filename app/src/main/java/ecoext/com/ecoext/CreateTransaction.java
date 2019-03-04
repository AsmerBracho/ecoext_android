package ecoext.com.ecoext;

import java.util.ArrayList;

/**
 * Class used to create instances of the transactions brought from database
 */
public class CreateTransaction {

    private int id;
    private String logo;
    private String title;
    private String purse;
    private String date;
    private double price;
    private ArrayList<CreateItem> listOfItems;

    /**
     * Constructor to create Records
     * take the parameters:
     * @param logo
     * @param title
     * @param purse
     * @param date
     * @param price
     */
    public CreateTransaction(String logo, String title, String purse, String date, double price) {
        this.logo = logo;
        this.title = title;
        this.purse = purse;
        this.date = date;
        this.price = price;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPurse() {
        return purse;
    }

    public void setPurse(String purse) {
        this.purse = purse;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }


}
