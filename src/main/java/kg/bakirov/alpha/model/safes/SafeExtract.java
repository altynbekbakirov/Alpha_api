package kg.bakirov.alpha.model.safes;

public class SafeExtract {
    private String date;
    private String ficheNo;
    private String title;
    private String definition;
    private int trCode;
    private byte sign;
    private double net;
    private double netUsd;
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

    public byte getSign() {
        return sign;
    }

    public void setSign(byte sign) {
        this.sign = sign;
    }

    public double getNet() {
        return net;
    }

    public void setNet(double net) {
        this.net = net;
    }

    public double getNetUsd() {
        return netUsd;
    }

    public void setNetUsd(double netUsd) {
        this.netUsd = netUsd;
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
