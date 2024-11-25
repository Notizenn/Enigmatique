// Source code is decompiled from a .class file using FernFlower decompiler.
package com.example.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.ui.Model;



@Controller 
public class LoginController {
   public LoginController() {
   }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("error", "Identifiants incorrects. Veuillez réessayer.");
        }
        if (logout != null) {
            model.addAttribute("message", "Vous vous êtes déconnecté avec succès.");
        }
        return "login";
    }


}
