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

    public List<SaleFiche> getSales(int firmNo, int periodNo) throws NotFoundException {
        List<SaleFiche> sales = saleRepository.getSales(firmNo, periodNo);
        if (sales.size() == 0) throw new NotFoundException("No records");
        return sales;
    }

    public List<SaleTotal> getSalesTotal(int firmNo, int periodNo) throws NotFoundException {
        List<SaleTotal> sales = saleRepository.getSalesTotal(firmNo, periodNo);
        if (sales.size() == 0) throw new NotFoundException("No records");
        return sales;
    }

    public List<SaleMonth> getSalesMonth(int firmNo, int periodNo) throws NotFoundException {
        List<SaleMonth> sales = saleRepository.getSalesMonth(firmNo, periodNo);
        if (sales.size() == 0) throw new NotFoundException("No records");
        return sales;
    }

    public List<SaleClientManager> getSalesManager(int firmNo, int periodNo) throws NotFoundException {
        List<SaleClientManager> sales = saleRepository.getSalesManager(firmNo, periodNo);
        if (sales.size() == 0) throw new NotFoundException("No records");
        return sales;
    }

    public List<SaleClient> getSalesClient(int firmNo, int periodNo) throws NotFoundException {
        List<SaleClient> sales = saleRepository.getSalesClient(firmNo, periodNo);
        if (sales.size() == 0) throw new NotFoundException("No records");
        return sales;
    }

    public List<SaleTable> getSalesTable(int firmNo, int periodNo) throws NotFoundException {
        List<SaleTable> sales = saleRepository.getSalesTable(firmNo, periodNo);
        if (sales.size() == 0) throw new NotFoundException("No records");
        return sales;

    }

    public List<SaleDetail> getSalesDetail(int firmNo, int periodNo) throws NotFoundException {
        List<SaleDetail> sales = saleRepository.getSalesDetail(firmNo, periodNo);
        if (sales.size() == 0) throw new NotFoundException("No records");
        return sales;
    }
}
