package kg.bakirov.alpha.model.products;

public class ProductFiche {
    private String code;
    private String name;
    private Integer count;
    private Double price;
    private Double priceUsd;
    private Double total;
    private Double totalUsd;
    private String definition;

    public ProductFiche(String code, String name, Integer count, Double price, Double priceUsd, Double total, Double totalUsd, String definition) {
        this.code = code;
        this.name = name;
        this.count = count;
        this.price = price;
        this.priceUsd = priceUsd;
        this.total = total;
        this.totalUsd = totalUsd;
        this.definition = definition;
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

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }
}
