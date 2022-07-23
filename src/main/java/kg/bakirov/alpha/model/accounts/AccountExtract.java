package kg.bakirov.alpha.model.accounts;

public class AccountExtract {

    private String code;
    private String name;
    private String date;
    private int trcode;
    private String ficheno;
    private String description;
    private Double debit;
    private Double credit;
    private Double balance;
    private Double balanceBefore;

    public AccountExtract() {
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

    public int getTrcode() {
        return trcode;
    }

    public void setTrcode(int trcode) {
        this.trcode = trcode;
    }

    public String getFicheno() {
        return ficheno;
    }

    public void setFicheno(String ficheno) {
        this.ficheno = ficheno;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getBalanceBefore() {
        return balanceBefore;
    }

    public void setBalanceBefore(Double balanceBefore) {
        this.balanceBefore = balanceBefore;
    }
}
