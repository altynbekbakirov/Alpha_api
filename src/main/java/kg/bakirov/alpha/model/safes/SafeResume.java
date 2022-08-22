package kg.bakirov.alpha.model.safes;

public class SafeResume {
    private int month;
    private double debit;
    private double debitUsd;
    private double credit;
    private double creditUsd;
    private double total;
    private double totalUsd;

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
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

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getTotalUsd() {
        return totalUsd;
    }

    public void setTotalUsd(double totalUsd) {
        this.totalUsd = totalUsd;
    }

    @Override
    public String toString() {
        return "SafeResume{" +
                "month=" + month +
                ", debit=" + debit +
                ", debitUsd=" + debitUsd +
                ", credit=" + credit +
                ", creditUsd=" + creditUsd +
                ", total=" + total +
                ", totalUsd=" + totalUsd +
                '}';
    }
}
