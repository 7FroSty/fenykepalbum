package hu.fenykep.demo.controller;

import hu.fenykep.demo.error.FelhasznaloError;
import hu.fenykep.demo.exception.FelhasznaloException;
import hu.fenykep.demo.model.Felhasznalo;
import hu.fenykep.demo.repository.FelhasznaloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

@Controller
public class FelhasznaloController {
    @Autowired
    private FelhasznaloRepository felhasznaloRepository;

    @GetMapping("/felhasznalo/regisztracio")
    public String regisztracio(Model model) {
        return "/felhasznalo/regisztracio";
    }

    @GetMapping("/felhasznalo/bejelentkezes")
    public String bejelentkezes(Model model) {
        return "/felhasznalo/bejelentkezes";
    }

    @GetMapping("/felhasznalo/profil")
    public String profil(Model model, HttpSession httpSession) {
        Felhasznalo felhasznalo = (Felhasznalo)httpSession.getAttribute("felhasznalo");
        if(felhasznalo == null) {
            return "redirect:/felhasznalo/bejelentkezes";
        }

        model.addAttribute("felhasznalo", felhasznalo);
        return "felhasznalo/profil";
    }


    @PostMapping("/felhasznalo/regisztracioPost")
    public ModelAndView regisztracioPost(@RequestParam("name") String nev, @RequestParam("email") String email,
                                         @RequestParam("jelszo") String jelszo, @RequestParam("iranyitoszam") int iranyitoszam,
                                         @RequestParam("telepules") String telepules, @RequestParam("utca") String utca,
                                         @RequestParam("hazszam") String hazszam, ModelMap modelMap) {
        try {
            // source: https://howtodoinjava.com/java/regex/java-regex-validate-email-address/
            String emailRegex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
            String jelszoRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$";

            if (nev.length() < 3 || nev.length() > 50) {
                throw new FelhasznaloException(FelhasznaloError.REG_BEVITEL_NEV);
            } else if (email.length() > 50 || !Pattern.compile(emailRegex).matcher(email).matches()) {
                throw new FelhasznaloException(FelhasznaloError.REG_BEVITEL_EMAIL);
            } else if (!Pattern.compile(jelszoRegex).matcher(jelszo).matches()) {
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

            String jelszoHash = getSha512Hash(jelszo);

            FelhasznaloError hiba =
                    FelhasznaloError.toFelhasznaloError(
                            ((BigDecimal) felhasznaloRepository.executeRegisztracio(
                                    nev, email, jelszoHash, iranyitoszam, telepules, utca, hazszam
                            ).get("p_error")).intValue()
                    );

            if (hiba != null) {
                throw new FelhasznaloException(hiba);
            }
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

    @PostMapping("/felhasznalo/bejelentkezesPost")
    public ModelAndView bejelentkezesPost(@RequestParam("email") String email, @RequestParam("jelszo") String jelszo,
                                          ModelMap modelMap, HttpSession httpSession) {
        try {
            Felhasznalo felhasznalo = felhasznaloRepository.getFelhasznaloByEmail(email);

            if (felhasznalo == null || !felhasznalo.getJelszo().equals(getSha512Hash(jelszo))) {
                throw new FelhasznaloException(FelhasznaloError.LOG_BEVITEL_EMAILJELSZO);
            }

            // TODO: Sikeres bejelentkezés, session kezelés
            httpSession.setAttribute("felhasznalo", felhasznalo);
            modelMap.addAttribute("felhasznalo", felhasznalo);

        } catch (FelhasznaloException e) {
            modelMap.addAttribute("hiba", e.getMessage());
            System.out.println("Bejelentkezés hiba: " + e.getMessage());
            return new ModelAndView("/felhasznalo/bejelentkezes", modelMap);
        } catch (Exception e) {
            modelMap.addAttribute("hiba", "Szerver hiba.");
            e.printStackTrace();
            return new ModelAndView("/felhasznalo/bejelentkezes", modelMap);
        }
        return new ModelAndView("redirect:/felhasznalo/profil");
    }

    public String getSha512Hash(String text) throws NoSuchAlgorithmException {
        // https://www.geeksforgeeks.org/sha-512-hash-in-java/
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        byte[] messageDigest = md.digest(text.getBytes());
        BigInteger no = new BigInteger(1, messageDigest);

        StringBuilder textHash = new StringBuilder(no.toString(16));
        while (textHash.length() < 32) {
            textHash.insert(0, "0");
        }
        return textHash.toString();
    }
}
