package hu.fenykep.demo.controller;

import hu.fenykep.demo.repository.KepRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Base64;

@Controller
public class KepController {
    @Autowired
    KepRepository kepRepository;

    @PostMapping("/feltoltes")
    public String kepFeltoltes(@RequestParam("kep") MultipartFile kep,
                               @RequestParam("cim") String cim,
                               @RequestParam("telepules") String telepules,
                               @RequestParam("kategoria") int kategoria_id,
                               @RequestParam("felhasznalo_id") String felhasznalo_id) throws IOException {
        System.out.println("kep feltoltese...");

        byte[] fileContent = kep.getBytes();
        String encodedString = Base64.getEncoder().encodeToString(fileContent);

        Timestamp date = new Timestamp(System.currentTimeMillis());

        kepRepository.executeKepFeltoltes(Integer.parseInt(felhasznalo_id), kategoria_id, cim, encodedString, date, telepules);

        return "index";
    }
}
