package kg.bakirov.alpha.model.accounts;

public class AccountFiche {
    private String code;
    private String name;
    private double debit;
    private double debitUsd;
    private double credit;
    private double creditUsd;
    private String definition;

    public AccountFiche() {
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

    public double getDebit() {
        return debit;
    }

    public void setDebit(double debit) {
        this.debit = debit;
    }

    public double getDebitUsd() {
        return debitUsd;
    }

    public void setDebitUsd(double debitUsd) {
        this.debitUsd = debitUsd;
    }

    public double getCredit() {
        return credit;
    }

    public void setCredit(double credit) {
        this.credit = credit;
    }

    public double getCreditUsd() {
        return creditUsd;
    }

    public void setCreditUsd(double creditUsd) {
        this.creditUsd = creditUsd;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }
}
