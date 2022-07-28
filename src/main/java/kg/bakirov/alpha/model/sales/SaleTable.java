package kg.bakirov.alpha.model.sales;

public class SaleTable {

    private String date;
    private Double total;
    private Double expenses;
    private Double discounts;
    private Double net;
    private Double net_usd;
    private Double ret_total;
    private Double ret_total_usd;

    public SaleTable() {
    }

    public Double getRet_total() {
        return ret_total;
    }

    public void setRet_total(Double ret_total) {
        this.ret_total = ret_total;
    }

    public Double getRet_total_usd() {
        return ret_total_usd;
    }

    public void setRet_total_usd(Double ret_total_usd) {
        this.ret_total_usd = ret_total_usd;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
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

    public Double getNet_usd() {
        return net_usd;
    }

    public void setNet_usd(Double net_usd) {
        this.net_usd = net_usd;
    }
}