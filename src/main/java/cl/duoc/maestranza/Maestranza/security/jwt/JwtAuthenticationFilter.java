package cl.duoc.maestranza.Maestranza.security.jwt;

import cl.duoc.maestranza.Maestranza.security.services.TokenBlacklistService;
import cl.duoc.maestranza.Maestranza.security.services.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            logger.info("JwtAuthenticationFilter: procesando petición para: {}", request.getRequestURI());
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt)) {
                logger.info("JWT encontrado en la solicitud");

                // Verificar si el token está en la lista negra - PRIMERO
                if (tokenBlacklistService.isBlacklisted(jwt)) {
                    logger.warn("Token en lista negra: acceso denegado");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("El token ha sido invalidado o la sesión ha sido cerrada");
                    return; // Detener el procesamiento aquí
                }

                // Validar el token DESPUÉS de verificar la lista negra
                if (tokenProvider.validateToken(jwt)) {
                    String username = tokenProvider.getUsernameFromJWT(jwt);
                    logger.info("Token válido para usuario: {}", username);

                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.info("Usuario autenticado correctamente: {}", username);
                } else {
                    logger.warn("El token JWT no es válido");
                }
            } else {
                logger.debug("No se encontró token JWT en la solicitud o formato incorrecto");
            }
        } catch (Exception ex) {
            logger.error("No se pudo establecer la autenticación de usuario en el contexto de seguridad: {}", ex.getMessage(), ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
