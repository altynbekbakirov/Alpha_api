package kg.bakirov.alpha.model.accounts;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AccountFiches {
    private String date;
    private String ficheNo;
    private int trCode;
    private double debit;
    private double credit;
    private double repDebit;
    private double repCredit;
    private String definition;
}
