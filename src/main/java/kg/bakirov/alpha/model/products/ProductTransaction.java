package kg.bakirov.alpha.model.products;

public class ProductTransaction {
    private String date;
    private String ficheNo;
    private Integer trCode;
    private String clientCode;
    private String clientName;
    private Integer count;
    private Double price;
    private Double priceUsd;
    private Double total;
    private Double totalUsd;

    public ProductTransaction(String date, String ficheNo, Integer trCode, String clientCode, String clientName, Integer count, Double price, Double priceUsd, Double total, Double totalUsd) {
        this.date = date;
        this.ficheNo = ficheNo;
        this.trCode = trCode;
        this.clientCode = clientCode;
        this.clientName = clientName;
        this.count = count;
        this.price = price;
        this.priceUsd = priceUsd;
        this.total = total;
        this.totalUsd = totalUsd;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFicheNo() {
        return ficheNo;
    }

    public void setFicheNo(String ficheNo) {
        this.ficheNo = ficheNo;
    }

    public Integer getTrCode() {
        return trCode;
    }

    public void setTrCode(Integer trCode) {
        this.trCode = trCode;
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

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getPriceUsd() {
        return priceUsd;
    }

    public void setPriceUsd(Double priceUsd) {
        this.priceUsd = priceUsd;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Double getTotalUsd() {
        return totalUsd;
    }

    public void setTotalUsd(Double totalUsd) {
        this.totalUsd = totalUsd;
    }
}
