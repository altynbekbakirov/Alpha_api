package kg.bakirov.alpha.model.sales;

public class SaleTable {

    private int date;
    private Double total;
    private Double expenses;
    private Double discounts;
    private Double net;
    private Double net_usd;
    private Double returnAmount;

    public SaleTable() {
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Double getExpenses() {
        return expenses;
    }

    public void setExpenses(Double expenses) {
        this.expenses = expenses;
    }

    public Double getDiscounts() {
        return discounts;
    }

    public void setDiscounts(Double discounts) {
        this.discounts = discounts;
    }

    public Double getNet() {
        return net;
    }

    public void setNet(Double net) {
        this.net = net;
    }

    public Double getReturnAmount() {
        return returnAmount;
    }

    public void setReturnAmount(Double returnAmount) {
        this.returnAmount = returnAmount;
    }

    public Double getNet_usd() {
        return net_usd;
    }

    public void setNet_usd(Double net_usd) {
        this.net_usd = net_usd;
    }
}