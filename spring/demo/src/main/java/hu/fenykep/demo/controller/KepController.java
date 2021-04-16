package hu.fenykep.demo.controller;

import hu.fenykep.demo.model.*;
import hu.fenykep.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.support.HttpRequestHandlerServlet;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Controller
public class KepController {
    @Autowired
    private KepRepository kepRepository;

    @Autowired
    private KategoriaRepository kategoriaRepository;

    @Autowired
    private VersenyRepository versenyRepository;

    @Autowired
    private FelhasznaloRepository felhasznaloRepository;

    @Autowired
    private ErtekelesRepository ertekelesRepository;

    @Autowired
    private KommentRepository kommentRepository;

    @GetMapping("/kep/kepFeltoltes")
    @PreAuthorize("hasRole('FELHASZNALO')")
    public String kep(Model model, Authentication authentication) {
        Felhasznalo felhasznalo = (Felhasznalo) authentication.getPrincipal();

        model.addAttribute("felhasznalo", felhasznalo);
        model.addAttribute("sajatProfil", true);

        List<Kategoria> kategoriak = kategoriaRepository.findAll();
        model.addAttribute("kategoriak", kategoriak);

        List<Verseny> versenyek = versenyRepository.getActiveVersenyek();
        model.addAttribute("versenyek", versenyek);

        return "/kep/kepFeltoltes";
    }

    @GetMapping("/kep/kepFeltoltes/{id}")
    public String kepFeltoltes(@PathVariable("id") Integer id, Model model, Authentication authentication) {
        Felhasznalo felhasznalo = null;
        if(authentication != null) {
            felhasznalo = (Felhasznalo) authentication.getPrincipal();
        }

        if (felhasznalo == null) {
            return "redirect:/felhasznalo/profil";
        }
        try {
            felhasznalo = felhasznaloRepository.getFelhasznaloById(id);
            model.addAttribute("felhasznalo", felhasznalo);
        }
        catch (DataAccessException e) {
            model.addAttribute("felhasznaloNemTalalt", true);
        }

        return "redirect:/kep/kepFeltoltes";
    }

    @PostMapping("/feltoltes")
    public String kepFeltoltes(@RequestParam("kep") MultipartFile kep,
                               @RequestParam("cim") String cim,
                               @RequestParam("telepules") String telepules,
                               @RequestParam("kategoria") int kategoria_id,
                               @RequestParam("felhasznalo_id") String felhasznalo_id) throws IOException {
        System.out.println("kep feltoltese...");

        byte[] fileContent = kep.getBytes();
        String encodedString = Base64.getEncoder().encodeToString(fileContent);

        Timestamp date = new Timestamp(System.currentTimeMillis());

        kepRepository.executeKepFeltoltes(Integer.parseInt(felhasznalo_id), kategoria_id, cim, encodedString, date, telepules);

        return "redirect:/";
    }

    @GetMapping("/kep/kategoriak")
    public String kategoriak(Model model) {
        model.addAttribute("kategoriak", kategoriaRepository.findAll());
        return "/kep/kategoriak";
    }

    @GetMapping("/kep/telepulesek")
    public String telepulesek(Model model) {
        model.addAttribute("telepulesek", kepRepository.findAllTelepules());
        return "/kep/telepulesek";
    }


    // Kép megtekintése
    @GetMapping("/kep/megtekintes/{id}")
    public String kepMegtekintesId(@PathVariable("id") Integer id, Model model) {
        Kep kep = kepRepository.findById(id);
        if(kep != null) {
            kepRepository.getKepAdatok(kep);
            List<Komment> kommentek = kommentRepository.getKepKommentek(kep);
            model.addAttribute("kommentek", kommentek);
            model.addAttribute("kep", kep);
        }
        return "/kep/megtekintes";
    }

    @GetMapping("/kep/ertekeles/{id}/csillag/{csillagok}")
    @PreAuthorize("hasRole('FELHASZNALO')")
    public String kepErtekeles(@PathVariable("id") Integer id,
                               @PathVariable("csillagok") Integer csillagok,
                               Authentication authentication) {
        Felhasznalo felhasznalo = (Felhasznalo) authentication.getPrincipal();
        if(csillagok >= 0 && csillagok <= 5) {
            ertekelesRepository.executeKepErtekeles(felhasznalo, id, csillagok);
        } else {
            System.out.println("Hiba történt értékelés közben.");
        }
        return "redirect:/kep/megtekintes/" + id;
    }

