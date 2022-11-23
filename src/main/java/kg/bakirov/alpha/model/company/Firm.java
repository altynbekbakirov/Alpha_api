package kg.bakirov.alpha.model.company;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Firm {
    private int id;
    private int NR;
    private String NAME;
    private String TITLE;
    private int PERNR;
}
