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
                        rs.getString("email"))
        );
        return result;
    }
}
