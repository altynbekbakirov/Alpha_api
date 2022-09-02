package kg.bakirov.alpha.model.safes;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class SafeResume {
    private int month;
    private double debit;
    private double debitUsd;
    private double credit;
    private double creditUsd;
    private double total;
    private double totalUsd;

    @Override
    public String toString() {
        return "SafeResume{" +
                "month=" + month +
                ", debit=" + debit +
                ", debitUsd=" + debitUsd +
                ", credit=" + credit +
                ", creditUsd=" + creditUsd +
                ", total=" + total +
                ", totalUsd=" + totalUsd +
                '}';
    }
}
