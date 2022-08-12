package kg.bakirov.alpha.model.sales;

public class SaleFiche {
    private String code;
    private String name;
    private String date;
    private Integer count;
    private String unit;
    private Double price;
    private Double total;
    private Double priceUsd;
    private Double totalUsd;

    public SaleFiche(String code, String name, String date, Integer count, String unit, Double price, Double total, Double priceUsd, Double totalUsd) {
        this.code = code;
        this.name = name;
        this.date = date;
        this.count = count;
        this.unit = unit;
        this.price = price;
        this.total = total;
        this.priceUsd = priceUsd;
        this.totalUsd = totalUsd;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Double getPriceUsd() {
        return priceUsd;
    }

    public void setPriceUsd(Double priceUsd) {
        this.priceUsd = priceUsd;
    }

    public Double getTotalUsd() {
        return totalUsd;
    }

    public void setTotalUsd(Double totalUsd) {
        this.totalUsd = totalUsd;
    }
}
