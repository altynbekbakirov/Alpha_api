package kg.bakirov.alpha.model.products;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ProductFiche {
    private String code;
    private String name;
    private Integer count;
    private Double price;
    private Double priceUsd;
    private Double total;
    private Double totalUsd;
    private String definition;
}
