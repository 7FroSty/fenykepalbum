package hu.fenykep.demo.repository;

import hu.fenykep.demo.model.Komment;
import hu.fenykep.demo.model.Kulcsszo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class KulcsszoRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<Kulcsszo> findAll(){
        List<Kulcsszo> result = jdbcTemplate.query(
                "SELECT * FROM Kulcsszo",
                (rs, rowNum) -> new Kulcsszo()
        );
        return result;
    }
}
