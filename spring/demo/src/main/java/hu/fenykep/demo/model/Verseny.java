package hu.fenykep.demo.model;

import java.sql.Timestamp;
import java.util.Date;

public class Verseny {
    private int id;
    private String cim;
    private String szoveg;
    private Timestamp szavazas_kezdete;
    private Timestamp szavazas_vege;

    public Verseny(int id, String cim, String szoveg, Timestamp szk, Timestamp szv){
        this.id=id;
        this.cim=cim;
        this.szoveg=szoveg;
        this.szavazas_kezdete=szk;
        this.szavazas_vege=szv;
    }

    public boolean isAktiv() {
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        return currentTimestamp.after(szavazas_kezdete) && currentTimestamp.before(szavazas_vege);
    }

    public String getCim() {
        return cim;
    }

    public String getSzoveg() {
        return szoveg;
    }

    public void setCim(String cim) {
        this.cim = cim;
    }

    public void setSzoveg(String szoveg) {
        this.szoveg = szoveg;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getSzavazas_kezdete() {
        return szavazas_kezdete;
    }

    public void setSzavazas_kezdete(Timestamp szavazas_kezdete) {
        this.szavazas_kezdete = szavazas_kezdete;
    }

    public Timestamp getSzavazas_vege() {
        return szavazas_vege;
    }

    public void setSzavazas_vege(Timestamp szavazas_vege) {
        this.szavazas_vege = szavazas_vege;
    }
}
