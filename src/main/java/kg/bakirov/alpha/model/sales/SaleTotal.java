package kg.bakirov.alpha.model.sales;

public class SaleTotal {

    private String code;
    private String name;
    private String group;
    private Double purchaseCount;
    private Double purchaseTotal;
    private Double purchaseTotalUsd;
    private Double saleCount;
    private Double saleTotal;
    private Double saleTotalUsd;

    public SaleTotal(String code, String name, String group, Double purchaseCount, Double purchaseTotal, Double purchaseTotalUsd, Double saleCount, Double saleTotal, Double saleTotalUsd) {
        this.code = code;
        this.name = name;
        this.group = group;
        this.purchaseCount = purchaseCount;
        this.purchaseTotal = purchaseTotal;
        this.purchaseTotalUsd = purchaseTotalUsd;
        this.saleCount = saleCount;
        this.saleTotal = saleTotal;
        this.saleTotalUsd = saleTotalUsd;
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

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Double getPurchaseCount() {
        return purchaseCount;
    }

    public void setPurchaseCount(Double purchaseCount) {
        this.purchaseCount = purchaseCount;
    }

    public Double getPurchaseTotal() {
        return purchaseTotal;
    }

    public void setPurchaseTotal(Double purchaseTotal) {
        this.purchaseTotal = purchaseTotal;
    }

    public Double getPurchaseTotalUsd() {
        return purchaseTotalUsd;
    }

    public void setPurchaseTotalUsd(Double purchaseTotalUsd) {
        this.purchaseTotalUsd = purchaseTotalUsd;
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

    public Double getSaleTotalUsd() {
        return saleTotalUsd;
    }

    public void setSaleTotalUsd(Double saleTotalUsd) {
        this.saleTotalUsd = saleTotalUsd;
    }
}
