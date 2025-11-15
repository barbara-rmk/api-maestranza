package cl.duoc.maestranza.Maestranza.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // Servir archivos estáticos de Angular
        registry.addResourceHandler("/assets/**")
                .addResourceLocations("classpath:/static/assets/")
                .setCachePeriod(3600);
                
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(0); // Sin cache para index.html
    }

    @Override
    public void addViewControllers(@NonNull ViewControllerRegistry registry) {
        // Redirigir la ruta raíz al index.html
        registry.addViewController("/").setViewName("forward:/index.html");
    }
}