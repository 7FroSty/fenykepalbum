package hu.fenykep.demo.repository;

import hu.fenykep.demo.model.Nevezes;
import hu.fenykep.demo.model.Szavazat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SzavazatRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<Szavazat> findAll(){
        List<Szavazat> result = jdbcTemplate.query(
                "SELECT * FROM Szavazat",
                (rs, rowNum) -> new Szavazat()
        );
        return result;
    }
}
