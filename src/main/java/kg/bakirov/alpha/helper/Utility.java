package kg.bakirov.alpha.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import static kg.bakirov.alpha.repository.MainRepository.GLOBAL_FIRM_NO;
import static kg.bakirov.alpha.repository.MainRepository.GLOBAL_PERIOD;

@Component
public class Utility {

    private final DataSource dataSource;

    @Autowired
    public Utility(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void CheckCompany(int firmNo, int periodNo) {

        GLOBAL_FIRM_NO = String.format("%03d", firmNo);
        GLOBAL_PERIOD = String.format("%02d", periodNo);

        /*if (GLOBAL_FIRM_NO == null) {
            List<Firm> firmList = mainRepository.getFirmList();
            Firm firm = firmList.get(0);
            GLOBAL_FIRM_NO = String.format("%03d", firm.getNR());
        } else {
            GLOBAL_FIRM_NO = String.format("%03d", firmNo);
        }

        List<Period> periods = mainRepository.getPeriodList(Integer.parseInt(GLOBAL_FIRM_NO));

        if (GLOBAL_PERIOD == null) {
            Period period = periods.get(0);
            GLOBAL_PERIOD = String.format("%02d", period.getNR());
        } else {
            GLOBAL_PERIOD = String.format("%02d", periodNo);
        }*/
    }
}
