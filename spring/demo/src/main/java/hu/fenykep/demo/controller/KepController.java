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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    public String kategoriak() {
        return "/kep/kategoriak";
    }

    @GetMapping("/kep/telepulesek")
    public String telepulesek() {
        return "/kep/telepulesek";
    }

    // összes kép listázása
    @GetMapping("/kep/kepek")
    public String kepListaOsszes(Model model) {
        model.addAttribute("kepek", kepRepository.findAll());
        model.addAttribute("kepSzuro", "Minden kép");
        return "/kep/listazas";
    }

    // Kép megtekintése
    @GetMapping("/kep/megtekintes/{id}")
    public String kepMegtekintesId(@PathVariable("id") Integer id, Model model) {
        Kep kep = kepRepository.findById(id);
        kepRepository.getKepAdatok(kep);

        model.addAttribute("kep", kep);

        List<Komment> kommentek = kommentRepository.getKepKommentek(kep);
        model.addAttribute("kommentek", kommentek);

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

    // Saját képek
    /*@GetMapping("/kep/kepek/felhasznalo/")
    public String kepListaFelhasznalo(Authentication authentication) {
        if(authentication != null && authentication.isAuthenticated()) {
            return "redirect:/kep/kepek/felhasznalo/" + ((Felhasznalo) authentication.getPrincipal()).getId();
        }
        return "/kep/kepkkek";
    }

    @GetMapping("/kep/kepek/felhasznalo/{id}")
    public String kepListaFelhasznaloId(@PathVariable("id") Integer id) {
        // get kepek by felhasznalo id
        return "/kep/kekkkkpek";
    }

    @GetMapping("/kep/kepek/kategoria/{id}")
    public String kepListaKategoriaId(@PathVariable("id") Integer id) {
        // get kepek by kategoria id
        return "/kep/kekasdpek";
    }*/
}
