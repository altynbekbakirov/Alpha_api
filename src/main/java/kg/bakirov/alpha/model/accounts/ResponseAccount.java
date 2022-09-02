package kg.bakirov.alpha.model.accounts;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ResponseAccount {
    private int firmno;
    private int periodno;
    private String begdate;
    private String enddate;
}
