package hu.fenykep.demo.model;

import java.util.Date;

public class KepMegtekintes {
    private int felhasznalo_id;
    private int kep_id;
    private Date idopont;

    public Date getIdopont() {
        return idopont;
    }

    public void setIdopont(Date idopont) {
        this.idopont = idopont;
    }

    public int getKep_id() {
        return kep_id;
    }

    public void setKep_id(int kep_id) {
        this.kep_id = kep_id;
    }

    public void setFelhasznalo_id(int felhasznalo_id) {
        this.felhasznalo_id = felhasznalo_id;
    }

    public int getFelhasznalo_id() {
        return felhasznalo_id;
    }
}
