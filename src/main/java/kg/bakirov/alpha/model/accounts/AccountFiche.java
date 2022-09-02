package kg.bakirov.alpha.model.accounts;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class AccountFiche {
    private String code;
    private String name;
    private double debit;
    private double debitUsd;
    private double credit;
    private double creditUsd;
    private String definition;
}
