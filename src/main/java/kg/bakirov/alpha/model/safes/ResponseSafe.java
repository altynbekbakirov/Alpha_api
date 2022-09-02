package kg.bakirov.alpha.model.safes;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ResponseSafe {
    private int firmno;
    private int periodno;
    private String begdate;
    private String enddate;
}
