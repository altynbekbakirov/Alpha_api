package kg.bakirov.alpha.model.accounts;

public class Account {
    private String code;
    private String name;
    private String address;
    private String phone;
    private double debit;
    private double credit;
    private double balance;

    public Account(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public Account(String code, String name, String address, String phone, double debit, double credit, double balance) {
        this.code = code;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.debit = debit;
        this.credit = credit;
        this.balance = balance;
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

    public double getDebit() {
        return debit;
    }

    public void setDebit(double debit) {
        this.debit = debit;
    }

    public double getCredit() {
        return credit;
    }

    public void setCredit(double credit) {
        this.credit = credit;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
