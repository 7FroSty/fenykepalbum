package hu.fenykep.demo.controller;

import hu.fenykep.demo.repository.KategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class KategoriaController {
    @Autowired
    KategoriaRepository kategoriaRepository;

    @PostMapping("/kategoriaFeltoltes")
    @PreAuthorize("hasRole('ADMIN')")
    public String kategoriaFeltoltes(@RequestParam("kategoria") String kategoria){
        kategoriaRepository.save(kategoria);
        return "redirect:/kep/kategoriak";
    }

    @PostMapping("/katModositasExecute")
    @PreAuthorize("hasRole('ADMIN')")
    public String katModositasExecute(@RequestParam("nev") String kategoria, @RequestParam("id") int id){
        kategoriaRepository.update(kategoria, id);
        return "redirect:/kep/kategoriak";
    }

    @PostMapping("/katTorles")
    @PreAuthorize("hasRole('ADMIN')")
    public String katTorles(@RequestParam("id") int id){
        kategoriaRepository.delete(id);
        return "redirect:/kep/kategoriak";
    }
}
