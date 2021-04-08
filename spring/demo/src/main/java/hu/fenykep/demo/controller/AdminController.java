package hu.fenykep.demo.controller;

import hu.fenykep.demo.model.Felhasznalo;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @GetMapping("/felhasznalo/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String admin(Model model, Authentication authentication) {
        Felhasznalo felhasznalo = (Felhasznalo) authentication.getPrincipal();
        System.out.println("HELLO " + felhasznalo.getNev());

        model.addAttribute("felhasznalo", felhasznalo);
        return "/felhasznalo/admin";
    }
}
