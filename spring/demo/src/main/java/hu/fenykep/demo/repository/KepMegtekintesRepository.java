package hu.fenykep.demo.repository;

import hu.fenykep.demo.model.Bejegyzes;
import hu.fenykep.demo.model.KepMegtekintes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class KepMegtekintesRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<KepMegtekintes> findAll(){
        List<KepMegtekintes> result = jdbcTemplate.query(
                "SELECT * FROM KepMegtekintes",
                (rs, rowNum) -> new KepMegtekintes()
        );
        return result;
    }
}
