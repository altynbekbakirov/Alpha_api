package kg.bakirov.alpha.model.products;

public class ProductFiches {

    private String ficheNo;
    private String date;
    private Integer trCode;
    private Double net;
    private Double netTotal;
    private Double reportRate;

    public ProductFiches(String ficheNo, String date, Integer trCode, Double net, Double netTotal, Double reportRate) {
        this.ficheNo = ficheNo;
        this.date = date;
        this.trCode = trCode;
        this.net = net;
        this.netTotal = netTotal;
        this.reportRate = reportRate;
    }

    public String getFicheNo() {
        return ficheNo;
    }

    public void setFicheNo(String ficheNo) {
        this.ficheNo = ficheNo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getTrCode() {
        return trCode;
    }

    public void setTrCode(Integer trCode) {
        this.trCode = trCode;
    }

    public Double getNet() {
        return net;
    }

    public void setNet(Double net) {
        this.net = net;
    }

    public Double getNetTotal() {
        return netTotal;
    }

    public void setNetTotal(Double netTotal) {
        this.netTotal = netTotal;
    }

    public Double getReportRate() {
        return reportRate;
    }

    public void setReportRate(Double reportRate) {
        this.reportRate = reportRate;
    }
}
