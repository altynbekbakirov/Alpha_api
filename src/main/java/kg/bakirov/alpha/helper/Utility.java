package kg.bakirov.alpha.helper;

import kg.bakirov.alpha.model.company.Firm;
import kg.bakirov.alpha.model.company.Period;
import kg.bakirov.alpha.repository.MainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import static kg.bakirov.alpha.repository.MainRepository.GLOBAL_FIRM_NO;
import static kg.bakirov.alpha.repository.MainRepository.GLOBAL_PERIOD;

@Component
public class Utility {

    private final MainRepository mainRepository;

    @Autowired
    public Utility(MainRepository mainRepository) {
        this.mainRepository = mainRepository;
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
