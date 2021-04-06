package hu.fenykep.demo.model;

public class Szavazat {
    private int nevezes_id;
    private int felhasznalo_id;

    public void setFelhasznalo_id(int felhasznalo_id) {
        this.felhasznalo_id = felhasznalo_id;
    }

    public int getFelhasznalo_id() {
        return felhasznalo_id;
    }

    public int getNevezes_id() {
        return nevezes_id;
    }

    public void setNevezes_id(int nevezes_id) {
        this.nevezes_id = nevezes_id;
    }
}
