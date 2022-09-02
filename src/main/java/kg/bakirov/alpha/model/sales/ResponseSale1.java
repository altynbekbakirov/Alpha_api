package kg.bakirov.alpha.model.sales;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ResponseSale1 {
    private int firmno;
    private int periodno;
    private String begdate;
    private String enddate;

}
