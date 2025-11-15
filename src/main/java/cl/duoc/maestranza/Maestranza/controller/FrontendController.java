package cl.duoc.maestranza.Maestranza.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FrontendController {

    /**
     * Redirige todas las rutas no encontradas al index.html de Angular
     * Esto permite que el Angular Router maneje las rutas del frontend
     */
    @RequestMapping(value = "/{path:^(?!api|assets|.*\\.).*}/**")
    public String redirect() {
        return "forward:/";
    }
} 