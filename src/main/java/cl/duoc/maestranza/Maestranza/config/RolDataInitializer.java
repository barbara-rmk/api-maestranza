package cl.duoc.maestranza.Maestranza.config;

import cl.duoc.maestranza.Maestranza.model.Rol;
import cl.duoc.maestranza.Maestranza.model.TipoRol;
import cl.duoc.maestranza.Maestranza.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RolDataInitializer implements CommandLineRunner {

    @Autowired
    private RolRepository rolRepository;

    @Override
    public void run(String... args) throws Exception {
        // Inicializar roles si no existen
        for (TipoRol tipoRol : TipoRol.values()) {
            if (!rolRepository.existsByNombre(tipoRol)) {
                Rol rol = new Rol();
                rol.setNombre(tipoRol);
                rolRepository.save(rol);
                System.out.println("Rol creado: " + tipoRol);
            }
        }
    }
}
