package kg.bakirov.alpha.model.products;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductFiches {

    private Long id;
    private String ficheNo;
    private String date;
    private Integer trCode;
    private Double net;
    private Double netTotal;
    private Double reportRate;


}
