package kg.bakirov.alpha.model.purchases;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class PurchaseClient {

    private String clientCode;
    private String clientName;
    private Double itemAmount;
    private Double itemTotal;
    private Double itemTotalUsd;
    private Double itemAmountRet;
    private Double itemTotalRet;
    private Double itemTotalUsdRet;
}
