package kg.bakirov.alpha.model.products;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ResponseModel {
    private int firmNo;
    private int periodNo;
    private String begDate;
    private String endDate;
    private int sourceIndex;
    private  String filterName;
}
