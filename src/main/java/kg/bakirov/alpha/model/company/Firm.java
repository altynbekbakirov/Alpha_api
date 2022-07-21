package kg.bakirov.alpha.model.company;

public class Firm {

    private int NR;
    private String NAME;
    private String TITLE;

    public Firm(int NR, String NAME, String TITLE) {
        this.NR = NR;
        this.NAME = NAME;
        this.TITLE = TITLE;
    }

    public int getNR() {
        return NR;
    }

    public void setNR(int NR) {
        this.NR = NR;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getTITLE() {
        return TITLE;
    }

    public void setTITLE(String TITLE) {
        this.TITLE = TITLE;
    }
}
