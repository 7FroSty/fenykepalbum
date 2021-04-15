package hu.fenykep.demo.repository;

import hu.fenykep.demo.model.Ertekeles;
import hu.fenykep.demo.model.Kategoria;
import org.springframework.beans.factory.annotation.Autowired;
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
}