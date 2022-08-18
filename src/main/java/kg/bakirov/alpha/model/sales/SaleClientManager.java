package kg.bakirov.alpha.model.sales;

public class SaleClientManager implements Comparable<SaleClientManager> {
    private String clientCode;
    private String clientName;
    private Double itemAmount;
    private Double itemTotal;
    private Double itemTotalUsd;
    private Double itemAmountRet;
    private Double itemTotalRet;
    private Double itemTotalUsdRet;

    public SaleClientManager() {
    }

    public SaleClientManager(String clientCode, String clientName, Double itemAmount, Double itemTotal, Double itemTotalUsd, Double itemAmountRet, Double itemTotalRet, Double itemTotalUsdRet) {
        this.clientCode = clientCode;
        this.clientName = clientName;
        this.itemAmount = itemAmount;
        this.itemTotal = itemTotal;
        this.itemTotalUsd = itemTotalUsd;
        this.itemAmountRet = itemAmountRet;
        this.itemTotalRet = itemTotalRet;
        this.itemTotalUsdRet = itemTotalUsdRet;
    }

    public Double getItemAmountRet() {
        return itemAmountRet;
    }

    public void setItemAmountRet(Double itemAmountRet) {
        this.itemAmountRet = itemAmountRet;
    }

    public Double getItemTotalRet() {
        return itemTotalRet;
    }

    public void setItemTotalRet(Double itemTotalRet) {
        this.itemTotalRet = itemTotalRet;
    }

    public Double getItemTotalUsdRet() {
        return itemTotalUsdRet;
    }

    public void setItemTotalUsdRet(Double itemTotalUsdRet) {
        this.itemTotalUsdRet = itemTotalUsdRet;
    }

    public String getClientCode() {
        return clientCode;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Double getItemAmount() {
        return itemAmount;
    }

    public void setItemAmount(Double itemAmount) {
        this.itemAmount = itemAmount;
    }

    public Double getItemTotal() {
        return itemTotal;
    }

    public void setItemTotal(Double itemTotal) {
        this.itemTotal = itemTotal;
    }

    public Double getItemTotalUsd() {
        return itemTotalUsd;
    }

    public void setItemTotalUsd(Double itemTotalUsd) {
        this.itemTotalUsd = itemTotalUsd;
    }

    @Override
    public int compareTo(SaleClientManager o) {
        return this.getClientCode().compareTo(o.getClientCode());
    }
}
