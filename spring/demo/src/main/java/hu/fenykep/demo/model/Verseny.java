package hu.fenykep.demo.model;

import java.util.Date;

public class Verseny {
    private int id;
    private Date szavazas_kezdete;
    private Date szavazas_vege;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getSzavazas_kezdete() {
        return szavazas_kezdete;
    }

    public void setSzavazas_kezdete(Date szavazas_kezdete) {
        this.szavazas_kezdete = szavazas_kezdete;
    }

    public Date getSzavazas_vege() {
        return szavazas_vege;
    }

    public void setSzavazas_vege(Date szavazas_vege) {
        this.szavazas_vege = szavazas_vege;
    }
}
