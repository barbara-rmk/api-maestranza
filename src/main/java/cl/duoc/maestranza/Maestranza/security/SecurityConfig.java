package cl.duoc.maestranza.Maestranza.security;

import cl.duoc.maestranza.Maestranza.security.jwt.JwtAuthenticationFilter;
import cl.duoc.maestranza.Maestranza.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    // Nota: el entry point que manejaba respuestas 401/403 se elimina aquí
    // porque la seguridad ha sido desactivada temporalmente para pruebas.
    // Si vuelves a activar seguridad, restaura este componente.

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        // ORIGINAL: Devuelve el filtro que valida el token JWT en cada request.
        // COMENTADO PARA PRUEBAS: durante las pruebas locales queremos que
        // todos los endpoints sean públicos, por lo tanto no se utilizará
        // activamente este filtro. Se mantiene el bean para evitar romper
        // otras dependencias en la aplicación, pero no se registra en la
        // cadena de filtros (ver método filterChain()).
        return new JwtAuthenticationFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @SuppressWarnings("deprecation")
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    // *****************************************************************
    // CAMBIO PARA PRUEBAS: Desactivar la capa de seguridad y permitir
    // todas las peticiones directamente. Esto hace que TODOS los
    // endpoints sean públicos temporalmente para facilitar pruebas con
    // Postman u otras herramientas.
    // *****************************************************************
    http
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(csrf -> csrf.disable())
        // Nota: no registramos entry point ni gestión de sesión avanzada
        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

    // ORIGINAL: El siguiente registro del authenticationProvider y la
    // adición del filtro JWT habilitan la autenticación basada en JWT
    // y validación de usuarios en cada petición. Se comentan para
    // permitir solicitudes públicas durante pruebas.
    // http.authenticationProvider(authenticationProvider());
    // http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

    return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));  // Permitir todos los orígenes
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}