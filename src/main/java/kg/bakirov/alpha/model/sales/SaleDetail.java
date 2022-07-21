package kg.bakirov.alpha.model.sales;

public class SaleDetail {

    private String code;
    private String name;
    private String itemGroup;
    private double retCount;
    private Double retTotal;
    private Double retCost;
    private Double saleCount;
    private Double saleTotal;
    private Double saleCost;
    private Double profitTotal;
    private Double profitPercent;

    public SaleDetail() {
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

    public String getItemGroup() {
        return itemGroup;
    }

    public void setItemGroup(String itemGroup) {
        this.itemGroup = itemGroup;
    }

    public double getRetCount() {
        return retCount;
    }

    public void setRetCount(double retCount) {
        this.retCount = retCount;
    }

    public Double getRetTotal() {
        return retTotal;
    }

    public void setRetTotal(Double retTotal) {
        this.retTotal = retTotal;
    }

    public Double getRetCost() {
        return retCost;
    }

    public void setRetCost(Double retCost) {
        this.retCost = retCost;
    }

    public Double getSaleCount() {
        return saleCount;
    }

    public void setSaleCount(Double saleCount) {
        this.saleCount = saleCount;
    }

    public Double getSaleTotal() {
        return saleTotal;
    }

    public void setSaleTotal(Double saleTotal) {
        this.saleTotal = saleTotal;
    }

    public Double getSaleCost() {
        return saleCost;
    }

    public void setSaleCost(Double saleCost) {
        this.saleCost = saleCost;
    }

    public Double getProfitTotal() {
        return profitTotal;
    }

    public void setProfitTotal(Double profitTotal) {
        this.profitTotal = profitTotal;
    }

    public Double getProfitPercent() {
        return profitPercent;
    }

    public void setProfitPercent(Double profitPercent) {
        this.profitPercent = profitPercent;
    }
}