package hu.fenykep.demo.repository;

import hu.fenykep.demo.model.Bejegyzes;
import oracle.jdbc.OracleTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

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

    public void bejegyzesTorles(int id){
        jdbcTemplate.update("DELETE FROM Bejegyzes WHERE id = " + id);
    }

    public List<Bejegyzes> findAll(){ return jdbcTemplate.query("SELECT * FROM Bejegyzes ORDER BY Idopont DESC", bejegyzesRowMapper);}

    public Map<String, Object> executeBejegyzesFeltoltes(String cim, String tartalom, int id){
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("BejegyzesFeltoltes");

        jdbcCall.declareParameters(
                new SqlParameter("p_cim", OracleTypes.VARCHAR),
                new SqlParameter("p_tartalom", OracleTypes.VARCHAR),
                new SqlParameter("p_id", OracleTypes.NUMBER)
        );

        return jdbcCall.execute(cim, tartalom, id);
    }
}
