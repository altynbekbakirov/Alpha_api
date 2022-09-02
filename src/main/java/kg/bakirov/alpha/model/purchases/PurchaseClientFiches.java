package kg.bakirov.alpha.model.purchases;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class PurchaseClientFiches {
    private Long id;
    private int trCode;
    private String ficheNo;
    private String date;
    private String clientCode;
    private String clientName;
    private Double gross;
    private Double discounts;
    private Double expenses;
    private Double net;
    private Double netUsd;
}