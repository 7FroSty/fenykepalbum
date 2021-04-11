package hu.fenykep.demo.repository;

import hu.fenykep.demo.model.Kep;
import hu.fenykep.demo.model.Verseny;
import oracle.jdbc.OracleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class VersenyRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;

    private final RowMapper<Verseny> versenyRowMapper = (rs, rowNum) -> new Verseny(
            rs.getInt("id"),
            rs.getString("cim"),
            rs.getString("szoveg"),
            rs.getDate("szavazas_kezdete"),
            rs.getDate("szavazas_vege")
    );

    public List<Verseny> findAll(){ return jdbcTemplate.query("SELECT * FROM Verseny", versenyRowMapper); }

    private SimpleJdbcCall getJdbcCallCursorVerseny() {
        return new SimpleJdbcCall(jdbcTemplate).returningResultSet("c_versenyek", versenyRowMapper);
    }

    public List<Verseny> getActiveVersenyek() {
        SimpleJdbcCall jdbcCall = getJdbcCallCursorVerseny().withProcedureName("AktivVersenyek");
        jdbcCall.addDeclaredParameter(new SqlParameter("p_datum", OracleType.DATE.getVendorTypeNumber()));

        Timestamp date = new Timestamp(System.currentTimeMillis());

        List<Verseny> versenyek = (List<Verseny>) jdbcCall.execute(date).get("c_versenyek");

        System.out.println(versenyek.size());

        return versenyek;
    }
}
