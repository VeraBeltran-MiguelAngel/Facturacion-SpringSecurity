package com.bolsadeideas.springboot.app.controllers;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    /**
     * Personalizar el login para no usar el default de spring security,
     * 
     * @param model     se usa para enviar datos a la vista
     * @param principal contiene el usuario logueado, valida si el usuario ya ha
     *                  iniciado sesion, si es asi redirige a la pagina de inicio y
     *                  ya no muestra el login
     * @param flash     para enviar mensajes de confirmacion
     * @return
     */
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error, Model model, Principal principal,
            RedirectAttributes flash) {

        if (principal != null) {
            // siginifica que ya habia iniciado sesion anteriormente
            flash.addFlashAttribute("info", "Ya ha iniciado sesion anteriormente");
            return "redirect:/";
        }

        if (error != null) {
            model.addAttribute("error",
                    "Error en el login: Nombre de usuario o contraseña incorrecta, por favor vuelva a intentarlo");
        }
        return "login";
    }
}
