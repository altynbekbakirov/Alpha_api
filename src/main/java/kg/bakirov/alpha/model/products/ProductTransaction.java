package kg.bakirov.alpha.model.products;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ProductTransaction {
    private String date;
    private String ficheNo;
    private Integer trCode;
    private String clientCode;
    private String clientName;
    private Integer count;
    private Double price;
    private Double priceUsd;
    private Double total;
    private Double totalUsd;
}
