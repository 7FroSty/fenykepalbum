package hu.fenykep.demo.repository;

import hu.fenykep.demo.model.Felhasznalo;
import hu.fenykep.demo.model.Kep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class KepRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<Kep> findAll(){
        List<Kep> result = jdbcTemplate.query(
                "SELECT * FROM Kep",
                (rs, rowNum) -> new Kep()
        );
        return result;
    }
}
