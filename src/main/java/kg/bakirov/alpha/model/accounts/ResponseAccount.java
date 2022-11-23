package kg.bakirov.alpha.model.accounts;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ResponseAccount {
    private int firmNo;
    private int periodNo;
    private String begDate;
    private String endDate;
    private String filterName;
}
