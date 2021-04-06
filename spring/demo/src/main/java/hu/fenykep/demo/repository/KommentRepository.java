package hu.fenykep.demo.repository;

import hu.fenykep.demo.model.KepMegtekintes;
import hu.fenykep.demo.model.Komment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class KommentRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<Komment> findAll(){
        List<Komment> result = jdbcTemplate.query(
                "SELECT * FROM Komment",
                (rs, rowNum) -> new Komment()
        );
        return result;
    }
}
