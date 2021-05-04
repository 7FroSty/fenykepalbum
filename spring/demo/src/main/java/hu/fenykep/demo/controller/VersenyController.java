package hu.fenykep.demo.controller;

import hu.fenykep.demo.model.Felhasznalo;
import hu.fenykep.demo.model.Verseny;
import hu.fenykep.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.spring5.expression.Mvc;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Controller
public class VersenyController {
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
    public ModelAndView versenyMeghirdetes(@RequestParam("cim") String cim,
                                           @RequestParam("szoveg") String szoveg,
                                           ModelMap modelMap, Authentication authentication) {
        Felhasznalo felhasznalo = authentication == null ? null : (Felhasznalo) authentication.getPrincipal();

        modelMap.addAttribute("teszt", "töszt");
        modelMap.addAttribute("felhasznalo", felhasznalo);
        return new ModelAndView("redirect:/versenyek", modelMap);
    }
}
