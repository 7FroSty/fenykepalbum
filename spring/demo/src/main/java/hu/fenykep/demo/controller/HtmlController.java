package hu.fenykep.demo.controller;

import hu.fenykep.demo.model.Bejegyzes;
import hu.fenykep.demo.model.Felhasznalo;
import hu.fenykep.demo.repository.BejegyzesRepository;
import hu.fenykep.demo.repository.FelhasznaloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class HtmlController {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private FelhasznaloRepository felhasznaloRepository;
    @Autowired
    private BejegyzesRepository bejegyzesRepository;

    @GetMapping("")
    public String index(Model model) throws Exception {
        try {
            this.runTeszt();
        }
        catch (Exception e){

        }
        model.addAttribute("hiba", "Hibauzenet");

        List<Bejegyzes> bejegyzesek = bejegyzesRepository.findAll();
        model.addAttribute("bejegyzesek", bejegyzesek);
        model.addAttribute("admin", felhasznaloRepository);

        return "index";
    }



    public void runTeszt() throws Exception {
        System.out.println("al");

        String sql = "SELECT * FROM Felhasznalo";

        List<Felhasznalo> felhasznalok = felhasznaloRepository.findAll();

        felhasznalok.forEach(System.out :: println);
    }

    @GetMapping("/index")
    public String index() {
        return "redirect:";
    }

    @GetMapping("/kep/kepek")
    public String kepek() {
        return "/kep/kepek";
    }

    @GetMapping("/versenyek")
    public String versenyek() {
        return "/versenyek";
    }
}
