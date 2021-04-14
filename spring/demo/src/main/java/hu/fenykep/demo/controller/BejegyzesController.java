package hu.fenykep.demo.controller;

import hu.fenykep.demo.repository.BejegyzesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BejegyzesController {
    @Autowired
    BejegyzesRepository bejegyzesRepository;

    @PostMapping("/bejegyzesTorles")
    public String bejegyzesTorles(@RequestParam("bejegyzesId") int id){
        bejegyzesRepository.bejegyzesTorles(id);
        return "redirect:/index";
    }

    @PostMapping("/bejegyzesFeltoltes")
    public String bejegyzesFeltoltes(@RequestParam("cim") String cim,
                                     @RequestParam("tartalom") String tartalom,
                                     @RequestParam("admin_id") int id){
        bejegyzesRepository.executeBejegyzesFeltoltes(cim, tartalom, id);
        return "redirect:/index";
    }
}
