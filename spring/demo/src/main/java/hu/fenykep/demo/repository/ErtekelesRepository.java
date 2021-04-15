package hu.fenykep.demo.repository;

import hu.fenykep.demo.model.Bejegyzes;
import hu.fenykep.demo.model.Ertekeles;
import hu.fenykep.demo.model.Felhasznalo;
import hu.fenykep.demo.model.Kep;
import oracle.jdbc.OracleTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

@Repository
public class ErtekelesRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;

    private final RowMapper<Ertekeles> ertekelesRowMapper = (rs, i) -> new Ertekeles(
            rs.getInt("felhasznalo_id"),
            rs.getInt("kep_id"),
            rs.getInt("csillagok"));

    public List<Ertekeles> findAll(){
        List<Ertekeles> result = jdbcTemplate.query(
                "SELECT * FROM Ertekeles", ertekelesRowMapper
        );
        return result;
    }

    public List<Ertekeles> findAllByKep(Kep kep){
        List<Ertekeles> result = jdbcTemplate.query(
                "SELECT * FROM Ertekeles WHERE kep_id = ?",
                new Object[] {
                        kep.getId()
                },
                new int[] {
                        OracleTypes.NUMBER
                }, ertekelesRowMapper
        );
        return result;
    }


    public void executeKepErtekeles(Felhasznalo felhasznalo, int kep_id, int csillagok) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("KepErtekeles")
                .declareParameters(
                        new SqlParameter("p_felhasznalo_id", OracleTypes.NUMBER),
                        new SqlParameter("p_kep_id", OracleTypes.NUMBER),
                        new SqlParameter("p_csillagok", OracleTypes.NUMBER)
                );
        jdbcCall.execute(felhasznalo.getId(), kep_id, csillagok);
    }
}
