package kg.bakirov.alpha.model.products;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ProductPrices {
    private String code;
    private String name;
    private String definition;
    private Integer ptype;
    private Double price;
    private String currency;
    private String begdate;
    private String enddate;
    private Integer active;
}
