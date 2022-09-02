package kg.bakirov.alpha.model.sales;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SaleClientManager implements Comparable<SaleClientManager> {
    private String clientCode;
    private String clientName;
    private Double itemAmount;
    private Double itemTotal;
    private Double itemTotalUsd;
    private Double itemAmountRet;
    private Double itemTotalRet;
    private Double itemTotalUsdRet;

    @Override
    public int compareTo(SaleClientManager o) {
        return this.getClientCode().compareTo(o.getClientCode());
    }
}
