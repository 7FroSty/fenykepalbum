package hu.fenykep.demo.repository;

import hu.fenykep.demo.model.Felhasznalo;
import hu.fenykep.demo.model.Kep;
import hu.fenykep.demo.model.Nevezes;
import hu.fenykep.demo.model.Verseny;
import oracle.jdbc.OracleType;
import oracle.jdbc.OracleTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.SQLException;
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
            rs.getTimestamp("szavazas_kezdete"),
            rs.getTimestamp("szavazas_vege")
    );

    private String clobToString(Clob clob) {
        try {
            Reader r = clob.getCharacterStream();
            StringBuilder buffer = new StringBuilder();
            int ch;
            while ((ch = r.read()) != -1) {
                buffer.append((char) ch);
            }
            return buffer.toString();
        } catch (IOException | SQLException ignored) {

        }
        return "";
    }

    private final RowMapper<Nevezes> nevezesRowMapper = (rs, i) -> new Nevezes(
            rs.getInt("n_id"),
            new Kep(rs.getInt("id"),
                    rs.getInt("felhasznalo_id"),
                    rs.getInt("kategoria_id"),
                    rs.getString("cim"),
                    clobToString(rs.getClob("tartalom")),
                    rs.getTimestamp("idopont"),
                    rs.getString("telepules")),
            rs.getInt("szavazat_db")
    );

    public List<Verseny> findAll() {
        return jdbcTemplate.query("SELECT * FROM Verseny", versenyRowMapper);
    }

    public Verseny findById(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM Verseny WHERE id = ?", new Object[]{
                    id
            }, new int[]{
                    OracleTypes.NUMBER
            }, versenyRowMapper);
        } catch (EmptyResultDataAccessException e) {
            System.out.println("Nincs ilyen verseny");
        }
        return null;
    }

    public List<Nevezes> findNevezesekById(int verseny_id) {
        try {
            return jdbcTemplate.query("SELECT N.ID AS N_ID, K.*, " +
                    "(SELECT COUNT(*) FROM SZAVAZAT WHERE SZAVAZAT.NEVEZES_ID = N.ID) AS szavazat_db FROM VERSENY " +
                    "RIGHT JOIN NEVEZES N on VERSENY.ID = N.VERSENY_ID " +
                    "LEFT JOIN KEP K on K.ID = N.KEP_ID " +
                    "WHERE VERSENY.ID = ?", new Object[]{
                    verseny_id
            }, new int[]{
                    OracleTypes.NUMBER
            }, nevezesRowMapper);
        } catch (EmptyResultDataAccessException e) {
            System.out.println("Nincsenek nevez??sek a versenyhez");
        }
        return new ArrayList<>();
    }

    private SimpleJdbcCall getJdbcCallCursorVerseny() {
        return new SimpleJdbcCall(jdbcTemplate).returningResultSet("c_versenyek", versenyRowMapper);
    }

    public List<Verseny> getActiveVersenyek() {
        SimpleJdbcCall jdbcCall = getJdbcCallCursorVerseny().withProcedureName("AktivVersenyek");
        jdbcCall.addDeclaredParameter(new SqlParameter("p_datum", OracleType.TIMESTAMP.getVendorTypeNumber()));

        Timestamp date = new Timestamp(System.currentTimeMillis());

        List<Verseny> versenyek = (List<Verseny>) jdbcCall.execute(date).get("c_versenyek");

        System.out.println(versenyek.size());

        return versenyek;
    }

    public void insertVerseny(String cim, String szoveg, Timestamp kezdeti, Timestamp vege) {
        jdbcTemplate.update("INSERT INTO VERSENY(CIM, SZOVEG, SZAVAZAS_KEZDETE, SZAVAZAS_VEGE) VALUES(?, ?, ?, ?)",
                new Object[]{
                        cim, szoveg, kezdeti, vege
                }, new int[]{
                        OracleTypes.VARCHAR,
                        OracleTypes.VARCHAR,
                        OracleTypes.TIMESTAMP,
                        OracleTypes.TIMESTAMP
                });
    }

    public void insertSzavazat(int nevezes_id, Felhasznalo felhasznalo) throws DataAccessException {
        jdbcTemplate.update("INSERT INTO SZAVAZAT(NEVEZES_ID, FELHASZNALO_ID) VALUES(?, ?)",
                new Object[]{
                        nevezes_id, felhasznalo.getId()
                }, new int[]{
                        OracleTypes.NUMBER,
                        OracleTypes.NUMBER,
                });
    }

    public void deleteSzavazat(int nevezes_id, Felhasznalo felhasznalo) throws DataAccessException {
        jdbcTemplate.update("DELETE FROM SZAVAZAT WHERE NEVEZES_ID = ? AND FELHASZNALO_ID = ?",
                new Object[]{
                        nevezes_id, felhasznalo.getId()
                }, new int[]{
                OracleTypes.NUMBER,
                OracleTypes.NUMBER,
        });
    }

    public void kepNevezese(int kep_id, int verseny_id){
        jdbcTemplate.update("INSERT INTO NEVEZES(VERSENY_ID, KEP_ID) VALUES(?, ?)",
                new Object[]{
                        verseny_id, kep_id
                }, new int[]{
                        OracleTypes.NUMBER,
                        OracleTypes.NUMBER,
                });
    }

    public boolean isSzavazott(Felhasznalo felhasznalo, Nevezes nevezes) {
        if (felhasznalo == null || nevezes == null) return false;
        try {
            Map<String, Object> rs = jdbcTemplate.queryForMap("SELECT COUNT(*) AS db FROM SZAVAZAT WHERE NEVEZES_ID = ? AND FELHASZNALO_ID = ?",
                    new Object[]{
                            nevezes.getId(), felhasznalo.getId()
                    }, new int[]{
                            OracleTypes.NUMBER, OracleTypes.NUMBER
                    });
            return ((BigDecimal) rs.get("db")).equals(BigDecimal.ONE);
        } catch (IncorrectResultSizeDataAccessException e) {
        }
        return false;
    }
}
