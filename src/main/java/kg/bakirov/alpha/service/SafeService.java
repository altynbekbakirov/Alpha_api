package kg.bakirov.alpha.service;

import kg.bakirov.alpha.exception.NotFoundException;
import kg.bakirov.alpha.model.accounts.*;
import kg.bakirov.alpha.model.safes.Safe;
import kg.bakirov.alpha.model.safes.SafeExtract;
import kg.bakirov.alpha.model.safes.SafeResume;
import kg.bakirov.alpha.repository.AccountRepository;
import kg.bakirov.alpha.repository.SafeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SafeService {

    private final SafeRepository safeRepository;

    @Autowired
    public SafeService(SafeRepository safeRepository) {
        this.safeRepository = safeRepository;
    }

    public List<Safe> getSafes(int firmNo, int periodNo) throws NotFoundException {
        List<Safe> list = safeRepository.getSafes(firmNo, periodNo);
        if (list.size() == 0) throw new NotFoundException("No records");
        return list;
    }

    public List<SafeExtract> getSafesExtracts(int firmNo, int periodNo, String begdate, String enddate) throws NotFoundException {
        List<SafeExtract> list = safeRepository.getSafesExtract(firmNo, periodNo, begdate, enddate);
        if (list.size() == 0) throw new NotFoundException("No records");
        return list;
    }

    public List<SafeExtract> getSafeExtracts(int firmNo, int periodNo, String begdate, String enddate, String code) throws NotFoundException {
        List<SafeExtract> list = safeRepository.getSafeExtract(firmNo, periodNo, begdate, enddate, code);
        if (list.size() == 0) throw new NotFoundException("No records");
        return list;
    }

    public List<SafeResume> getSafeResume(int firmNo, int periodNo, String begdate, String enddate, String code) throws NotFoundException {
        List<SafeResume> list = safeRepository.getSafeResume(firmNo, periodNo, begdate, enddate, code);
        if (list.size() == 0) throw new NotFoundException("No records");
        return list;
    }

}
