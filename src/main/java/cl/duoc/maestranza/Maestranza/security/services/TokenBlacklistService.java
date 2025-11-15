package cl.duoc.maestranza.Maestranza.security.services;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {

    private static final Logger logger = LoggerFactory.getLogger(TokenBlacklistService.class);

    // Almacenamiento de tokens invalidados junto con su tiempo de expiración
    private final Map<String, Instant> blacklistedTokens = new ConcurrentHashMap<>();

    /**
     * Añade un token a la lista negra
     * @param token El token JWT a invalidar
     * @param expiryDate Fecha de expiración del token
     */
    public void blacklistToken(String token, Instant expiryDate) {
        blacklistedTokens.put(token, expiryDate);
        logger.info("Token añadido a la lista negra. Total tokens: {}", blacklistedTokens.size());
    }

    /**
     * Versión simplificada para añadir token con la expiración actual + tiempo específico
     * @param token El token JWT a invalidar
     */
    public void blacklistToken(String token) {
        // Usamos un tiempo de expiración predeterminado (24 horas)
        Instant expiry = Instant.now().plusSeconds(86400);
        blacklistedTokens.put(token, expiry);
        logger.info("Token añadido a la lista negra con expiración por defecto. Total tokens: {}", blacklistedTokens.size());
    }

    /**
     * Comprueba si un token está en la lista negra
     * @param token El token JWT a verificar
     * @return true si el token está en la lista negra, false en caso contrario
     */
    public boolean isBlacklisted(String token) {
        Instant expirationTime = blacklistedTokens.get(token);
        if (expirationTime == null) {
            return false;
        }
        
        // Si el token ya expiró naturalmente, removerlo de la blacklist
        if (Instant.now().isAfter(expirationTime)) {
            blacklistedTokens.remove(token);
            logger.debug("Token expirado removido automáticamente de la blacklist");
            return false;
        }
        
        return true;
    }

    /**
     * Limpia los tokens expirados de la lista negra
     * Se ejecuta automáticamente cada hora
     */
    @Scheduled(fixedRate = 3600000) // Ejecutar cada hora (3600000 ms)
    public void cleanupExpiredTokens() {
        Instant now = Instant.now();
        int initialSize = blacklistedTokens.size();
        blacklistedTokens.entrySet().removeIf(entry -> entry.getValue().isBefore(now));
        int removedTokens = initialSize - blacklistedTokens.size();
        
        if (removedTokens > 0) {
            logger.info("Limpieza automática: {} tokens expirados removidos. Tokens restantes: {}", 
                       removedTokens, blacklistedTokens.size());
        }
    }

    public int getBlacklistedTokenCount() {
        return blacklistedTokens.size();
    }

    @Scheduled(fixedRate = 3600000) // Cada hora
    public void logBlacklistStatus() {
        logger.info("Estado actual de la lista negra: {} tokens", blacklistedTokens.size());
    }
}