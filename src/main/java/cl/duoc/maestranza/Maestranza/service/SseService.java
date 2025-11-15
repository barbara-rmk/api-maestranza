package cl.duoc.maestranza.Maestranza.service;

import cl.duoc.maestranza.Maestranza.dto.AlertaDTO;
import cl.duoc.maestranza.Maestranza.model.Alerta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SseService {
    private static final Logger logger = LoggerFactory.getLogger(SseService.class);
    private static final long SSE_TIMEOUT = 1000 * 60 * 30; // 30 minutos
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);

        // Configurar callbacks
        emitter.onCompletion(() -> {
            logger.info("Cliente SSE completó la conexión");
            emitters.remove(emitter);
        });

        emitter.onTimeout(() -> {
            logger.info("Cliente SSE timeout");
            emitters.remove(emitter);
        });

        emitter.onError((ex) -> {
            logger.error("Error en conexión SSE: {}", ex.getMessage());
            emitters.remove(emitter);
        });

        // Agregar el nuevo emisor a la lista
        emitters.add(emitter);
        logger.info("Nuevo cliente SSE suscrito. Total de clientes: {}", emitters.size());

        // Enviar un evento inicial para confirmar la conexión
        try {
            emitter.send(SseEmitter.event()
                    .name("conexion")
                    .data("Conexión establecida con éxito"));
        } catch (IOException e) {
            logger.error("Error al enviar evento de conexión inicial", e);
        }

        return emitter;
    }

    /**
     * Envía una alerta a todos los clientes conectados
     */
    public void sendAlerta(Alerta alerta) {
        AlertaDTO alertaDTO = convertirADTO(alerta);
        sendEvent("alerta", alertaDTO);
        logger.info("Alerta enviada a {} clientes: {}", emitters.size(), alerta.getId());
    }

    private AlertaDTO convertirADTO(Alerta alerta) {
        return AlertaDTO.fromEntity(alerta);
    }

    /**
     * Método genérico para enviar cualquier tipo de evento
     */
    public <T> void sendEvent(String eventName, T data) {
        List<SseEmitter> deadEmitters = new ArrayList<>();

        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name(eventName)
                        .data(data));
                logger.debug("Evento '{}' enviado a cliente SSE", eventName);
            } catch (IOException e) {
                deadEmitters.add(emitter);
                logger.error("Error enviando evento a cliente SSE: {}", e.getMessage());
            }
        });

        // Limpiar emisores muertos
        if (!deadEmitters.isEmpty()) {
            emitters.removeAll(deadEmitters);
            logger.info("Se eliminaron {} conexiones muertas. Clientes SSE activos: {}",
                    deadEmitters.size(), emitters.size());
        }
    }

    /**
     * Cierra todas las conexiones activas
     */
    public void closeAllConnections() {
        emitters.forEach(SseEmitter::complete);
        emitters.clear();
        logger.info("Todas las conexiones SSE han sido cerradas");
    }

    /**
     * @return Número de clientes conectados actualmente
     */
    public int getActiveClientsCount() {
        return emitters.size();
    }
}