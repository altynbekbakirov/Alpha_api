package kg.bakirov.alpha.model.accounts;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class AccountExtract {

    private String code;
    private String name;
    private String date;
    private int trcode;
    private String ficheno;
    private String description;
    private Double debit;
    private Double credit;
    private Double balance;
    private Double balanceBefore;
}
