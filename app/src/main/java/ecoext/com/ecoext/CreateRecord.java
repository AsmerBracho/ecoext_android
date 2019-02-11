package ecoext.com.ecoext;

/**
 * Class used to create instances of the records brought from database
 */
public class CreateRecord {

    private String logo;
    private String title;
    private String description;
    private String date;
    private double price;

    /**
     * Constructor to create Records
     * take the parameters:
     * @param logo
     * @param title
     * @param description
     * @param date
     * @param price
     */
    public CreateRecord(String logo, String title, String description, String date, double price) {
        this.logo = logo;
        this.title = title;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
