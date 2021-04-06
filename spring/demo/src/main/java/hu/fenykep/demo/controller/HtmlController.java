package hu.fenykep.demo.controller;

import hu.fenykep.demo.model.Felhasznalo;
import hu.fenykep.demo.repository.FelhasznaloRepository;
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
    @Autowired
    private FelhasznaloRepository felhasznaloRepository;

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

        List<Felhasznalo> felhasznalok = felhasznaloRepository.findAll();

        felhasznalok.forEach(System.out :: println);
    }
}
