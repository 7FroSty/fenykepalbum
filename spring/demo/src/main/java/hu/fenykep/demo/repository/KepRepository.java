package hu.fenykep.demo.repository;

import hu.fenykep.demo.model.Ertekeles;
import hu.fenykep.demo.model.Kep;
import hu.fenykep.demo.model.Telepules;
import oracle.jdbc.OracleTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"DuplicatedCode", "unchecked"})
@Repository
public class KepRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    FelhasznaloRepository felhasznaloRepository;

    @Autowired
    KepMegtekintesRepository kepMegtekintesRepository;

    @Autowired
    ErtekelesRepository ertekelesRepository;

    @Autowired
    KulcsszoRepository kulcsszoRepository;

    @Autowired
    KategoriaRepository kategoriaRepository;

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

    private final RowMapper<Telepules> telepulesRowMapper = (rs, i) -> new Telepules(
            rs.getString("nev"),
            rs.getInt("kepdb"));


    private SimpleJdbcCall getJdbcCallCursorKep() {
        return new SimpleJdbcCall(jdbcTemplate)
                .returningResultSet("c_kepek", kepRowMapper);
    }


    public List<Kep> findAll(int page) {
        return jdbcTemplate.query("SELECT * FROM Kep ORDER BY idopont DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY",
                new Object[]{
                        (page - 1) * 15, 15
                }, new int[]{
                        OracleTypes.NUMBER, OracleTypes.NUMBER
                }, kepRowMapper);
    }

    public List<Telepules> findAllTelepules() {
        return jdbcTemplate.query("SELECT INITCAP(telepules) AS nev, COUNT(telepules) AS kepdb " +
                "FROM Kep " +
                "GROUP BY INITCAP(telepules) " +
                "ORDER BY nev", telepulesRowMapper);
    }

    public List<Kep> findAllByKategoriaId(int id) {
        return jdbcTemplate.query("SELECT * FROM Kep WHERE kategoria_id = ? ORDER BY idopont DESC",
                new Object[] { id }, new int[]{ OracleTypes.NUMBER }, kepRowMapper);
    }

    public List<Kep> findAllByFelhasznaloId(int id) {
        return jdbcTemplate.query("SELECT * FROM Kep WHERE felhasznalo_id = ? ORDER BY idopont DESC",
                new Object[] { id }, new int[]{ OracleTypes.NUMBER }, kepRowMapper);
    }

    public List<Kep> findAllByTelepulesNev(String nev) {
        return jdbcTemplate.query("SELECT * FROM Kep WHERE LOWER(telepules) = ? ORDER BY idopont DESC",
                new Object[] { nev.toLowerCase() }, new int[]{ OracleTypes.VARCHAR }, kepRowMapper);
    }

    public Kep findById(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM Kep Where id = ?",
                    new Object[]{
                            id
                    }, new int[]{
                            OracleTypes.NUMBER
                    }, kepRowMapper);
        }catch (DataAccessException dae) {
            System.out.println("Ez a kép nem létezik.");
        }
        return null;
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


    public List<Kep> executeKepKeresesCim(String cim) {
        return executeKepKeresesCim(cim, true);
    }
    public List<Kep> executeKepKeresesCim(String cim, boolean legujabbElol) {
        SimpleJdbcCall jdbcCall = getJdbcCallCursorKep().withProcedureName("KepKeresesCim")
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


    public List<Kep> executeKepKeresesFelhasznalo(String felhasznalo) {
        return executeKepKeresesFelhasznalo(felhasznalo, true);
    }
    public List<Kep> executeKepKeresesFelhasznalo(String felhasznalo, boolean legujabbElol) {
        SimpleJdbcCall jdbcCall = getJdbcCallCursorKep().withProcedureName("KepKeresesFelhasznalo")
                .declareParameters(
                        new SqlParameter("p_nev", OracleTypes.VARCHAR),
                        new SqlParameter("p_rendez", OracleTypes.NUMBER),
                        new SqlParameter("p_tolarencia", OracleTypes.NUMBER)
                );

        // Kis tolarencia
        List<Kep> kepek = (List<Kep>) jdbcCall.execute(felhasznalo, legujabbElol ? 0 : 1, 0).get("c_kepek");

        if (kepek.size() == 0) {
            // Nagy tolarencia
            kepek = (List<Kep>) jdbcCall.execute(felhasznalo, legujabbElol ? 0 : 1, 1).get("c_kepek");
        }
        return kepek;
    }


    public List<Kep> executeKepKeresesKategoria(String kategoria) {
        return executeKepKeresesKategoria(kategoria, true);
    }
    public List<Kep> executeKepKeresesKategoria(String kategoria, boolean legujabbElol) {
        SimpleJdbcCall jdbcCall = getJdbcCallCursorKep().withProcedureName("KepKeresesKategoria")
                .declareParameters(
                        new SqlParameter("p_kategoria", OracleTypes.VARCHAR),
                        new SqlParameter("p_rendez", OracleTypes.NUMBER),
                        new SqlParameter("p_tolarencia", OracleTypes.NUMBER)
                );

        // Kis tolarencia
        List<Kep> kepek = (List<Kep>) jdbcCall.execute(kategoria, legujabbElol ? 0 : 1, 0).get("c_kepek");

        if (kepek.size() == 0) {
            // Nagy tolarencia
            kepek = (List<Kep>) jdbcCall.execute(kategoria, legujabbElol ? 0 : 1, 1).get("c_kepek");
        }
        return kepek;
    }


    public List<Kep> executeKepKeresesKulcsszo(String kulcsszo) {
        return executeKepKeresesKulcsszo(kulcsszo, true);
    }
    public List<Kep> executeKepKeresesKulcsszo(String kulcsszo, boolean legujabbElol) {
        SimpleJdbcCall jdbcCall = getJdbcCallCursorKep().withProcedureName("KepKeresesKulcsszo")
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


    public List<Kep> executeKepKeresesTelepules(String telepules) {
        return executeKepKeresesTelepules(telepules, true);
    }
    public List<Kep> executeKepKeresesTelepules(String telepules, boolean legujabbElol) {
        SimpleJdbcCall jdbcCall = getJdbcCallCursorKep().withProcedureName("KepKeresesTelepules")
                .declareParameters(
                        new SqlParameter("p_telepules", OracleTypes.VARCHAR),
                        new SqlParameter("p_rendez", OracleTypes.NUMBER),
                        new SqlParameter("p_tolarencia", OracleTypes.NUMBER)
                );

        // Kis tolarencia
        List<Kep> kepek = (List<Kep>) jdbcCall.execute(telepules, legujabbElol ? 0 : 1, 0).get("c_kepek");

        if (kepek.size() == 0) {
            // Nagy tolarencia
            kepek = (List<Kep>) jdbcCall.execute(telepules, legujabbElol ? 0 : 1, 1).get("c_kepek");
        }
        return kepek;
    }


    public void getKepAdatok(Kep kep) {
        kep.setFelhasznalo(felhasznaloRepository.getFelhasznaloById(kep.getFelhasznalo_id()));
        kep.setKategoria(kategoriaRepository.findKategoriaById(kep.getKategoria_id()));

        double atlag = 0.0;
        List<Ertekeles> ertekelesList = ertekelesRepository.findAllByKep(kep);
        for (Ertekeles ertekeles : ertekelesList) {
            atlag += ertekeles.getCsillagok();
        }
        if(atlag != 0.0) {
            atlag /= ertekelesList.size();
        }

        kep.setCsillagok(atlag);
    }

    public void kepTorles(int id){
        jdbcTemplate.update("DELETE FROM Kep WHERE id = "+id);
    }

}
