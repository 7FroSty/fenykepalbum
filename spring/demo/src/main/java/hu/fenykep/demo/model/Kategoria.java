package hu.fenykep.demo.model;

import hu.fenykep.demo.repository.KategoriaRepository;

import java.io.Serializable;

public class Kategoria implements Serializable {
    private int id;
    private String nev;
    private int kepdb;

    public Kategoria(int id, String nev){
        this.id=id;
        this.nev=nev;
    }

    public Kategoria(int id, String nev, int kepdb) {
        this.id = id;
        this.nev = nev;
        this.kepdb = kepdb;
    }

    public int getKepdb() {
        return kepdb;
    }

    public void setKepdb(int kepdb) {
        this.kepdb = kepdb;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNev() {
        return nev;
    }

    public void setNev(String nev) {
        this.nev = nev;
    }
}
