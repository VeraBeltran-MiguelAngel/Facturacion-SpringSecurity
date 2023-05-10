package com.bolsadeideas.springboot.app.auth.handler;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.SessionFlashMapManager;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        // * administrador map para mensajes flash
        SessionFlashMapManager flashMapManager = new SessionFlashMapManager();

        FlashMap flashMap = new FlashMap();
        // enviamos el mensaje
        flashMap.put("success", "Hola "+authentication.getName()+" haz iniciado sesión con éxito! ");
        // guardamos el mensaje
        flashMapManager.saveOutputFlashMap(flashMap, request, response);

        // para ver el mensaje en consola 
        if (authentication != null) {
            logger.info("El usuario '"+authentication.getName()+"' ha iniciado sesioón con éxito");
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }

}
