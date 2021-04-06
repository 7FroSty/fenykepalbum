package hu.fenykep.demo.model;

import java.util.Date;

public class Komment {
    private int felhasznalo_id;
    private int kep_id;
    private String szoveg;
    private Date idopont;

    public int getFelhasznalo_id() {
        return felhasznalo_id;
    }

    public void setFelhasznalo_id(int felhasznalo_id) {
        this.felhasznalo_id = felhasznalo_id;
    }

    public int getKep_id() {
        return kep_id;
    }

    public void setKep_id(int kep_id) {
        this.kep_id = kep_id;
    }

    public void setIdopont(Date idopont) {
        this.idopont = idopont;
    }

    public Date getIdopont() {
        return idopont;
    }

    public String getSzoveg() {
        return szoveg;
    }

    public void setSzoveg(String szoveg) {
        this.szoveg = szoveg;
    }
}
