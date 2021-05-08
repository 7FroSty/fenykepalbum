package hu.fenykep.demo.repository;

import hu.fenykep.demo.model.KepKulcsszo;
import hu.fenykep.demo.model.Kulcsszo;
import oracle.jdbc.OracleTypes;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class KepKulcsszoRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;

    private final RowMapper<KepKulcsszo> kepKulcsszoRowMapper = (rs, rowNum) -> new KepKulcsszo(
            rs.getInt("kep_id"),
            rs.getInt("kulcsszo_id")
    );

    public List<KepKulcsszo> findAll() {
        return jdbcTemplate.query("SELECT * FROM KEPKULCSSZO", kepKulcsszoRowMapper);
    }

    public void addKulcsszoToKep(int kep_id, int ksz_id){
        System.out.println("inserting into kepkulcsszo kep:"+kep_id+" ksz:"+ksz_id);
        jdbcTemplate.update("INSERT INTO KEPKULCSSZO(KEP_ID, KULCSSZO_ID) VALUES(?,?)",
                new Object[]{
                        kep_id, ksz_id
                }, new int[]{
                        OracleTypes.NUMBER,
                        OracleTypes.NUMBER,
                });
    }

    public List<Integer> getIdsByKepId(int id){
        List<Integer> result = new ArrayList<>();
        for(KepKulcsszo temp: this.findAll()){
            if(temp.getKep_id() == id){
                result.add(temp.getKulcsszo_id());
            }
        }
        return result;
    }
}
