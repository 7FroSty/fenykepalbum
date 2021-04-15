package hu.fenykep.demo.model;

public class Ertekeles {
    private int felhasznalo_id;
    private int kep_id;
    private int csillagok;

    public Ertekeles(int felhasznalo_id, int kep_id, int csillagok) {
        this.felhasznalo_id = felhasznalo_id;
        this.kep_id = kep_id;
        this.csillagok = csillagok;
    }

    public void setKep_id(int kep_id) {
        this.kep_id = kep_id;
    }

    public int getKep_id() {
        return kep_id;
    }

    public void setFelhasznalo_id(int felhasznalo_id) {
        this.felhasznalo_id = felhasznalo_id;
    }

    public int getFelhasznalo_id() {
        return felhasznalo_id;
    }

    public int getCsillagok() {
        return csillagok;
    }

    public void setCsillagok(int csillagok) {
        this.csillagok = csillagok;
    }
}
