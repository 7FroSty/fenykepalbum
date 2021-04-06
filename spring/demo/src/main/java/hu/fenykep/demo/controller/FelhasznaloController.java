package hu.fenykep.demo.controller;

import hu.fenykep.demo.model.Felhasznalo;
import hu.fenykep.demo.repository.FelhasznaloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FelhasznaloController {
    @Autowired
    private FelhasznaloRepository felhasznaloRepository;

    @PostMapping("/register")
    public String regisztracio(@RequestParam("name") String nev, @RequestParam("email") String email,
                               @RequestParam("pwd") String pw, @RequestParam("irany") int iranyito,
                               @RequestParam("tele") String telepules, @RequestParam("utca") String utca,
                               @RequestParam("haz") String haz){
        try {
            felhasznaloRepository.insert(nev, email, pw, iranyito, telepules, utca, haz);
        }
        catch (Exception e){
            System.out.println("sikertelen");
            return "sikertelen";
        }
        return "sikeres";
    }

    @PostMapping("bejelentkezesSubmit")
    public String bejelentkezes(@RequestParam("email") String email, @RequestParam("jelszo") String jelszo){
        Felhasznalo felhasznalo = felhasznaloRepository.getFelhasznaloByEmail(email);
        if(felhasznalo==null){
            System.out.println("null");
            return "sikertelen";
        }
        if(felhasznalo.getJelszo().equals(jelszo)){
            return "sikeres";
        }
        return "sikertelen";
    }
}
