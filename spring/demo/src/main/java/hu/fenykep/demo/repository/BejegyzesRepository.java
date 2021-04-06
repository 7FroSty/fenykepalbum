package hu.fenykep.demo.repository;

import hu.fenykep.demo.model.Bejegyzes;
import hu.fenykep.demo.model.Kep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BejegyzesRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<Bejegyzes> findAll(){
        List<Bejegyzes> result = jdbcTemplate.query(
                "SELECT * FROM Bejegyzes",
                (rs, rowNum) -> new Bejegyzes()
        );
        return result;
    }
}
