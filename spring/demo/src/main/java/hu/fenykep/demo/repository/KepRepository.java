package hu.fenykep.demo.repository;

import hu.fenykep.demo.model.Felhasznalo;
import hu.fenykep.demo.model.Kep;
import oracle.jdbc.OracleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public class KepRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<Kep> findAll(){
        List<Kep> result = jdbcTemplate.query(
                "SELECT * FROM Kep",
                (rs, rowNum) -> new Kep()
        );
        return result;
    }

    public Map<String, Object> executeKepFeltoltes(int felhasznalo_id, int kategoria_id, String cim,
                                                   String tartalom, Timestamp idopont, String telepules) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("KepFeltoltes");

        jdbcCall.addDeclaredParameter(new SqlParameter("p_felhasznalo_id", OracleType.NUMBER.getVendorTypeNumber()));
        jdbcCall.addDeclaredParameter(new SqlParameter("p_kategoria_id", OracleType.NUMBER.getVendorTypeNumber()));
        jdbcCall.addDeclaredParameter(new SqlParameter("p_cim", OracleType.VARCHAR2.getVendorTypeNumber()));
        jdbcCall.addDeclaredParameter(new SqlParameter("p_tartalom", OracleType.CLOB.getVendorTypeNumber()));
        jdbcCall.addDeclaredParameter(new SqlParameter("p_idopont", OracleType.DATE.getVendorTypeNumber()));
        jdbcCall.addDeclaredParameter(new SqlParameter("p_telepules", OracleType.VARCHAR2.getVendorTypeNumber()));
        jdbcCall.addDeclaredParameter(new SqlOutParameter("p_error", OracleType.NUMBER.getVendorTypeNumber()));

        return jdbcCall.execute(felhasznalo_id, kategoria_id, cim, tartalom, idopont, telepules);

    }
}
