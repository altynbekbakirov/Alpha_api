package kg.bakirov.alpha.model.accounts;

public class AccountDebit {

    private String code;
    private String name;
    private String address;
    private String phone;
    private Double debit;
    private Double credit;
    private Double balance;
    private Double debitUsd;
    private Double creditUsd;
    private Double balanceUsd;

    public AccountDebit() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Double getDebit() {
        return debit;
    }

    public void setDebit(Double debit) {
        this.debit = debit;
    }

    public Double getCredit() {
        return credit;
    }

    public void setCredit(Double credit) {
        this.credit = credit;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Double getDebitUsd() {
        return debitUsd;
    }

    public void setDebitUsd(Double debitUsd) {
        this.debitUsd = debitUsd;
    }

    public Double getCreditUsd() {
        return creditUsd;
    }

    public void setCreditUsd(Double creditUsd) {
        this.creditUsd = creditUsd;
    }

    public Double getBalanceUsd() {
        return balanceUsd;
    }

    public void setBalanceUsd(Double balanceUsd) {
        this.balanceUsd = balanceUsd;
    }
}
