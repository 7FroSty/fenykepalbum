package hu.fenykep.demo.repository;

import hu.fenykep.demo.model.*;
import oracle.jdbc.OracleTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public class KommentRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;

    private final RowMapper<Komment> kommentRowMapper = (rs, i) -> new Komment(
            rs.getInt("id"),
            rs.getInt("felhasznalo_id"),
            rs.getInt("kep_id"),
            rs.getString("szoveg"),
            rs.getTimestamp("idopont")
    );

    private final RowMapper<Komment> kommentFelhasznaloRowMapper = (rs, i) -> {
        Komment komment = new Komment(rs.getInt("id"),
                rs.getInt("felhasznalo_id"),
                rs.getInt("kep_id"),
                rs.getString("szoveg"),
                rs.getTimestamp("idopont"));
        komment.setFelhasznalo(new Felhasznalo(
                rs.getInt("felhasznalo_id"),
                rs.getString("f_nev"),
                rs.getString("f_email")));
        return komment;
    };

    public List<Komment> findAll(){
        return jdbcTemplate.query(
                "SELECT * FROM Komment", kommentRowMapper
        );
    }

    public List<Komment> getKepKommentek(Kep kep) {

        return jdbcTemplate.query(
                "SELECT k.id, k.kep_id, k.szoveg, k.idopont, f.id AS felhasznalo_id, f.nev AS f_nev, f.email AS f_email " +
                        "FROM Komment k " +
                        "LEFT JOIN Felhasznalo f ON k.felhasznalo_id = f.id " +
                        "WHERE kep_id = ? " +
                        "ORDER BY k.idopont DESC",
                new Object[] { kep.getId() },
                new int[] { OracleTypes.NUMBER },
                kommentFelhasznaloRowMapper
        );
    }

    public Map<String, Object> executeKommentFeltoltes(String szoveg, int k_id, int f_id){
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("KommentFeltoltes");

        jdbcCall.declareParameters(
                new SqlParameter("p_szoveg", OracleTypes.VARCHAR),
                new SqlParameter("p_kep_id", OracleTypes.NUMBER),
                new SqlParameter("p_felhasznalo_id", OracleTypes.NUMBER)
        );

        return jdbcCall.execute(szoveg, k_id, f_id);
    }

    public void kommentTorles(int id){
        jdbcTemplate.update("DELETE FROM Komment WHERE id = "+id);
    }
}
