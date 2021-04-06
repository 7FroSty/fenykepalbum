package hu.fenykep.demo.controller;

import hu.fenykep.demo.model.Felhasznalo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.DayOfWeek;
import java.util.List;

@Controller
public class HtmlController {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("")
    public String index() throws Exception {
        try {
            this.run();
        }
        catch (Exception e){

        }
        return "/index";
    }

    public void run() throws Exception {
        System.out.println("alma");

        String sql = "SELECT * FROM Felhasznalo";

        List<Felhasznalo> felhasznalok = jdbcTemplate.query(sql,
                BeanPropertyRowMapper.newInstance(Felhasznalo.class));

        felhasznalok.forEach(System.out :: println);
    }
}
