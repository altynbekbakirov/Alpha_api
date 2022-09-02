package kg.bakirov.alpha.model.sales;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SaleFiche {
    private String code;
    private String name;
    private String date;
    private Integer count;
    private String unit;
    private Double price;
    private Double total;
    private Double priceUsd;
    private Double totalUsd;

}
