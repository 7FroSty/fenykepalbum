package hu.fenykep.demo.repository;

import hu.fenykep.demo.model.Felhasznalo;
import hu.fenykep.demo.model.Kep;
import oracle.jdbc.OracleTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

@SuppressWarnings({"DuplicatedCode", "unchecked"})
@Repository
public class KepRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;

    private String clobToString(Clob clob) {
        try {
            Reader r = clob.getCharacterStream();
            StringBuilder buffer = new StringBuilder();
            int ch;
            while ((ch = r.read()) != -1) {
                buffer.append((char)ch);
            }
            return buffer.toString();
        } catch (IOException | SQLException ignored){

        }
        return "";
    }

    private final RowMapper<Kep> kepRowMapper = (rs, i) -> new Kep(
            rs.getInt("id"),
            rs.getInt("felhasznalo_id"),
            rs.getInt("kategoria_id"),
            rs.getString("cim"),
            clobToString(rs.getClob("tartalom")),
            rs.getTimestamp("idopont"),
            rs.getString("telepules"));


    private SimpleJdbcCall getJdbcCallCursorKep() {
        return new SimpleJdbcCall(jdbcTemplate)
                .returningResultSet("c_kepek", kepRowMapper);
    }


    public List<Kep> findAll() {
        return jdbcTemplate.query("SELECT * FROM Kep", kepRowMapper);
    }

    public Kep findById(int id) {
        return jdbcTemplate.queryForObject("SELECT * FROM Kep Where id = ?",
                new Object[]{
                        id
                }, new int[]{
                        OracleTypes.NUMBER
                }, kepRowMapper);
    }


    public Map<String, Object> executeKepFeltoltes(int felhasznalo_id, int kategoria_id, String cim,
                                                   String tartalom, Timestamp idopont, String telepules) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("KepFeltoltes");

        jdbcCall.declareParameters(
                new SqlParameter("p_felhasznalo_id", OracleTypes.NUMBER),
                new SqlParameter("p_kategoria_id", OracleTypes.NUMBER),
                new SqlParameter("p_cim", OracleTypes.VARCHAR),
                new SqlParameter("p_tartalom", OracleTypes.CLOB),
                new SqlParameter("p_idopont", OracleTypes.DATE),
                new SqlParameter("p_telepules", OracleTypes.VARCHAR),
                new SqlOutParameter("p_error", OracleTypes.NUMBER)
        );

        return jdbcCall.execute(felhasznalo_id, kategoria_id, cim, tartalom, idopont, telepules);

    }


    public List<Kep> executeKepListaCim(String cim) {
        return executeKepListaCim(cim, true);
    }
    public List<Kep> executeKepListaCim(String cim, boolean legujabbElol) {
        SimpleJdbcCall jdbcCall = getJdbcCallCursorKep().withProcedureName("KepListaCim")
                .declareParameters(
                        new SqlParameter("p_cim", OracleTypes.VARCHAR),
                        new SqlParameter("p_rendez", OracleTypes.NUMBER),
                        new SqlParameter("p_tolarencia", OracleTypes.NUMBER)
                );

        // Kis tolarencia
        List<Kep> kepek = (List<Kep>) jdbcCall.execute(cim, legujabbElol ? 0 : 1, 0).get("c_kepek");

        if (kepek.size() == 0) {
            // Nagy tolarencia
            kepek = (List<Kep>) jdbcCall.execute(cim, legujabbElol ? 0 : 1, 1).get("c_kepek");
        }
        return kepek;
    }


    public List<Kep> executeKepListaKategoria(int kategoriaId) {
        return executeKepListaKategoria(kategoriaId, true);
    }
    public List<Kep> executeKepListaKategoria(int kategoriaId, boolean legujabbElol) {
        SimpleJdbcCall jdbcCall = getJdbcCallCursorKep().withProcedureName("KepListaKategoria")
                .declareParameters(
                        new SqlParameter("p_kategoria", OracleTypes.NUMBER),
                        new SqlParameter("p_rendez", OracleTypes.NUMBER)
                );

        return (List<Kep>) jdbcCall.execute(kategoriaId, legujabbElol ? 0 : 1).get("c_kepek");
    }


    public List<Kep> executeKepListaKulcsszo(String kulcsszo) {
        return executeKepListaKulcsszo(kulcsszo, true);
    }
    public List<Kep> executeKepListaKulcsszo(String kulcsszo, boolean legujabbElol) {
        SimpleJdbcCall jdbcCall = getJdbcCallCursorKep().withProcedureName("KepListaKulcsszo")
                .declareParameters(
                        new SqlParameter("p_kulcsszo", OracleTypes.VARCHAR),
                        new SqlParameter("p_rendez", OracleTypes.NUMBER),
                        new SqlParameter("p_tolarencia", OracleTypes.NUMBER)
                );

        // Kis tolarencia
        List<Kep> kepek = (List<Kep>) jdbcCall.execute(kulcsszo, legujabbElol ? 0 : 1, 0).get("c_kepek");

        if (kepek.size() == 0) {
            // Nagy tolarencia
            kepek = (List<Kep>) jdbcCall.execute(kulcsszo, legujabbElol ? 0 : 1, 1).get("c_kepek");
        }
        return kepek;
    }
}
