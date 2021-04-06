package hu.fenykep.demo.repository;

import hu.fenykep.demo.model.Bejegyzes;
import hu.fenykep.demo.model.Ertekeles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ErtekelesRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<Ertekeles> findAll(){
        List<Ertekeles> result = jdbcTemplate.query(
                "SELECT * FROM Ertekeles",
                (rs, rowNum) -> new Ertekeles()
        );
        return result;
    }
}
