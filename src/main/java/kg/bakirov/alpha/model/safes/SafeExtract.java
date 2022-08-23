package kg.bakirov.alpha.model.safes;

public class SafeExtract {
    private String date;
    private String ficheNo;
    private String title;
    private String definition;
    private int trCode;
    private double collection;
    private double collectionUsd;
    private double payment;
    private double paymentUsd;
    private byte hour;
    private byte minute;

    public SafeExtract() {
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public int getTrCode() {
        return trCode;
    }

    public void setTrCode(int trCode) {
        this.trCode = trCode;
    }

    public double getCollection() {
        return collection;
    }

    public void setCollection(double collection) {
        this.collection = collection;
    }

    public double getCollectionUsd() {
        return collectionUsd;
    }

    public void setCollectionUsd(double collectionUsd) {
        this.collectionUsd = collectionUsd;
    }

    public double getPayment() {
        return payment;
    }

    public void setPayment(double payment) {
        this.payment = payment;
    }

    public double getPaymentUsd() {
        return paymentUsd;
    }

    public void setPaymentUsd(double paymentUsd) {
        this.paymentUsd = paymentUsd;
    }

    public byte getHour() {
        return hour;
    }

    public void setHour(byte hour) {
        this.hour = hour;
    }

    public byte getMinute() {
        return minute;
    }

    public void setMinute(byte minute) {
        this.minute = minute;
    }
}
