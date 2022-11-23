package kg.bakirov.alpha.model.products;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Product {
    private String item_code;
    private String item_name;
    private String item_group;
    private double item_purchase_price;
    private double item_sale_price;
    private int item_purAmount;
    private double item_purCurr;
    private int item_salAmount;
    private double item_salCurr;
    private int item_onHand;
    private double item_purchase_sum;
    private double item_sale_sum;
}
