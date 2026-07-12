package com.ludistudy.smsroutingservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ApiDocsController {

    @GetMapping({"/scalar", "/scalar/"})
    public String redirectToScalar() {
        return "redirect:/scalar/index.html";
    }
}
