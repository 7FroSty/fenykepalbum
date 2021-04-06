package hu.fenykep.demo.repository;

import hu.fenykep.demo.model.Felhasznalo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FelhasznaloRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<Felhasznalo> findAll(){
        List<Felhasznalo> result = jdbcTemplate.query(
                "SELECT * FROM Felhasznalo",
                (rs, rowNum) -> new Felhasznalo(
                        rs.getString("nev"),
                        rs.getString("email"),
                        rs.getString("jelszo"),
                        rs.getInt("iranyitoszam"),
                        rs.getString("telepules"),
                        rs.getString("utca"),
                        rs.getString("hazszam"),
                        rs.getBoolean("admin"))
        );
        return result;
    }

    public void insert(String nev, String email, String jelszo, int iranyitoszam,
                         String telepules, String utca, String hazszam){
        jdbcTemplate.update("INSERT INTO Felhasznalo(NEV, EMAIL, JELSZO, IRANYITOSZAM, " +
                "TELEPULES, UTCA, HAZSZAM, ADMIN) VALUES (?, ?, ?, ?, ?, ?, ?, ?)", new Object[]{
                nev, email, jelszo, iranyitoszam, telepules, utca, hazszam, false
        });
    }

    public Felhasznalo getFelhasznaloByEmail(String email){
        List<Felhasznalo> lista = this.findAll();
        Felhasznalo result=null;
        for(int i=0; i<lista.size(); i++){
            System.out.println(lista.get(i).getEmail());
            if(lista.get(i).getEmail().equals(email)){
                result = lista.get(i);
            }
        }
        return result;
    }
}
