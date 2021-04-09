package hu.fenykep.demo.controller;

import hu.fenykep.demo.model.Felhasznalo;
import hu.fenykep.demo.model.Kategoria;
import hu.fenykep.demo.repository.FelhasznaloRepository;
import hu.fenykep.demo.repository.KategoriaRepository;
import hu.fenykep.demo.repository.KepRepository;
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
import java.util.Base64;
import java.util.List;

@Controller
public class KepController {
    @Autowired
    private KepRepository kepRepository;

    @Autowired
    private KategoriaRepository kategoriaRepository;

    @Autowired
    private FelhasznaloRepository felhasznaloRepository;

    @GetMapping("/kep/kepFeltoltes")
    @PreAuthorize("hasRole('FELHASZNALO')")
    public String kep(Model model, Authentication authentication) {
        Felhasznalo felhasznalo = (Felhasznalo) authentication.getPrincipal();

        model.addAttribute("felhasznalo", felhasznalo);
        model.addAttribute("sajatProfil", true);

        List<Kategoria> kategoriak = kategoriaRepository.findAll();
        model.addAttribute("kategoriak", kategoriak);

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

        return "/kep/kepFeltoltes";
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

        return "index";
    }
}
