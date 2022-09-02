package kg.bakirov.alpha.model.sales;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class SaleFiches {

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
    private String managerCode;
    private String managerName;

    public SaleFiches(Long id, int trCode, String ficheNo, String date, String managerCode, String managerName, String clientCode, String clientName, Double gross, Double discounts, Double expenses, Double net, Double netUsd) {
        this.id = id;
        this.trCode = trCode;
        this.ficheNo = ficheNo;
        this.date = date;
        this.managerCode = managerCode;
        this.managerName = managerName;
        this.clientCode = clientCode;
        this.clientName = clientName;
        this.gross = gross;
        this.discounts = discounts;
        this.expenses = expenses;
        this.net = net;
        this.netUsd = netUsd;
    }
}