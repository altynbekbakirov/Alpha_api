package kg.bakirov.alpha.model.sales;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ResponseSale {
    private int firmno;
    private int periodno;
    private String begdate;
    private String enddate;
    private int sourceindex;

}
