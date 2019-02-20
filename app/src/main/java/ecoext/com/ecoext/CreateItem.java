package ecoext.com.ecoext;

public class CreateItem {

    private int qty;
    private String description;
    private double unitPrice;
    private double totalprice;

    /**
     * Constructor that creates items for the receipts
     * Takes the parameters
     * @param qty
     * @param description
     * @param unitPrice
     * @param totalprice
     */
    public CreateItem(int qty, String description, double unitPrice, double totalprice) {
        this.qty = qty;
        this.description = description;
        this.unitPrice = unitPrice;
        this.totalprice = totalprice;
    }


    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(int unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(int totalprice) {
        this.totalprice = totalprice;
    }
}
