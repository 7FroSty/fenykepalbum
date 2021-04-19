package hu.fenykep.demo.model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Kep implements Serializable {
    private int id;
    private int felhasznalo_id;
    private Felhasznalo felhasznalo;
    private int kategoria_id;
    private Kategoria kategoria;
    private String cim;
    private String tartalom;
    private Date idopont;
    private String telepules;
    private double csillagok; // Átlag: [0; 5]

    public Kep() {
    }

    public Kep(int id, int felhasznalo_id, int kategoria_id, String cim, String tartalom, Date idopont, String telepules) {
        this.id = id;
        this.felhasznalo_id = felhasznalo_id;
        this.kategoria_id = kategoria_id;
        this.cim = cim;
        this.tartalom = tartalom;
        this.idopont = idopont;
        this.telepules = telepules;
    }

    public String getDateAsString() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String strDate = dateFormat.format(this.idopont);
        return strDate;
    }

    public double getCsillagok() {
        return csillagok;
    }

    public void setCsillagok(double csillagok) {
        this.csillagok = csillagok;
    }

    public void setTelepules(String telepules) {
        this.telepules = telepules;
    }

    public String getTelepules() {
        return telepules;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTartalom() {
        return tartalom;
    }

    public void setTartalom(String tartalom) {
        this.tartalom = tartalom;
    }

    public Date getIdopont() {
        return idopont;
    }

    public void setIdopont(Date idopont) {
        this.idopont = idopont;
    }

    public int getFelhasznalo_id() {
        return felhasznalo_id;
    }

    public void setFelhasznalo_id(int felhasznalo_id) {
        this.felhasznalo_id = felhasznalo_id;
    }

    public int getKategoria_id() {
        return kategoria_id;
    }

    public void setKategoria_id(int kategoria_id) {
        this.kategoria_id = kategoria_id;
    }

    public Felhasznalo getFelhasznalo() {
        return felhasznalo;
    }

    public void setFelhasznalo(Felhasznalo felhasznalo) {
        this.felhasznalo = felhasznalo;
    }

    public Kategoria getKategoria() {
        return kategoria;
    }

    public void setKategoria(Kategoria kategoria) {
        this.kategoria = kategoria;
    }

    public String getCim() {
        return cim;
    }

    public void setCim(String cim) {
        this.cim = cim;
    }
}
