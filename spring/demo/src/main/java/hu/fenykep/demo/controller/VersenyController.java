package hu.fenykep.demo.controller;

import hu.fenykep.demo.model.Felhasznalo;
import hu.fenykep.demo.model.Nevezes;
import hu.fenykep.demo.model.Verseny;
import hu.fenykep.demo.repository.VersenyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class VersenyController {
    @Autowired
    private VersenyRepository versenyRepository;

    @GetMapping("/versenyek")
    public String versenyListazas(Model model, Authentication authentication) {
        Felhasznalo felhasznalo = authentication == null ? null : (Felhasznalo) authentication.getPrincipal();

        List<Verseny> versenyList = versenyRepository.findAll();
        model.addAttribute("felhasznalo", felhasznalo);
        model.addAttribute("versenyek", versenyList);
        return "/verseny/listazas";
    }

    @PostMapping("/versenyek/meghirdetes")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView versenyMeghirdetes(
            @RequestParam("cim") String cim,
            @RequestParam("szoveg") String szoveg,
            @RequestParam("ido_kezdeti") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime ido_kezdeti,
            @RequestParam("ido_vege") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime ido_vege,
            ModelMap modelMap, Authentication authentication) {
        Felhasznalo felhasznalo = authentication == null ? null : (Felhasznalo) authentication.getPrincipal();

        if(cim == null || szoveg == null || ido_kezdeti == null || ido_vege == null) {
            modelMap.addAttribute("hiba", "Minden mező kitöltése kötelező");
        } else if(ido_kezdeti.isAfter(ido_vege)) {
            modelMap.addAttribute("hiba", "A kezdeti időpont a végső időpont előtt kell legyen");
        } else {
            versenyRepository.insertVerseny(cim, szoveg, Timestamp.valueOf(ido_kezdeti), Timestamp.valueOf(ido_vege));
        }


        modelMap.addAttribute("felhasznalo", felhasznalo);
        return new ModelAndView("redirect:/versenyek", modelMap);
    }

    @GetMapping("/versenyek/megtekintes/{id}")
    public String versenyMegtekintes(@PathVariable("id") Integer id,
                                     Model model, Authentication authentication) {
        Felhasznalo felhasznalo = authentication == null ? null : (Felhasznalo) authentication.getPrincipal();

        Verseny verseny = versenyRepository.findById(id);
        List<Nevezes> nevezesek = versenyRepository.findNevezesekById(id);
        List<Nevezes> szavazottNevezesek = new ArrayList<>();
        for (Nevezes nevezes : nevezesek) {
            if(versenyRepository.isSzavazott(felhasznalo, nevezes)) {
                szavazottNevezesek.add(nevezes);
            }
        }


        model.addAttribute("nevezesek", nevezesek);
        model.addAttribute("szavazott", szavazottNevezesek);
        model.addAttribute("verseny", verseny);
        return "/verseny/megtekintes";
    }

    @GetMapping("/versenyek/szavazas/{id}")
    @PreAuthorize("hasRole('FELHASZNALO')")
    public String versenySzavazas(@PathVariable("id") Integer n_id, Authentication authentication, HttpServletRequest request) {
        Felhasznalo felhasznalo = authentication == null ? null : (Felhasznalo) authentication.getPrincipal();
        try {
            if(felhasznalo != null) {
                versenyRepository.insertSzavazat(n_id, felhasznalo);
            }
        } catch (DataAccessException e){

        }
        return "redirect:" + request.getHeader("referer");
    }

    @GetMapping("/versenyek/szavazasTorles/{id}")
    @PreAuthorize("hasRole('FELHASZNALO')")
    public String versenySzavazasTorles(@PathVariable("id") Integer n_id, Authentication authentication, HttpServletRequest request) {
        Felhasznalo felhasznalo = authentication == null ? null : (Felhasznalo) authentication.getPrincipal();
        try {
            if (felhasznalo != null) {
                versenyRepository.deleteSzavazat(n_id, felhasznalo);
            }
        } catch (DataAccessException e) {

        }
        return "redirect:" + request.getHeader("referer");
    }

}
