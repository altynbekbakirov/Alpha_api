package kg.bakirov.alpha.model.products;

public class ProductPrices {
    private String code;
    private String name;
    private String definition;
    private Integer ptype;
    private Double price;
    private String currency;
    private String begdate;
    private String enddate;
    private Integer active;

    public ProductPrices(String code, String name, String definition, Integer ptype, Double price, String currency, String begdate, String enddate, Integer active) {
        this.code = code;
        this.name = name;
        this.definition = definition;
        this.ptype = ptype;
        this.price = price;
        this.currency = currency;
        this.begdate = begdate;
        this.enddate = enddate;
        this.active = active;
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

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public Integer getPtype() {
        return ptype;
    }

    public void setPtype(Integer ptype) {
        this.ptype = ptype;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getBegdate() {
        return begdate;
    }

    public void setBegdate(String begdate) {
        this.begdate = begdate;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }
}
