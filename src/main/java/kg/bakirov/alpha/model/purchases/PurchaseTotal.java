package kg.bakirov.alpha.model.purchases;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class PurchaseTotal {

    private String code;
    private String name;
    private String group;
    private Double purchaseCount;
    private Double purchaseTotal;
    private Double purchaseTotalUsd;
    private Double saleCount;
    private Double saleTotal;
    private Double saleTotalUsd;
}
