package kg.bakirov.alpha.model.products;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ProductEnough {
    private String code;
    private String name;
    private String groupCode;
    private int purAmount;
    private int saleAmount;
    private double saleTotal;
    private int onHand;
    private double enough;
}
