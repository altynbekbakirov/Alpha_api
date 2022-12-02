package kg.bakirov.alpha.model.sales;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SaleWare implements Comparable<SaleWare> {
    private int wareCode;
    private String wareName;
    private Double itemAmount;
    private Double itemTotal;
    private Double itemTotalUsd;
    private Double itemAmountRet;
    private Double itemTotalRet;
    private Double itemTotalUsdRet;

    @Override
    public int compareTo(SaleWare o) {
        return Integer.compare(this.getWareCode(), o.getWareCode());
    }
}
