package hu.fenykep.demo.model;

public class Nevezes {
    private int id;
    private Kep kep;
    private int szavazat_db;

    public Nevezes(int id, Kep kep, int szavazat_db) {
        this.id = id;
        this.kep = kep;
        this.szavazat_db = szavazat_db;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Kep getKep() {
        return kep;
    }

    public void setKep(Kep kep) {
        this.kep = kep;
    }

    public int getSzavazat_db() {
        return szavazat_db;
    }

    public void setSzavazat_db(int szavazat_db) {
        this.szavazat_db = szavazat_db;
    }
}
