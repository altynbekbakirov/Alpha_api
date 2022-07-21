package kg.bakirov.alpha.model.products;

public class ProductFiche {

    private Long item_trCode;
    private String item_ficheNo;
    private String item_date;
    private String item_clientCode;
    private String item_clientName;
    private double item_gross;
    private double item_discounts;
    private double item_expenses;
    private double item_net;
    private String item_type;

    public ProductFiche(Long item_trCode, String item_ficheNo, String item_date, String item_clientCode, String item_clientName, double item_gross, double item_discounts, double item_expenses, double item_net, String item_type) {
        this.item_trCode = item_trCode;
        this.item_ficheNo = item_ficheNo;
        this.item_date = item_date;
        this.item_clientCode = item_clientCode;
        this.item_clientName = item_clientName;
        this.item_gross = item_gross;
        this.item_discounts = item_discounts;
        this.item_expenses = item_expenses;
        this.item_net = item_net;
        this.item_type = item_type;
    }

    public Long getItem_trCode() {
        return item_trCode;
    }

    public void setItem_trCode(Long item_trCode) {
        this.item_trCode = item_trCode;
    }

    public String getItem_ficheNo() {
        return item_ficheNo;
    }

    public void setItem_ficheNo(String item_ficheNo) {
        this.item_ficheNo = item_ficheNo;
    }

    public String getItem_date() {
        return item_date;
    }

    public void setItem_date(String item_date) {
        this.item_date = item_date;
    }

    public String getItem_clientCode() {
        return item_clientCode;
    }

    public void setItem_clientCode(String item_clientCode) {
        this.item_clientCode = item_clientCode;
    }

    public String getItem_clientName() {
        return item_clientName;
    }

    public void setItem_clientName(String item_clientName) {
        this.item_clientName = item_clientName;
    }

    public double getItem_gross() {
        return item_gross;
    }

    public void setItem_gross(double item_gross) {
        this.item_gross = item_gross;
    }

    public double getItem_discounts() {
        return item_discounts;
    }

    public void setItem_discounts(double item_discounts) {
        this.item_discounts = item_discounts;
    }

    public double getItem_expenses() {
        return item_expenses;
    }

    public void setItem_expenses(double item_expenses) {
        this.item_expenses = item_expenses;
    }

    public double getItem_net() {
        return item_net;
    }

    public void setItem_net(double item_net) {
        this.item_net = item_net;
    }

    public String getItem_type() {
        return item_type;
    }

    public void setItem_type(String item_type) {
        this.item_type = item_type;
    }
}
