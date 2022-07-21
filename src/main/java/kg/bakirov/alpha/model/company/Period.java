package kg.bakirov.alpha.model.company;

public class Period {

    private int NR;
    private int FIRMNR;
    private String BEGDATE;
    private String ENDDATE;
    private int ACTIVE;

    public Period(int NR, int FIRMNR, String BEGDATE, String ENDDATE, int ACTIVE) {
        this.NR = NR;
        this.FIRMNR = FIRMNR;
        this.BEGDATE = BEGDATE;
        this.ENDDATE = ENDDATE;
        this.ACTIVE = ACTIVE;
    }

    public int getNR() {
        return NR;
    }

    public void setNR(int NR) {
        this.NR = NR;
    }

    public int getFIRMNR() {
        return FIRMNR;
    }

    public void setFIRMNR(int FIRMNR) {
        this.FIRMNR = FIRMNR;
    }

    public String getBEGDATE() {
        return BEGDATE;
    }

    public void setBEGDATE(String BEGDATE) {
        this.BEGDATE = BEGDATE;
    }

    public String getENDDATE() {
        return ENDDATE;
    }

    public void setENDDATE(String ENDDATE) {
        this.ENDDATE = ENDDATE;
    }

    public int getACTIVE() {
        return ACTIVE;
    }

    public void setACTIVE(int ACTIVE) {
        this.ACTIVE = ACTIVE;
    }
}
