package hu.fenykep.demo.repository;

import hu.fenykep.demo.model.Kulcsszo;
import hu.fenykep.demo.model.Verseny;
import oracle.jdbc.OracleTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class KulcsszoRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    KepKulcsszoRepository kepKulcsszoRepository;

    private final RowMapper<Kulcsszo> kulcsSzoRowMapper = (rs, rowNum) -> new Kulcsszo(
            rs.getInt("id"),
            rs.getString("nev")
    );

    public List<Kulcsszo> findAll() {
        return jdbcTemplate.query("SELECT * FROM KULCSSZO", kulcsSzoRowMapper);
    }

    public Kulcsszo addKulcsszo(String kulcsszo){
        for(Kulcsszo temp : this.findAll()){
            if(temp.getNev() != null && temp.getNev().equals(kulcsszo)){
                return temp;
            }
        }

        jdbcTemplate.update("INSERT INTO KULCSSZO(NEV) VALUES(?)",
                new Object[]{
                        kulcsszo
                }, new int[]{
                        OracleTypes.VARCHAR
                });

        for(Kulcsszo temp : this.findAll()){
            if(temp.getNev() != null && temp.getNev().equals(kulcsszo)){
                return temp;
            }
        }

        return null;
    }

    public List<Kulcsszo> findKulcsszoByKepId(int id){
        List<Kulcsszo> result = new ArrayList<>();
        for(int i: kepKulcsszoRepository.getIdsByKepId(id)){
            for(Kulcsszo temp : this.findAll()){
                if(i == temp.getId()){
                    result.add(temp);
                }
            }
        }
        return result;
    }
}
