package kg.bakirov.alpha.service;

import kg.bakirov.alpha.exception.NotFoundException;
import kg.bakirov.alpha.model.purchases.PurchaseClient;
import kg.bakirov.alpha.model.purchases.PurchaseFiche;
import kg.bakirov.alpha.model.purchases.PurchaseMonth;
import kg.bakirov.alpha.model.purchases.PurchaseTotal;
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

    public List<PurchaseFiche> getPurchases(int firmNo, int periodNo) throws NotFoundException {
        List<PurchaseFiche> purchases = purchaseRepository.getPurchases(firmNo, periodNo);
        if (purchases.size() == 0) throw new NotFoundException("No records");
        return purchases;
    }

    public List<PurchaseTotal> getPurchasesTotal(int firmNo, int periodNo) throws NotFoundException {
        List<PurchaseTotal> totals = purchaseRepository.getPurchasesTotal(firmNo, periodNo);
        if (totals.size() == 0) throw new NotFoundException("No records");
        return totals;
    }

    public List<PurchaseMonth> getPurchasesMonth(int firmNo, int periodNo) throws NotFoundException {
        List<PurchaseMonth> months = purchaseRepository.getPurchasesMonth(firmNo, periodNo);
        if (months.size() == 0) throw new NotFoundException("No records");
        return months;
    }

    public List<PurchaseClient> getPurchasesClient(int firmNo, int periodNo) throws NotFoundException {
        List<PurchaseClient> clients = purchaseRepository.getPurchasesClient(firmNo, periodNo);
        if (clients.size() == 0) throw new NotFoundException("No records");
        return clients;
    }
}
