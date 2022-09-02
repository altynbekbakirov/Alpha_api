package kg.bakirov.alpha.model.sales;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SaleTotal {

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
