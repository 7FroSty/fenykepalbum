package hu.fenykep.demo.repository;

import hu.fenykep.demo.model.Kep;
import hu.fenykep.demo.model.Verseny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class VersenyRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<Verseny> findAll(){
        List<Verseny> result = jdbcTemplate.query(
                "SELECT * FROM Verseny",
                (rs, rowNum) -> new Verseny()
        );
        return result;
    }
}
