package kg.bakirov.alpha.model.products;

public class ResponseModel {
    private int firmno;
    private int periodno;
    private String begdate;
    private String enddate;
    private int sourceindex;

    public ResponseModel(int firmno, int periodno, String begdate, String enddate, int sourceindex) {
        this.firmno = firmno;
        this.periodno = periodno;
        this.begdate = begdate;
        this.enddate = enddate;
        this.sourceindex = sourceindex;
    }

    public int getFirmno() {
        return firmno;
    }

    public void setFirmno(int firmno) {
        this.firmno = firmno;
    }

    public int getPeriodno() {
        return periodno;
    }

    public void setPeriodno(int periodno) {
        this.periodno = periodno;
    }

    public String getBegdate() {
        return begdate;
    }

    public void setBegdate(String begdate) {
        this.begdate = begdate;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    public int getSourceindex() {
        return sourceindex;
    }

    public void setSourceindex(int sourceindex) {
        this.sourceindex = sourceindex;
    }

}
