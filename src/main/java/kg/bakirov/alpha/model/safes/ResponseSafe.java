package kg.bakirov.alpha.model.safes;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ResponseSafe {
    private int firmNo;
    private int periodNo;
    private String begDate;
    private String endDate;
    private String filterName;
    private String operationType;
}
