package hu.fenykep.demo.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Felhasznalo implements UserDetails {
    private int id;
    private String nev;
    private String email;
    private String jelszo;
    private int iranyitoszam;
    private String telepules;
    private String utca;
    private String hazszam;
    private Boolean admin;

    public Felhasznalo(int id, String nev, String email) {
        this.id = id;
        this.nev = nev;
        this.email = email;
    }

    public Felhasznalo(int id, String nev, String email, String jelszo, int iranyitoszam,
                       String telepules, String utca, String hazszam, Boolean admin) {
        this.id = id;
        this.nev = nev;
        this.email = email;
        this.jelszo = jelszo;
        this.iranyitoszam = iranyitoszam;
        this.telepules = telepules;
        this.utca = utca;
        this.hazszam = hazszam;
        this.admin = admin;
    }

    @Override
    public String toString() {
        return "Felhasznalo[" + this.email + ", " + this.nev + "]";
    }

    public Boolean isAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public String getHazszam() {
        return hazszam;
    }

    public void setHazszam(String hazszam) {
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

    /*
     * UserDetails metódusok
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorityList = new ArrayList<>();
        authorityList.add(new SimpleGrantedAuthority("ROLE_FELHASZNALO"));
        if (this.isAdmin()) {
            authorityList.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        return authorityList;
    }

    @Override
    public String getPassword() {
        return this.getJelszo();
    }

    @Override
    public String getUsername() {
        return this.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

