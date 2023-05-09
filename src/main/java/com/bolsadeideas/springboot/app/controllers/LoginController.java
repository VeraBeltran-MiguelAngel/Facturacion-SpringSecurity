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
     * @param error     para manejar un error de inicio de sesion
     * @param logout    para madar el mensaje de cerrar sesion
     * @param model     se usa para enviar datos a la vista
     * @param principal contiene el usuario logueado, valida si el usuario ya ha
     *                  iniciado sesion, si es asi redirige a la pagina de inicio y
     *                  ya no muestra el login
     * @param flash     para enviar mensajes de confirmacion
     * @return
     */
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout, Model model, Principal principal,
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

        if (logout != null) {
            model.addAttribute("success", "Ha cerrado sesión con éxito!");
        }
        return "login";
    }
}
