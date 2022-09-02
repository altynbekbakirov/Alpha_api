package kg.bakirov.alpha.model.accounts;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class AccountDebit {

    private String code;
    private String name;
    private String address;
    private String phone;
    private Double debit;
    private Double credit;
    private Double balance;
    private Double debitUsd;
    private Double creditUsd;
    private Double balanceUsd;
}
