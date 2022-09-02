package kg.bakirov.alpha.model.purchases;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ResponseModel {
    private int firmno;
    private int periodno;
    private String begdate;
    private String enddate;
    private int sourceindex;
}
