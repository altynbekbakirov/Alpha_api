package kg.bakirov.alpha.service;

import kg.bakirov.alpha.exception.NotFoundException;
import kg.bakirov.alpha.model.purchases.*;
import kg.bakirov.alpha.repository.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;

    @Autowired
    public PurchaseService(PurchaseRepository purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }

    public List<PurchaseFiches> getPurchases(int firmNo, int periodNo, String begDate, String endDate, int sourceIndex, String operationType, String filterCode) throws NotFoundException {
        List<PurchaseFiches> purchases = purchaseRepository.getPurchases(firmNo, periodNo, begDate, endDate, sourceIndex, operationType, filterCode);
        if (purchases.size() == 0) throw new NotFoundException("No records");
        return purchases;
    }

    public List<PurchaseFiche> getFiche(int firmNo, int periodNo, int fiche) throws NotFoundException {
        List<PurchaseFiche> purchases = purchaseRepository.getFiche(firmNo, periodNo, fiche);
        if (purchases.size() == 0) throw new NotFoundException("No records");
        return purchases;
    }

    public List<PurchaseTotal> getPurchasesTotal(int firmNo, int periodNo, String begDate, String endDate, int sourceIndex, String filterName) throws NotFoundException {
        List<PurchaseTotal> totals = purchaseRepository.getPurchasesTotal(firmNo, periodNo, begDate, endDate, sourceIndex, filterName);
        if (totals.size() == 0) throw new NotFoundException("No records");
        return totals;
    }

    public List<PurchaseMonth> getPurchasesMonth(int firmNo, int periodNo, String begDate, String endDate, int sourceIndex) throws NotFoundException {
        List<PurchaseMonth> months = purchaseRepository.getPurchasesMonth(firmNo, periodNo, begDate, endDate, sourceIndex);
        if (months.size() == 0) throw new NotFoundException("No records");
        return months;
    }

    public List<PurchaseClient> getPurchasesClient(int firmNo, int periodNo, String begDate, String endDate, int sourceIndex, String filterName) throws NotFoundException {
        List<PurchaseClient> clients = purchaseRepository.getPurchasesClient(firmNo, periodNo, begDate, endDate, sourceIndex, filterName);
        if (clients.size() == 0) throw new NotFoundException("No records");
        return clients;
    }

    public List<PurchaseClientFiches> getPurchasesClientFiches(int firmNo, int periodNo, String begDate, String endDate, int sourceIndex, String code) throws NotFoundException {
        List<PurchaseClientFiches> clients = purchaseRepository.getPurchasesClientFiches(firmNo, periodNo, begDate, endDate, sourceIndex, code);
        if (clients.size() == 0) throw new NotFoundException("No records");
        return clients;
    }
}
