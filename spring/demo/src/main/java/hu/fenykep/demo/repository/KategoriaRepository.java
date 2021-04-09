package hu.fenykep.demo.repository;

import hu.fenykep.demo.model.Ertekeles;
import hu.fenykep.demo.model.Kategoria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class KategoriaRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<Kategoria> findAll(){
        List<Kategoria> result = jdbcTemplate.query(
                "SELECT * FROM Kategoria",
                (rs, rowNum) -> new Kategoria(
                        rs.getInt("id"),
                        rs.getString("nev"))
        );
        return result;
    }
}
