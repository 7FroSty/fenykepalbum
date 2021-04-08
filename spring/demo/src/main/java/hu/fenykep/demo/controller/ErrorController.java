package hu.fenykep.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {

    // Default error mapping
    @GetMapping("/error")
    public String error() {
        return "/error/notFound";
    }

    @GetMapping("/error/accessDenied")
    public String accessDenied() {
        return "/error/accessDenied";
    }

    @GetMapping("/error/notFound")
    public String notFound() {
        return "/error/notFound";
    }
}
