package kg.bakirov.alpha.model.products;

public class ProductEnough {
    private String code;
    private String name;
    private String groupCode;
    private int purAmount;
    private int saleAmount;
    private double saleTotal;
    private int onHand;
    private double enough;

    public ProductEnough() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public int getPurAmount() {
        return purAmount;
    }

    public void setPurAmount(int purAmount) {
        this.purAmount = purAmount;
    }

    public int getSaleAmount() {
        return saleAmount;
    }

    public void setSaleAmount(int saleAmount) {
        this.saleAmount = saleAmount;
    }

    public double getSaleTotal() {
        return saleTotal;
    }

    public void setSaleTotal(double saleTotal) {
        this.saleTotal = saleTotal;
    }

    public int getOnHand() {
        return onHand;
    }

    public void setOnHand(int onHand) {
        this.onHand = onHand;
    }

    public double getEnough() {
        return enough;
    }

    public void setEnough(double enough) {
        this.enough = enough;
    }
}
