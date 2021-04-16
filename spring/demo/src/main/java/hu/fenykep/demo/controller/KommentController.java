package hu.fenykep.demo.controller;

import hu.fenykep.demo.repository.KommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class KommentController {
    @Autowired
    KommentRepository kommentRepository;

    @GetMapping("kommentiras")
    public String kommentIras(@RequestParam("szoveg") String szoveg,
                            @RequestParam("kep_id") int k_id,
                            @RequestParam("felhasznalo_id") int f_id){
        kommentRepository.executeKommentFeltoltes(szoveg, k_id, f_id);
        return "redirect:/kep/megtekintes/"+k_id;
    }

    @GetMapping("kommenttorles")
    public String kommentTorles(@RequestParam("id") int id,
                              @RequestParam("kep_id") int k_id){
        kommentRepository.kommentTorles(id);
        return "redirect:/kep/megtekintes/"+k_id;
    }
}
