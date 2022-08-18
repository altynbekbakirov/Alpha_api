package kg.bakirov.alpha.model.accounts;

public class AccountFiches {
    private String date;
    private String ficheNo;
    private int trCode;
    private double debit;
    private double credit;
    private double repDebit;
    private double repCredit;
    private String definition;

    public AccountFiches(String date, String ficheNo, int trCode, double debit, double credit, double repDebit, double repCredit, String definition) {
        this.date = date;
        this.ficheNo = ficheNo;
        this.trCode = trCode;
        this.debit = debit;
        this.credit = credit;
        this.repDebit = repDebit;
        this.repCredit = repCredit;
        this.definition = definition;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFicheNo() {
        return ficheNo;
    }

    public void setFicheNo(String ficheNo) {
        this.ficheNo = ficheNo;
    }

    public int getTrCode() {
        return trCode;
    }

    public void setTrCode(int trCode) {
        this.trCode = trCode;
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

    public double getRepDebit() {
        return repDebit;
    }

    public void setRepDebit(double repDebit) {
        this.repDebit = repDebit;
    }

    public double getRepCredit() {
        return repCredit;
    }

    public void setRepCredit(double repCredit) {
        this.repCredit = repCredit;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }
}
