package kg.bakirov.alpha.model.accounts;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Account {
    private Long id;
    private String code;
    private String name;
    private String address;
    private String phone;
    private double debit;
    private double credit;
    private double balance;
}
