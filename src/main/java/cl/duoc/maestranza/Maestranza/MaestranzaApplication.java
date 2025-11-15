package cl.duoc.maestranza.Maestranza;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
public class MaestranzaApplication {

	@PostConstruct
	public void init() {
		// Configurar zona horaria por defecto para toda la aplicación
		TimeZone.setDefault(TimeZone.getTimeZone("America/Santiago"));
		// O usar UTC: TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	public static void main(String[] args) {
		SpringApplication.run(MaestranzaApplication.class, args);
	}
}