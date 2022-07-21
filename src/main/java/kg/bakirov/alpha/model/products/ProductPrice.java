package kg.bakirov.alpha.model.products;

public class ProductPrice {

    private Long item_row;
    private String item_code;
    private String item_name;
    private String item_groupCode;
    private Double item_onHand;
    private String item_unit;
    private Double item_price;

    public ProductPrice(Long item_row, String item_code, String item_name, String item_groupCode, Double item_onHand, String item_unit, Double item_price) {
        this.item_row = item_row;
        this.item_code = item_code;
        this.item_name = item_name;
        this.item_groupCode = item_groupCode;
        this.item_onHand = item_onHand;
        this.item_unit = item_unit;
        this.item_price = item_price;
    }

    public Long getItem_row() {
        return item_row;
    }

    public void setItem_row(Long item_row) {
        this.item_row = item_row;
    }

    public String getItem_code() {
        return item_code;
    }

    public void setItem_code(String item_code) {
        this.item_code = item_code;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getItem_groupCode() {
        return item_groupCode;
    }

    public void setItem_groupCode(String item_groupCode) {
        this.item_groupCode = item_groupCode;
    }

    public Double getItem_onHand() {
        return item_onHand;
    }

    public void setItem_onHand(Double item_onHand) {
        this.item_onHand = item_onHand;
    }

    public String getItem_unit() {
        return item_unit;
    }

    public void setItem_unit(String item_unit) {
        this.item_unit = item_unit;
    }

    public Double getItem_price() {
        return item_price;
    }

    public void setItem_price(Double item_price) {
        this.item_price = item_price;
    }
}
