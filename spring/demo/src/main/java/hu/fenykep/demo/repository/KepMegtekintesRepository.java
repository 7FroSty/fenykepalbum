package hu.fenykep.demo.repository;

import hu.fenykep.demo.model.KepMegtekintes;
import oracle.jdbc.OracleTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class KepMegtekintesRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;

    private final RowMapper<KepMegtekintes> kepMegtekintesRowMapper = (rs, i) -> new KepMegtekintes();

    public List<KepMegtekintes> findAll(){
        List<KepMegtekintes> result = jdbcTemplate.query(
                "SELECT * FROM KepMegtekintes",
                (rs, rowNum) -> new KepMegtekintes()
        );
        return result;
    }
    public void addMegtekintes(int felhasznalo_id, int kep_id){
        jdbcTemplate.update("INSERT INTO KEPMEGTEKINTES(FELHASZNALO_ID, KEP_ID) VALUES(?, ?)",
                new Object[]{
                        felhasznalo_id, kep_id
                }, new int[]{
                        OracleTypes.NUMBER,
                        OracleTypes.NUMBER,
                });
    }

    public List<KepMegtekintes> countMegtekintes(int kep_id){
        try {
            return jdbcTemplate.query("SELECT * FROM KEPMEGTEKINTES Where KEP_ID = ?",
                    new Object[]{
                            kep_id
                    }, new int[]{
                            OracleTypes.NUMBER
                    }, kepMegtekintesRowMapper);
        }catch (DataAccessException dae) {
            System.out.println("Ez a kép nem létezik.");
        }
        return null;
    }
}
