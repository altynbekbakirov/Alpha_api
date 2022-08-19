package kg.bakirov.alpha.model.safes;

public class Safe {
    private String code;
    private String name;
    private String definition;
    private double balance;
    private double balanceUsd;

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

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getBalanceUsd() {
        return balanceUsd;
    }

    public void setBalanceUsd(double balanceUsd) {
        this.balanceUsd = balanceUsd;
    }
}
