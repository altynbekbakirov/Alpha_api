package kg.bakirov.alpha.model.products;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ProductInventory {

    private String item_code;
    private String item_name;
    private String item_group;
    private double item_onHand;
    private double item_avgVal;
    private double item_total;
}
