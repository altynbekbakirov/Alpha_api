package kg.bakirov.alpha.model.accounts;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccountAging {
    private String code;
    private String name;
    private String phone;
    private Double balance;
    private Double payment1;
    private Double payment2;
    private Double payment3;
    private Double payment4;
    private Double payment5;
    private Double payment;
    private String lastFinTrans;
    private String lastMatTrans;
}
