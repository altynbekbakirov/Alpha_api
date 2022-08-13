package kg.bakirov.alpha.model.sales;

public class SaleDaily {
    private String date;
    private double net;
    private double net_usd;
    private double ret_total;
    private double ret_total_usd;
    private int trCode;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getTrCode() {
        return trCode;
    }

    public void setTrCode(int trCode) {
        this.trCode = trCode;
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

    @Override
    public String toString() {
        return "SaleDaily{" +
                "date='" + date + '\'' +
                ", trCode=" + trCode +
                ", netTotal=" + net +
                ", reportNet=" + net_usd +
                ", netReturn=" + ret_total +
                ", reportReturn=" + ret_total_usd +
                '}';
    }
}
