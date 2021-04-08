package hu.fenykep.demo.controller;

import hu.fenykep.demo.error.FelhasznaloError;
import hu.fenykep.demo.exception.FelhasznaloException;
import hu.fenykep.demo.model.Felhasznalo;
import hu.fenykep.demo.repository.FelhasznaloRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.regex.Pattern;

@Controller
public class FelhasznaloController {
    @Autowired
    private FelhasznaloRepository felhasznaloRepository;

    // source: https://howtodoinjava.com/java/regex/java-regex-validate-email-address/
    private static final String RE_EMAIL = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
    private static final String RE_JELSZO = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$";
    private static final Pattern patternEmail = Pattern.compile(RE_EMAIL);
    private static final Pattern patternJelszo = Pattern.compile(RE_JELSZO);

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @GetMapping("/felhasznalo/regisztracio")
    public String regisztracio() {
        return "/felhasznalo/regisztracio";
    }

    @GetMapping("/felhasznalo/bejelentkezes")
    public String bejelentkezes(Authentication authentication) {
        if(authentication != null && authentication.isAuthenticated()) {
            return "redirect:/felhasznalo/profil";
        }
        return "/felhasznalo/bejelentkezes";
    }

    @GetMapping("/felhasznalo/profil")
    @PreAuthorize("hasRole('FELHASZNALO')")
    public String profil(Model model, Authentication authentication) {
        Felhasznalo felhasznalo = (Felhasznalo) authentication.getPrincipal();

        model.addAttribute("felhasznalo", felhasznalo);
        model.addAttribute("sajatProfil", true);
        return "/felhasznalo/profil";
    }


    @GetMapping("/felhasznalo/profil/{id}")
    public String profilId(@PathVariable("id") Integer id, Model model, Authentication authentication) {
        Felhasznalo felhasznalo = null;
        if(authentication != null) {
            felhasznalo = (Felhasznalo) authentication.getPrincipal();
        }

        // Redirect ha be van jelentkezve és a saját ID-ját adta meg
        if (felhasznalo != null && felhasznalo.getId() == id) {
            return "redirect:/felhasznalo/profil";
        }
        try {
            felhasznalo = felhasznaloRepository.getFelhasznaloById(id);
            model.addAttribute("felhasznalo", felhasznalo);
        }
        catch (DataAccessException e) {
            model.addAttribute("felhasznaloNemTalalt", true);
        }

        return "/felhasznalo/profil";
    }

    @GetMapping("/kepfeltoltes")
    @PreAuthorize("hasRole('FELHASZNALO')")
    public String kep(Model model, Authentication authentication) {
        Felhasznalo felhasznalo = (Felhasznalo) authentication.getPrincipal();

        model.addAttribute("felhasznalo", felhasznalo);
        model.addAttribute("sajatProfil", true);
        return "/kepfeltoltes";
    }

    @GetMapping("/kepfeltoltes/{id}")
    public String kepFeltoltes(@PathVariable("id") Integer id, Model model, Authentication authentication) {
        Felhasznalo felhasznalo = null;
        if(authentication != null) {
            felhasznalo = (Felhasznalo) authentication.getPrincipal();
        }

        // Redirect ha be van jelentkezve és a saját ID-ját adta meg
        if (felhasznalo != null && felhasznalo.getId() == id) {
            return "redirect:/felhasznalo/profil";
        }
        try {
            felhasznalo = felhasznaloRepository.getFelhasznaloById(id);
            model.addAttribute("felhasznalo", felhasznalo);
        }
        catch (DataAccessException e) {
            model.addAttribute("felhasznaloNemTalalt", true);
        }

        return "/kepfeltoltes";
    }

    @PostMapping("/felhasznalo/regisztracioPost")
    public ModelAndView regisztracioPost(@RequestParam("name") String nev, @RequestParam("email") String email,
                                         @RequestParam("jelszo") String jelszo, @RequestParam("iranyitoszam") int iranyitoszam,
                                         @RequestParam("telepules") String telepules, @RequestParam("utca") String utca,
                                         @RequestParam("hazszam") String hazszam, ModelMap modelMap) {
        try {
            if (nev.length() < 3 || nev.length() > 50) {
                throw new FelhasznaloException(FelhasznaloError.REG_BEVITEL_NEV);
            } else if (email.length() > 50 || !patternEmail.matcher(email).matches()) {
                throw new FelhasznaloException(FelhasznaloError.REG_BEVITEL_EMAIL);
            } else if (!patternJelszo.matcher(jelszo).matches()) {
                throw new FelhasznaloException(FelhasznaloError.REG_BEVITEL_JELSZO);
            } else if (iranyitoszam < 0 || iranyitoszam > 10000) {
                throw new FelhasznaloException(FelhasznaloError.REG_BEVITEL_IRANYITOSZAM);
            } else if (telepules.length() > 50) {
                throw new FelhasznaloException(FelhasznaloError.REG_BEVITEL_TELEPULES);
            } else if (utca.length() > 50) {
                throw new FelhasznaloException(FelhasznaloError.REG_BEVITEL_UTCA);
            } else if (hazszam.length() > 50) {
                throw new FelhasznaloException(FelhasznaloError.REG_BEVITEL_HAZSZAM);
            }

            String jelszoHash = passwordEncoder.encode(jelszo);

            FelhasznaloError hiba =
                    FelhasznaloError.toFelhasznaloError(
                            ((BigDecimal) felhasznaloRepository.executeRegisztracio(
                                    nev, email, jelszoHash, iranyitoszam, telepules, utca, hazszam
                            ).get("p_error")).intValue()
                    );

            if (hiba != null) {
                throw new FelhasznaloException(hiba);
            }
            System.out.println("WOW");
        } catch (FelhasznaloException e) {
            modelMap.addAttribute("hiba", e.getMessage());
            System.out.println("Regisztrációs hiba: " + e.getMessage());
            return new ModelAndView("/felhasznalo/regisztracio", modelMap);
        } catch (Exception e) {
            modelMap.addAttribute("hiba", "Szerver hiba.");
            e.printStackTrace();
            return new ModelAndView("/felhasznalo/regisztracio", modelMap);
        }
        return new ModelAndView("redirect:/felhasznalo/bejelentkezes");
    }
}
