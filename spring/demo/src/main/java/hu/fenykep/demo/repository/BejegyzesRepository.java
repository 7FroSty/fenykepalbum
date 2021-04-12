package hu.fenykep.demo.repository;

import hu.fenykep.demo.model.Bejegyzes;
import hu.fenykep.demo.model.Kep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BejegyzesRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;

    private final RowMapper<Bejegyzes> bejegyzesRowMapper = (rs, rowNum) -> new Bejegyzes(
            rs.getInt("id"),
            rs.getInt("felhasznalo_id"),
            rs.getString("cim"),
            rs.getString("szoveg"),
            rs.getDate("idopont")
    );

    public List<Bejegyzes> findAll(){ return jdbcTemplate.query("SELECT * FROM Bejegyzes ORDER BY Idopont DESC", bejegyzesRowMapper);}
}
