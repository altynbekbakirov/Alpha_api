package kg.bakirov.alpha.model.sales;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class SaleTable {

    private Integer date;
    private Double total;
    private Double expenses;
    private Double discounts;
    private Double net;
    private Double net_usd;
    private Double ret_total;
    private Double ret_total_usd;

}