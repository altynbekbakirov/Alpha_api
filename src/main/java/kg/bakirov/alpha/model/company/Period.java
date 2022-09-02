package kg.bakirov.alpha.model.company;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Period {

    private int NR;
    private int FIRMNR;
    private String BEGDATE;
    private String ENDDATE;
    private int ACTIVE;
}
