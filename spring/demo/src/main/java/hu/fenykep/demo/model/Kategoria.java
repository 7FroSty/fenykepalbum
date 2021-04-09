package hu.fenykep.demo.model;

import hu.fenykep.demo.repository.KategoriaRepository;

import java.io.Serializable;

public class Kategoria implements Serializable {
    private int id;
    private String nev;

    public Kategoria(int id, String nev){
        this.id=id;
        this.nev=nev;
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
