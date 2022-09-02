package kg.bakirov.alpha.model.products;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ProductPrice {

    private Long item_row;
    private String item_code;
    private String item_name;
    private String item_groupCode;
    private Double item_onHand;
    private String item_unit;
    private Double item_price;
}
