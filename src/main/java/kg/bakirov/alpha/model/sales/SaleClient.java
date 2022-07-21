package kg.bakirov.alpha.model.sales;

public class SaleClient {
    private String clientCode;
    private String clientName;
    private String itemCode;
    private String itemName;
    private String itemGroup;
    private Double itemAmount;
    private Double itemTotal;
    private Double itemTotalUsd;
    private Double itemAmountRet;
    private Double itemTotalRet;
    private Double itemTotalUsdRet;

    public SaleClient() {
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

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemGroup() {
        return itemGroup;
    }

    public void setItemGroup(String itemGroup) {
        this.itemGroup = itemGroup;
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
}