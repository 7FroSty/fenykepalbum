package hu.fenykep.demo.repository;

import hu.fenykep.demo.model.Ertekeles;
import hu.fenykep.demo.model.Kategoria;
import hu.fenykep.demo.model.Kep;
import oracle.jdbc.OracleTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class KategoriaRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;

    private final RowMapper<Kategoria> kategoriaRowMapper = (rs, rowNum) -> new Kategoria(
            rs.getInt("id"),
            rs.getString("nev")
    );

    private final RowMapper<Kategoria> kategoriaDbRowMapper = (rs, rowNum) -> new Kategoria(
            rs.getInt("id"),
            rs.getString("nev"),
            rs.getInt("kepdb")
    );

    public List<Kategoria> findAll() {
        return jdbcTemplate.query("SELECT kat.id, kat.nev, COUNT(kep.id) AS kepdb " +
            "FROM Kategoria kat " +
            "LEFT JOIN Kep ON kep.kategoria_id = kat.id " +
            "GROUP BY kat.id, kat.nev " +
            "ORDER BY kat.nev ASC", kategoriaDbRowMapper);
    }

    public Map<String, Object> save(String nev){
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("KatFeltoltes");
        jdbcCall.declareParameters(
                new SqlParameter("p_kat_nev", OracleTypes.VARCHAR)
        );
        return jdbcCall.execute(nev);
    }

    public Map<String, Object> update(String nev, int id){
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("KatFrissites");
        jdbcCall.declareParameters(
                new SqlParameter("p_kat_nev", OracleTypes.VARCHAR),
                new SqlParameter("p_kat_id", OracleTypes.INTEGER)
        );
        return jdbcCall.execute(nev, id);
    }

    public void delete(int id) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("KatTorles");
        jdbcCall.declareParameters(
                new SqlParameter("p_kat_id", OracleTypes.INTEGER)
        );
        jdbcCall.execute(id);
    }

    public Kategoria findKategoriaById(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM Kategoria Where id = ?",
                    new Object[]{
                            id
                    }, new int[]{
                            OracleTypes.NUMBER
                    }, kategoriaRowMapper);
        }catch (EmptyResultDataAccessException e) {
            System.out.println("Ez a Kategória nem létezik.");
        }
        return null;
    }
}