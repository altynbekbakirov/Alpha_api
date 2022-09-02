package kg.bakirov.alpha.model.sales;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SaleMonth {

    private String code;
    private String name;
    private String groupCode;
    private Double jan;
    private Double feb;
    private Double mar;
    private Double apr;
    private Double may;
    private Double jun;
    private Double jul;
    private Double aug;
    private Double sep;
    private Double oct;
    private Double nov;
    private Double dec;
    private Double totalCount;
    private Double totalSum;
    private Double totalUsd;

}