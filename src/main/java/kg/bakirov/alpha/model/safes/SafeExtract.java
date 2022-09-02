package kg.bakirov.alpha.model.safes;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class SafeExtract {
    private String date;
    private String ficheNo;
    private String title;
    private String definition;
    private int trCode;
    private double collection;
    private double collectionUsd;
    private double payment;
    private double paymentUsd;
    private byte hour;
    private byte minute;
}
