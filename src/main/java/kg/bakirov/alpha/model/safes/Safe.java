package kg.bakirov.alpha.model.safes;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Safe {
    private String code;
    private String name;
    private String definition;
    private double balance;
    private double balanceUsd;
}
