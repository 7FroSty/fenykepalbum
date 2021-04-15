package hu.fenykep.demo.repository;

import hu.fenykep.demo.model.Ertekeles;
import hu.fenykep.demo.model.Kategoria;
import hu.fenykep.demo.model.Kep;
import oracle.jdbc.OracleTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class KategoriaRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;

    private final RowMapper<Kategoria> kategoriaRowMapper = (rs, rowNum) -> new Kategoria(
            rs.getInt("id"),
            rs.getString("nev")
    );

    public List<Kategoria> findAll(){ return jdbcTemplate.query("SELECT * FROM Kategoria", kategoriaRowMapper);}

    public Kategoria findKategoriaById(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM Kategoria Where id = ?",
                    new Object[]{
                            id
                    }, new int[]{
                            OracleTypes.NUMBER
                    }, kategoriaRowMapper);
        }catch (DataAccessException dae) {
            System.out.println("Ez a Kategória nem létezik.");
        }
        return null;
    }
}