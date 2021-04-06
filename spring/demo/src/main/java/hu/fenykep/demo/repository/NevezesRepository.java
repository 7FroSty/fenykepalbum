package hu.fenykep.demo.repository;

import hu.fenykep.demo.model.Kulcsszo;
import hu.fenykep.demo.model.Nevezes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NevezesRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<Nevezes> findAll(){
        List<Nevezes> result = jdbcTemplate.query(
                "SELECT * FROM Nevezes",
                (rs, rowNum) -> new Nevezes()
        );
        return result;
    }
}
