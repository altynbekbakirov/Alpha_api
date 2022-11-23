package kg.bakirov.alpha.model.sales;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ResponseSale {
    private int firmNo;
    private int periodNo;
    private String begDate;
    private String endDate;
    private int sourceIndex;
}
