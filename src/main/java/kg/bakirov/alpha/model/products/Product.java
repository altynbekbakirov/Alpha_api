package kg.bakirov.alpha.model.products;

public class Product {

    private String item_code;
    private String item_name;
    private String item_group;
    private double item_purchase_price;
    private double item_sale_price;
    private int item_purAmount;
    private int item_salAmount;
    private double item_salCurr;
    private int item_onHand;
    private double item_purchase_sum;
    private double item_sale_sum;

    public Product(String item_code, String item_name, String item_group, double item_purchase_price, double item_sale_price, int item_purAmount, int item_salAmount, double item_salCurr, int item_onHand, double item_purchase_sum, double item_sale_sum) {
        this.item_code = item_code;
        this.item_name = item_name;
        this.item_group = item_group;
        this.item_purchase_price = item_purchase_price;
        this.item_sale_price = item_sale_price;
        this.item_purAmount = item_purAmount;
        this.item_salAmount = item_salAmount;
        this.item_salCurr = item_salCurr;
        this.item_onHand = item_onHand;
        this.item_purchase_sum = item_purchase_sum;
        this.item_sale_sum = item_sale_sum;
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

    public double getItem_purchase_price() {
        return item_purchase_price;
    }

    public void setItem_purchase_price(double item_purchase_price) {
        this.item_purchase_price = item_purchase_price;
    }

    public double getItem_sale_price() {
        return item_sale_price;
    }

    public void setItem_sale_price(double item_sale_price) {
        this.item_sale_price = item_sale_price;
    }

    public int getItem_purAmount() {
        return item_purAmount;
    }

    public void setItem_purAmount(int item_purAmount) {
        this.item_purAmount = item_purAmount;
    }

    public int getItem_salAmount() {
        return item_salAmount;
    }

    public void setItem_salAmount(int item_salAmount) {
        this.item_salAmount = item_salAmount;
    }

    public double getItem_salCurr() {
        return item_salCurr;
    }

    public void setItem_salCurr(double item_salCurr) {
        this.item_salCurr = item_salCurr;
    }

    public int getItem_onHand() {
        return item_onHand;
    }

    public void setItem_onHand(int item_onHand) {
        this.item_onHand = item_onHand;
    }

    public double getItem_purchase_sum() {
        return item_purchase_sum;
    }

    public void setItem_purchase_sum(double item_purchase_sum) {
        this.item_purchase_sum = item_purchase_sum;
    }

    public double getItem_sale_sum() {
        return item_sale_sum;
    }

    public void setItem_sale_sum(double item_sale_sum) {
        this.item_sale_sum = item_sale_sum;
    }
}
