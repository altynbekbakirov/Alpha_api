package kg.bakirov.alpha.service;

import kg.bakirov.alpha.exception.NotFoundException;
import kg.bakirov.alpha.model.accounts.*;
import kg.bakirov.alpha.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<AccountDebit> getAccountDebit(int firmNo, int periodNo, String begDate, String endDate, String filterName) throws NotFoundException {
        List<AccountDebit> productList = accountRepository.getAccountDebit(firmNo, periodNo, begDate, endDate, filterName);
        if (productList.size() == 0) throw new NotFoundException("No records");
        return productList;
    }

    public List<Account> getAccounts(int firmNo, int periodNo, String begDate, String endDate, String filterName) throws NotFoundException {
        List<Account> accountList = accountRepository.getAccounts(firmNo, periodNo, begDate, endDate, filterName);
        if (accountList.size() == 0) throw new NotFoundException("No records");
        return accountList;
    }

    public List<AccountExtract> getAccountExtract(int firmNo, int periodNo, String begdate, String enddate) throws NotFoundException {
        List<AccountExtract> productList = accountRepository.getAccountExtract(firmNo, periodNo, begdate, enddate);
        if (productList.size() == 0) throw new NotFoundException("No records");
        return productList;
    }

    public List<AccountExtract> getAccountExtract(int firmNo, int periodNo, String begdate, String enddate, String code) throws NotFoundException {
        List<AccountExtract> productList = accountRepository.getAccountExtract(firmNo, periodNo, begdate, enddate, code);
        if (productList.size() == 0) throw new NotFoundException("No records");
        return productList;
    }

    public List<AccountFiches> getAccountFiches(int firmNo, int periodNo, String begdate, String enddate) throws NotFoundException {
        List<AccountFiches> productList = accountRepository.getAccountFiches(firmNo, periodNo, begdate, enddate);
        if (productList.size() == 0) throw new NotFoundException("No records");
        return productList;
    }

    public List<AccountFiche> getAccountFiche(int firmNo, int periodNo, String begDate, String endDate, int code) throws NotFoundException {
        List<AccountFiche> productList = accountRepository.getAccountFiche(firmNo, periodNo, begDate, endDate, code);
        if (productList.size() == 0) throw new NotFoundException("No records");
        return productList;
    }

    public List<AccountAging> getAccountsAging(int firmNo, int periodNo, String begDate, String endDate, String date1, String date2, String date3, String date4, String date5, String filterName) throws NotFoundException {
        List<AccountAging> productList = accountRepository.getAccountsAging(firmNo, periodNo, begDate, endDate, date1, date2, date3, date4, date5, filterName);
        if (productList.size() == 0) throw new NotFoundException("No records");
        return productList;
    }

}
