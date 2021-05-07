package hu.fenykep.demo.repository;

import hu.fenykep.demo.model.KepKulcsszo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class KepKulcsszoRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<KepKulcsszo> findAll(){
        List<KepKulcsszo> result = jdbcTemplate.query(
                "SELECT * FROM KepKulcsszo",
                (rs, rowNum) -> new KepKulcsszo()
        );
        return result;
    }
}
