package kg.bakirov.alpha.helper;

import org.springframework.stereotype.Component;
import static kg.bakirov.alpha.repository.MainRepository.GLOBAL_FIRM_NO;
import static kg.bakirov.alpha.repository.MainRepository.GLOBAL_PERIOD;

@Component
public class Utility {

    public void CheckCompany(int firmNo, int periodNo) {

        GLOBAL_FIRM_NO = String.format("%03d", firmNo);
        GLOBAL_PERIOD = String.format("%02d", periodNo);

    }
}
