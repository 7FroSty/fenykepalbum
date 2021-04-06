package hu.fenykep.demo.model;

public class Nevezes {
    private int id;
    private int verseny_id;
    private int kep_id;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setKep_id(int kep_id) {
        this.kep_id = kep_id;
    }

    public int getKep_id() {
        return kep_id;
    }

    public int getVerseny_id() {
        return verseny_id;
    }

    public void setVerseny_id(int verseny_id) {
        this.verseny_id = verseny_id;
    }
}
