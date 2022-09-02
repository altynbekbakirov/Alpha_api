package kg.bakirov.alpha.model.sales;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class SaleDetail {

    private String code;
    private String name;
    private String itemGroup;
    private double retCount;
    private Double retTotal;
    private Double retCost;
    private Double saleCount;
    private Double saleTotal;
    private Double saleCost;
    private Double profitTotal;
    private Double profitPercent;

}