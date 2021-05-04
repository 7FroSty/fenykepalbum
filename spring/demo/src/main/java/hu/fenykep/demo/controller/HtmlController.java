package hu.fenykep.demo.controller;

import hu.fenykep.demo.model.Bejegyzes;
import hu.fenykep.demo.model.Felhasznalo;
import hu.fenykep.demo.repository.BejegyzesRepository;
import hu.fenykep.demo.repository.FelhasznaloRepository;
import hu.fenykep.demo.repository.KategoriaRepository;
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

    @Autowired
    private KategoriaRepository kategoriaRepository;

    @GetMapping("/")
    public String index(Model model, Authentication authentication) throws Exception {
        try {
            Felhasznalo felhasznalo = (Felhasznalo) authentication.getPrincipal();
            model.addAttribute("felhasznalo", felhasznalo);
            model.addAttribute("bejegyzesRepository", bejegyzesRepository);

            System.out.println(felhasznalo.isAdmin());

        }catch (Exception e){
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
    public String bejegyzesModositas(Model model, @RequestParam("bejegyzesId") int id, Authentication authentication){
        try {
            Felhasznalo felhasznalo = (Felhasznalo) authentication.getPrincipal();
            if(!felhasznalo.isAdmin()){
                return "redirect:/";
            }

        }catch (Exception e){
            return "redirect:/felhasznalo/bejelentkezes";
        }
        model.addAttribute("bejegyzes", bejegyzesRepository.getBejegyzesById(id));
        return "/bejegyzesModositas";
    }

    @GetMapping("/katModositas")
    public String katModositas(Model model, @RequestParam("katId") int id, Authentication authentication){
        try {
            Felhasznalo felhasznalo = (Felhasznalo) authentication.getPrincipal();
            if(!felhasznalo.isAdmin()){
                return "redirect:/";
            }

        }catch (Exception e){
            return "redirect:/felhasznalo/bejelentkezes";
        }
        model.addAttribute("kategoria", kategoriaRepository.findKategoriaById(id));
        return "/katModositas";
    }
}
