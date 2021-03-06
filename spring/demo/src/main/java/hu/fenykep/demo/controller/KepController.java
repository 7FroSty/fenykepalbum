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
    private KepMegtekintesRepository kepMegtekintesRepository;

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

    @Autowired
    private KepKulcsszoRepository kepKulcsszoRepository;

    @Autowired
    private KulcsszoRepository kulcsszoRepository;

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
    public String kepFeltoltes(Model model,
                               @RequestParam("kep") MultipartFile kep,
                               @RequestParam("cim") String cim,
                               @RequestParam("telepules") String telepules,
                               @RequestParam("kategoria") int kategoria_id,
                               @RequestParam("felhasznalo_id") int felhasznalo_id,
                               @RequestParam("verseny") int verseny_id,
                               @RequestParam("kulcsszavak") String kulcsszavak) throws IOException {
        System.out.println("kep feltoltese...");

        String[] kulcsszo = kulcsszavak.split(" ");

        byte[] fileContent = kep.getBytes();
        String encodedString = Base64.getEncoder().encodeToString(fileContent);

        Timestamp date = new Timestamp(System.currentTimeMillis());

        kepRepository.executeKepFeltoltes(felhasznalo_id, kategoria_id, cim, encodedString, date, telepules);

        List<Kep> kepek = kepRepository.findAllByFelhasznaloId(felhasznalo_id);

        for(Kep temp : kepek){
            if(temp.getCim().equals(cim) && temp.getKategoria_id() == kategoria_id
            && temp.getTelepules().equals(telepules)){
                for(String kulcs : kulcsszo){
                    Kulcsszo ksz = kulcsszoRepository.addKulcsszo(kulcs);
                    System.out.println(ksz.getId());
                    if(ksz!=null) {
                        kepKulcsszoRepository.addKulcsszoToKep(temp.getId(), ksz.getId());
                    }
                }
                versenyRepository.kepNevezese(temp.getId(), verseny_id);
                return "redirect:/kep/kepek";
            }
        }

        model.addAttribute("hiba", true);

        System.out.println("nem sikerult nevezni a versenyre");

        return "redirect:/kep/kepek";
    }

    @GetMapping("/kep/kategoriak")
    public String kategoriak(Model model, Authentication authentication) {
        try {
            Felhasznalo felhasznalo = (Felhasznalo) authentication.getPrincipal();
            model.addAttribute("felhasznalo", felhasznalo);

            System.out.println(felhasznalo.isAdmin());

        }catch (Exception e){
        }

        model.addAttribute("kategoriak", kategoriaRepository.findAll());
        return "/kep/kategoriak";
    }

    @GetMapping("/kep/telepulesek")
    public String telepulesek(Model model) {
        model.addAttribute("telepulesek", kepRepository.findAllTelepules());
        return "/kep/telepulesek";
    }


    // K??p megtekint??se
    @GetMapping("/kep/megtekintes/{id}")
    public String kepMegtekintesId(@PathVariable("id") Integer id, Model model, Authentication authentication) {
        Kep kep = kepRepository.findById(id);
        if(kep != null) {
            try {
                Felhasznalo felhasznalo = (Felhasznalo) authentication.getPrincipal();
                model.addAttribute("felhasznalo", felhasznalo);

                kepMegtekintesRepository.addMegtekintes(felhasznalo.getId(), id);

            }catch (Exception e){
            }
            List<KepMegtekintes> temp = kepMegtekintesRepository.countMegtekintes(id);
            model.addAttribute("megtekintesek", temp.size());

            kepRepository.getKepAdatok(kep);
            List<Komment> kommentek = kommentRepository.getKepKommentek(kep);

            String ksz="";
            List<Kulcsszo> foo = kulcsszoRepository.findKulcsszoByKepId(kep.getId());

            for(Kulcsszo i: foo){
                ksz+=i.getNev()+", ";
            }

            model.addAttribute("kulcsszavak", ksz);
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
            System.out.println("Hiba t??rt??nt ??rt??kel??s k??zben.");
        }
        return "redirect:/kep/megtekintes/" + id;
    }

    // ??sszes k??p list??z??sa
    @GetMapping("/kep/kepek")
    public String kepListaOsszes(Model model) {
        model.addAttribute("kepek", kepRepository.findAll(1));
        model.getAttribute("hiba");
        model.addAttribute("kepSzuro", "Minden k??p");
        model.addAttribute("page", 1);
        return "/kep/listazas";
    }

    // ??sszes k??p list??z??sa
    @GetMapping("/kep/kepek/{page}")
    public String kepListaOsszesoldal(@PathVariable("page") Integer page, Model model) {
        model.addAttribute("kepek", kepRepository.findAll(page));
        model.getAttribute("hiba");
        model.addAttribute("kepSzuro", "Minden k??p, " + page + ". oldal");
        model.addAttribute("page", page);
        return "/kep/listazas";
    }

    // ??sszes k??p list??z??sa egy adott kateg??ri??b??l
    @GetMapping("/kep/kepek/kategoria/{id}")
    public String kepListaByKategoriaId(@PathVariable("id") Integer id, Model model) {
        Kategoria kategoria = kategoriaRepository.findKategoriaById(id);

        String kategoriaStr = "Nincs ilyen kateg??ria";
        if(kategoria != null) {
            kategoriaStr = "Kateg??ria: " + kategoria.getNev();
            model.addAttribute("kepek", kepRepository.findAllByKategoriaId(id));
        }
        model.addAttribute("kepSzuro", kategoriaStr);
        return "/kep/listazas";
    }

    // ??sszes k??p list??z??sa egy adott felhaszn??l??t??l
    @GetMapping("/kep/kepek/felhasznalo/{id}")
    public String kepListaByFelhasznaloId(@PathVariable("id") Integer id, Model model) {
        Felhasznalo felhasznalo = felhasznaloRepository.getFelhasznaloById(id, true);

        String felhasznaloStr = "Nincs ilyen felhaszn??l??";
        if(felhasznalo != null) {
            felhasznaloStr = "K??pek t??le: " + felhasznalo.getNev();
            model.addAttribute("kepek", kepRepository.findAllByFelhasznaloId(id));
        }
        model.addAttribute("kepSzuro", felhasznaloStr);
        return "/kep/listazas";
    }

    // ??sszes k??p list??z??sa telep??l??s alapj??n.
    // Itt pontos egyez??s alapj??n
    @GetMapping("/kep/kepek/telepules/{nev}")
    public String kepListaByTelepulesNev(@PathVariable("nev") String nev, Model model) {
        List<Kep> kepek = kepRepository.findAllByTelepulesNev(nev);

        String telepulesStr = "Nincs k??p itt: " + nev;
        if(kepek.size() != 0) {
            telepulesStr = "K??pek itt: " + nev;
            model.addAttribute("kepek", kepek);
        }
        model.addAttribute("kepSzuro", telepulesStr);
        return "/kep/listazas";
    }

    // Keres??s

    @PostMapping("/kep/kereses")
    public String kepKeresesPost(@RequestParam("kereses") String kereses,
                                 @RequestParam("szoveg") String szoveg,
                                 Model model) {
        List<Kep> kepek = new ArrayList<>();

        String szuroStr = "";
        switch (kereses) {
            case "cim":
                kepek.addAll(kepRepository.executeKepKeresesCim(szoveg));
                szuroStr += "Keres??s C??mre: ";
                break;
            case "felhasznalo":
                kepek.addAll(kepRepository.executeKepKeresesFelhasznalo(szoveg));
                szuroStr += "Keres??s Felhaszn??l??ra: ";
                break;
            case "kategoria":
                kepek.addAll(kepRepository.executeKepKeresesKategoria(szoveg));
                szuroStr += "Keres??s Kateg??ri??ra: ";
                break;
            case "kulcsszo":
                kepek.addAll(kepRepository.executeKepKeresesKulcsszo(szoveg));
                szuroStr += "Keres??s Kulcssz??ra: ";
                break;
            case "telepules":
                kepek.addAll(kepRepository.executeKepKeresesTelepules(szoveg));
                szuroStr += "Keres??s Telep??l??sre: ";
                break;
            default:
                kepek.addAll(kepRepository.findAll(1));
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

    @GetMapping("kep/keptorles")
    public String kepTorles(@RequestParam("id") int id){
        kepRepository.kepTorles(id);
        return "redirect:/kep/kepek";
    }
}
