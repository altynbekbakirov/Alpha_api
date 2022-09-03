package kg.bakirov.alpha.service;

import kg.bakirov.alpha.exception.NotFoundException;
import kg.bakirov.alpha.model.company.Firm;
import kg.bakirov.alpha.model.company.Period;
import kg.bakirov.alpha.model.company.WareHouse;
import kg.bakirov.alpha.repository.MainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MainService {

    private final MainRepository mainRepository;

    @Autowired
    public MainService(MainRepository mainRepository) {
        this.mainRepository = mainRepository;
    }

    public List<Firm> getCompanyList() throws NotFoundException {
        List<Firm> companies = mainRepository.getFirmList();
        if (companies.size() == 0) throw new NotFoundException("No records");
        return companies;
    }

    public List<Period> getPeriodList(int firmNo) throws NotFoundException {
        List<Period> periodList = mainRepository.getPeriodList(firmNo);
        if (periodList.size() == 0) throw new NotFoundException("No records");
        return periodList;
    }

    public List<WareHouse> getWareList(int firmNo) throws NotFoundException {
        List<WareHouse> wareHouseList = mainRepository.getWareHouseList(firmNo);
        if (wareHouseList.size() == 0) throw new NotFoundException("No records");
        return wareHouseList;
    }

    public String networkClear(int firmNo) throws NotFoundException {
        mainRepository.networkClear(firmNo);
        return "Ok";
    }

}