    // összes kép listázása
    @GetMapping("/kep/kepek")
    public String kepListaOsszes(Model model) {
        model.addAttribute("kepek", kepRepository.findAll());
        model.addAttribute("kepSzuro", "Minden kép");
        return "/kep/listazas";
    }

    // összes kép listázása egy adott kategóriából
    @GetMapping("/kep/kepek/kategoria/{id}")
    public String kepListaByKategoriaId(@PathVariable("id") Integer id, Model model) {
        Kategoria kategoria = kategoriaRepository.findKategoriaById(id);

        String kategoriaStr = "Nincs ilyen kategória";
        if(kategoria != null) {
            kategoriaStr = "Kategória: " + kategoria.getNev();
            model.addAttribute("kepek", kepRepository.findAllByKategoriaId(id));
        }
        model.addAttribute("kepSzuro", kategoriaStr);
        return "/kep/listazas";
    }

    // összes kép listázása egy adott felhasználótól
    @GetMapping("/kep/kepek/felhasznalo/{id}")
    public String kepListaByFelhasznaloId(@PathVariable("id") Integer id, Model model) {
        Felhasznalo felhasznalo = felhasznaloRepository.getFelhasznaloById(id, true);

        String felhasznaloStr = "Nincs ilyen felhasználó";
        if(felhasznalo != null) {
            felhasznaloStr = "Képek tőle: " + felhasznalo.getNev();
            model.addAttribute("kepek", kepRepository.findAllByFelhasznaloId(id));
        }
        model.addAttribute("kepSzuro", felhasznaloStr);
        return "/kep/listazas";
    }

    // összes kép listázása település alapján.
    // Itt pontos egyezés alapján
    @GetMapping("/kep/kepek/telepules/{nev}")
    public String kepListaByTelepulesNev(@PathVariable("nev") String nev, Model model) {
        List<Kep> kepek = kepRepository.findAllByTelepulesNev(nev);

        String telepulesStr = "Nincs kép itt: " + nev;
        if(kepek.size() != 0) {
            telepulesStr = "Képek itt: " + nev;
            model.addAttribute("kepek", kepek);
        }
        model.addAttribute("kepSzuro", telepulesStr);
        return "/kep/listazas";
    }

    // Keresés

    @PostMapping("/kep/kereses")
    public String kepKeresesPost(@RequestParam("kereses") String kereses,
                                 @RequestParam("szoveg") String szoveg,
                                 Model model) {
        List<Kep> kepek = new ArrayList<>();

        String szuroStr = "";
        switch (kereses) {
            case "cim":
                kepek.addAll(kepRepository.executeKepKeresesCim(szoveg));
                szuroStr += "Keresés Címre: ";
                break;
            case "felhasznalo":
                kepek.addAll(kepRepository.executeKepKeresesFelhasznalo(szoveg));
                szuroStr += "Keresés Felhasználóra: ";
                break;
            case "kategoria":
                kepek.addAll(kepRepository.executeKepKeresesKategoria(szoveg));
                szuroStr += "Keresés Kategóriára: ";
                break;
            case "kulcsszo":
                kepek.addAll(kepRepository.executeKepKeresesKulcsszo(szoveg));
                szuroStr += "Keresés Kulcsszóra: ";
                break;
            case "telepules":
                kepek.addAll(kepRepository.executeKepKeresesTelepules(szoveg));
                szuroStr += "Keresés Településre: ";
                break;
            default:
                kepek.addAll(kepRepository.findAll());
                break;
        }
        szuroStr += szoveg;

        model.addAttribute("kereses", kereses);
        model.addAttribute("keresesSzoveg", szoveg);

        System.out.println("Kereses: " + kereses + ", szoveg: " + szoveg);

        model.addAttribute("kepek", kepek);
        model.addAttribute("kepSzuro", szuroStr);
        return "/kep/listazas";
    }
}
