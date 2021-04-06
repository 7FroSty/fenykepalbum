package hu.fenykep.demo.model;

import java.util.Date;

public class Bejegyzes {
    private int id;
    private int felhasznalo_id;
    private String cim;
    private String szoveg;
    private Date idopont;

    public int getFelhasznalo_id() {
        return felhasznalo_id;
    }

    public void setFelhasznalo_id(int felhasznalo_id) {
        this.felhasznalo_id = felhasznalo_id;
    }

    public void setIdopont(Date idopont) {
        this.idopont = idopont;
    }

    public Date getIdopont() {
        return idopont;
    }

    public void setSzoveg(String szoveg) {
        this.szoveg = szoveg;
    }

    public String getSzoveg() {
        return szoveg;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setCim(String cim) {
        this.cim = cim;
    }

    public String getCim() {
        return cim;
    }
}
