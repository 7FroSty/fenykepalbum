package hu.fenykep.demo.model;

public class Felhasznalo {
    private int id;
    private String nev;
    private String email;
    private String jelszo;
    private int iranyitoszam;
    private String telepules;
    private String utca;
    private int hazszam;
    private Boolean admin;

    public Felhasznalo(String nev, String email){
        this.nev=nev;
        this.email=email;
    }

    @Override
    public String toString(){
        return this.nev;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public int getHazszam() {
        return hazszam;
    }

    public void setHazszam(int hazszam) {
        this.hazszam = hazszam;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIranyitoszam() {
        return iranyitoszam;
    }

    public void setIranyitoszam(int iranyitoszam) {
        this.iranyitoszam = iranyitoszam;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getJelszo() {
        return jelszo;
    }

    public void setJelszo(String jelszo) {
        this.jelszo = jelszo;
    }

    public String getNev() {
        return nev;
    }

    public void setNev(String nev) {
        this.nev = nev;
    }

    public String getTelepules() {
        return telepules;
    }

    public void setTelepules(String telepules) {
        this.telepules = telepules;
    }

    public String getUtca() {
        return utca;
    }

    public void setUtca(String utca) {
        this.utca = utca;
    }
}

