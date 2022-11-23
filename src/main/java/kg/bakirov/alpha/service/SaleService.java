package kg.bakirov.alpha.service;

import kg.bakirov.alpha.exception.NotFoundException;
import kg.bakirov.alpha.model.sales.*;
import kg.bakirov.alpha.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SaleService {

    private final SaleRepository saleRepository;

    @Autowired
    public SaleService(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    public List<SaleFiches> getSales(int firmNo, int periodNo, String begDate, String endDate, int sourceIndex, String operationType, String filterCode) throws NotFoundException {
        List<SaleFiches> sales = saleRepository.getSales(firmNo, periodNo, begDate, endDate, sourceIndex, operationType, filterCode);
        if (sales.size() == 0) throw new NotFoundException("No records");
        return sales;
    }

    public List<SaleFiche> getFiche(int firmNo, int periodNo, int fiche) throws NotFoundException {
        List<SaleFiche> sales = saleRepository.getFiche(firmNo, periodNo, fiche);
        if (sales.size() == 0) throw new NotFoundException("No records");
        return sales;
    }

    public List<SaleTotal> getSalesTotal(int firmNo, int periodNo, String begDate, String endDate, int sourceIndex, String filterName) throws NotFoundException {
        List<SaleTotal> sales = saleRepository.getSalesTotal(firmNo, periodNo, begDate, endDate, sourceIndex, filterName);
        if (sales.size() == 0) throw new NotFoundException("No records");
        return sales;
    }

    public List<SaleMonth> getSalesMonth(int firmNo, int periodNo, String begDate, String endDate, int sourceIndex) throws NotFoundException {
        List<SaleMonth> sales = saleRepository.getSalesMonth(firmNo, periodNo, begDate, endDate, sourceIndex);
        if (sales.size() == 0) throw new NotFoundException("No records");
        return sales;
    }

    public List<SaleDaily> getSalesDaily(int firmNo, int periodNo, String begDate, String endDate, int sourceIndex) throws NotFoundException {
        List<SaleDaily> sales = saleRepository.getSalesDaily(firmNo, periodNo, begDate, endDate, sourceIndex);
        if (sales.size() == 0) throw new NotFoundException("No records");
        return sales;
    }

    public List<SaleClientManager> getSalesManager(int firmNo, int periodNo, String begDate, String endDate, int sourceIndex, String filterName) throws NotFoundException {
        List<SaleClientManager> sales = saleRepository.getSalesManager(firmNo, periodNo, begDate, endDate, sourceIndex, filterName);
        if (sales.size() == 0) throw new NotFoundException("No records");
        return sales;
    }

    public List<SaleClientManager> getSalesManagerOne(int firmNo, int periodNo, String begDate, String endDate, int sourceIndex, String code) throws NotFoundException {
        List<SaleClientManager> sales = saleRepository.getSalesManagerOne(firmNo, periodNo, begDate, endDate, sourceIndex, code);
        if (sales.size() == 0) throw new NotFoundException("No records");
        return sales;
    }

    public List<SaleClient> getSalesClient(int firmNo, int periodNo, String begDate, String endDate, int sourceIndex, String filterName) throws NotFoundException {
        List<SaleClient> sales = saleRepository.getSalesClient(firmNo, periodNo, begDate, endDate, sourceIndex, filterName);
        if (sales.size() == 0) throw new NotFoundException("No records");
        return sales;
    }

    public List<SaleClientFiches> getSalesClientFiches(int firmNo, int periodNo, String begDate, String endDate, int sourceIndex, String code) throws NotFoundException {
        List<SaleClientFiches> sales = saleRepository.getSalesClientFiches(firmNo, periodNo, begDate, endDate, sourceIndex, code);
        if (sales.size() == 0) throw new NotFoundException("No records");
        return sales;
    }

    public List<SaleClientTop> getSalesClientTop(int firmNo, int periodNo, String begDate, String endDate, int sourceIndex) throws NotFoundException {
        List<SaleClientTop> sales = saleRepository.getSalesClientTop(firmNo, periodNo, begDate, endDate, sourceIndex);
        if (sales.size() == 0) throw new NotFoundException("No records");
        return sales;
    }

    public List<SaleTable> getSalesTable(int firmNo, int periodNo, String begDate, String endDate, int sourceIndex, String filterName) throws NotFoundException {
        List<SaleTable> sales = saleRepository.getSalesTable(firmNo, periodNo, begDate, endDate, sourceIndex, filterName);
        if (sales.size() == 0) throw new NotFoundException("No records");
        return sales;

    }

    public List<SaleDetail> getSalesDetail(int firmNo, int periodNo, String begDate, String endDate, int sourceIndex, String filterName) throws NotFoundException {
        List<SaleDetail> sales = saleRepository.getSalesDetail(firmNo, periodNo, begDate, endDate, sourceIndex, filterName);
        if (sales.size() == 0) throw new NotFoundException("No records");
        return sales;
    }
}
