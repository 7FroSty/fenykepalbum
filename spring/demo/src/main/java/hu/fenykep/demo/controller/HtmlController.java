package hu.fenykep.demo.controller;

import hu.fenykep.demo.model.Bejegyzes;
import hu.fenykep.demo.model.Felhasznalo;
import hu.fenykep.demo.repository.BejegyzesRepository;
import hu.fenykep.demo.repository.FelhasznaloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HtmlController {
    @Autowired
    private FelhasznaloRepository felhasznaloRepository;

    @Autowired
    private BejegyzesRepository bejegyzesRepository;

    @GetMapping("/")
    public String index(Model model, Authentication authentication) throws Exception {
        try {
            Felhasznalo felhasznalo = (Felhasznalo) authentication.getPrincipal();
            model.addAttribute("felhasznalo", felhasznalo);
            model.addAttribute("bejegyzesRepository", bejegyzesRepository);

            System.out.println(felhasznalo.isAdmin());

        }catch (Exception e){
            Felhasznalo felhasznalo = new Felhasznalo(0, "vendeg", "", "", 0,
                    "", "", "", false);
            model.addAttribute("felhasznalo", felhasznalo);
        }

        List<Bejegyzes> bejegyzesek = bejegyzesRepository.findAll();
        model.addAttribute("bejegyzesek", bejegyzesek);
        model.addAttribute("admin", felhasznaloRepository);

        return "index";
    }

    @GetMapping("/index")
    public String index() {
        return "redirect:/";
    }

    @GetMapping("/versenyek")
    public String versenyek() {
        return "/versenyek";
    }

    @GetMapping("/bejegyzesModositas")
    public String bejegyzesModositas(Model model, @RequestParam("bejegyzesId") int id){
        model.addAttribute("bejegyzes", bejegyzesRepository.getBejegyzesById(id));
        return "/bejegyzesModositas";
    }
}
