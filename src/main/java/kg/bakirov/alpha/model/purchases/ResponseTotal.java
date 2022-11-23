package kg.bakirov.alpha.model.purchases;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ResponseTotal {
    private int firmNo;
    private int periodNo;
    private String begDate;
    private String endDate;
    private  int sourceIndex;
    private  String filterName;
}
