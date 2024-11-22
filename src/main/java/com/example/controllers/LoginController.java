// Source code is decompiled from a .class file using FernFlower decompiler.
package com.example.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
   public LoginController() {
   }

   @GetMapping({"/login"})
   public String showLoginPage() {
      return "login";
   }
}
