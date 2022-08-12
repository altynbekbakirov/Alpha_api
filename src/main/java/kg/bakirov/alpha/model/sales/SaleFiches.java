package kg.bakirov.alpha.model.sales;

public class SaleFiches {

    private int trCode;
    private String ficheNo;
    private String date;
    private String clientCode;
    private String clientName;
    private Double gross;
    private Double discounts;
    private Double expenses;
    private Double net;
    private Double netUsd;
    private String managerCode;
    private String managerName;

    public SaleFiches(int trCode, String ficheNo, String date, String managerCode, String managerName, String clientCode, String clientName, Double gross, Double discounts, Double expenses, Double net, Double netUsd) {
        this.trCode = trCode;
        this.ficheNo = ficheNo;
        this.date = date;
        this.managerCode = managerCode;
        this.managerName = managerName;
        this.clientCode = clientCode;
        this.clientName = clientName;
        this.gross = gross;
        this.discounts = discounts;
        this.expenses = expenses;
        this.net = net;
        this.netUsd = netUsd;
    }

    public String getManagerCode() {
        return managerCode;
    }

    public void setManagerCode(String managerCode) {
        this.managerCode = managerCode;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public int getTrCode() {
        return trCode;
    }

    public void setTrCode(int trCode) {
        this.trCode = trCode;
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

    public Double getGross() {
        return gross;
    }

    public void setGross(Double gross) {
        this.gross = gross;
    }

    public Double getDiscounts() {
        return discounts;
    }

    public void setDiscounts(Double discounts) {
        this.discounts = discounts;
    }

    public Double getExpenses() {
        return expenses;
    }

    public void setExpenses(Double expenses) {
        this.expenses = expenses;
    }

    public Double getNet() {
        return net;
    }

    public void setNet(Double net) {
        this.net = net;
    }

    public Double getNetUsd() {
        return netUsd;
    }

    public void setNetUsd(Double netUsd) {
        this.netUsd = netUsd;
    }
}