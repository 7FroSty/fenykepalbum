package hu.fenykep.demo.repository;

import hu.fenykep.demo.model.Felhasznalo;

import oracle.jdbc.OracleType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository
public class FelhasznaloRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Felhasznalo> felhasznaloRowMapper = (rs, rowNum) -> new Felhasznalo(
            rs.getInt("id"),
            rs.getString("nev"),
            rs.getString("email"),
            rs.getString("jelszo"),
            rs.getInt("iranyitoszam"),
            rs.getString("telepules"),
            rs.getString("utca"),
            rs.getString("hazszam"),
            rs.getBoolean("admin"));

    public List<Felhasznalo> findAll() {
        return jdbcTemplate.query("SELECT * FROM Felhasznalo", felhasznaloRowMapper);
    }


    public Map<String, Object> executeRegisztracio(String nev, String email, String jelszo, int iranyitoszam,
                                                   String telepules, String utca, String hazszam) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("FelhasznaloRegisztracio");

        jdbcCall.addDeclaredParameter(new SqlParameter("p_nev", OracleType.VARCHAR2.getVendorTypeNumber()));
        jdbcCall.addDeclaredParameter(new SqlParameter("p_email", OracleType.VARCHAR2.getVendorTypeNumber()));
        jdbcCall.addDeclaredParameter(new SqlParameter("p_jelszo", OracleType.VARCHAR2.getVendorTypeNumber()));
        jdbcCall.addDeclaredParameter(new SqlParameter("p_iranyitoszam", OracleType.NUMBER.getVendorTypeNumber()));
        jdbcCall.addDeclaredParameter(new SqlParameter("p_telepules", OracleType.VARCHAR2.getVendorTypeNumber()));
        jdbcCall.addDeclaredParameter(new SqlParameter("p_utca", OracleType.VARCHAR2.getVendorTypeNumber()));
        jdbcCall.addDeclaredParameter(new SqlParameter("p_hazszam", OracleType.VARCHAR2.getVendorTypeNumber()));
        jdbcCall.addDeclaredParameter(new SqlOutParameter("p_error", OracleType.NUMBER.getVendorTypeNumber()));

        return jdbcCall.execute(nev, email, jelszo, iranyitoszam, telepules, utca, hazszam);

    }

    public Felhasznalo getFelhasznaloByEmail(String email) {
        return jdbcTemplate.queryForObject("SELECT * FROM Felhasznalo WHERE email = ?", new Object[]{
                        email
                }, new int[]{
                        OracleType.VARCHAR2.getVendorTypeNumber()
                }, felhasznaloRowMapper);
    }

    public Felhasznalo getFelhasznaloById(int id) throws DataAccessException {
        return jdbcTemplate.queryForObject("SELECT * FROM Felhasznalo WHERE id = ?", new Object[]{
                        id
                }, new int[]{
                        OracleType.NUMBER.getVendorTypeNumber()
                }, felhasznaloRowMapper);
    }
}
