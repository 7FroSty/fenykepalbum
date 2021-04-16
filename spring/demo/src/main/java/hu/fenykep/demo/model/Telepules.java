package hu.fenykep.demo.model;

public class Telepules {
    private String nev;
    private int kepdb;

    public Telepules(String nev, int kepdb) {
        this.nev = nev;
        this.kepdb = kepdb;
    }

    public String getNev() {
        return nev;
    }

    public void setNev(String nev) {
        this.nev = nev;
    }

    public int getKepdb() {
        return kepdb;
    }

    public void setKepdb(int kepdb) {
        this.kepdb = kepdb;
    }
}
