package kg.bakirov.alpha.service;

import kg.bakirov.alpha.exception.NotFoundException;
import kg.bakirov.alpha.model.accounts.Account;
import kg.bakirov.alpha.model.accounts.AccountDebit;
import kg.bakirov.alpha.model.accounts.AccountExtract;
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

    public List<AccountDebit> getAccountDebit(int firmNo, int periodNo) throws NotFoundException {
        List<AccountDebit> productList = accountRepository.getAccountDebit(firmNo, periodNo);
        if (productList.size() == 0) throw new NotFoundException("No records");
        return productList;
    }

    public List<Account> getAccounts(int firmNo, int periodNo) throws NotFoundException {
        List<Account> accountList = accountRepository.getAccounts(firmNo, periodNo);
        if (accountList.size() == 0) throw new NotFoundException("No records");
        return accountList;
    }

    public List<AccountExtract> getAccountExtract(int firmNo, int periodNo) throws NotFoundException {
        List<AccountExtract> productList = accountRepository.getAccountExtract(firmNo, periodNo);
        if (productList.size() == 0) throw new NotFoundException("No records");
        return productList;
    }

}