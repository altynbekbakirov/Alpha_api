package kg.bakirov.alpha.model.products;

public class ProductInventory {

    private String item_code;
    private String item_name;
    private String item_group;
    private double item_onHand;
    private double item_avgVal;
    private double item_total;

    public ProductInventory() {
    }

    public ProductInventory(String item_code, String item_name, String item_group, double item_onHand, double item_avgVal, double item_total) {
        this.item_code = item_code;
        this.item_name = item_name;
        this.item_group = item_group;
        this.item_onHand = item_onHand;
        this.item_avgVal = item_avgVal;
        this.item_total = item_total;
    }

    public String getItem_code() {
        return item_code;
    }

    public void setItem_code(String item_code) {
        this.item_code = item_code;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getItem_group() {
        return item_group;
    }

    public void setItem_group(String item_group) {
        this.item_group = item_group;
    }

    public double getItem_onHand() {
        return item_onHand;
    }

    public void setItem_onHand(double item_onHand) {
        this.item_onHand = item_onHand;
    }

    public double getItem_avgVal() {
        return item_avgVal;
    }

    public void setItem_avgVal(double item_avgVal) {
        this.item_avgVal = item_avgVal;
    }

    public double getItem_total() {
        return item_total;
    }

    public void setItem_total(double item_total) {
        this.item_total = item_total;
    }
}
