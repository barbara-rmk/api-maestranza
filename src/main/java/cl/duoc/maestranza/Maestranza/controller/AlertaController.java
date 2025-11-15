package cl.duoc.maestranza.Maestranza.controller;

import cl.duoc.maestranza.Maestranza.dto.AlertaDTO;
import cl.duoc.maestranza.Maestranza.service.AlertaService;
import cl.duoc.maestranza.Maestranza.service.SseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/alertas")
@CrossOrigin(origins = "*")
public class AlertaController {

    private static final Logger logger = LoggerFactory.getLogger(AlertaController.class);

    @Autowired
    private AlertaService alertaService;

    @Autowired
    private SseService sseService;

    // ===== ENDPOINTS DE CONSULTA GENERAL =====
    /**
     * Obtener el conteo de alertas activas
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> obtenerConteoAlertas() {
        logger.info("GET /api/v1/alertas/count - Obteniendo conteo de alertas activas");
        try {
            long totalActivas = alertaService.contarActivas();

            Map<String, Object> response = new HashMap<>();
            response.put("count", totalActivas);
            logger.info("Conteo de alertas activas: {}", totalActivas);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al obtener conteo de alertas: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "*")
    public SseEmitter subscribeToAlerts() {
        logger.info("Nueva suscripción SSE a alertas");
        return sseService.subscribe();
    }

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("clientesConectados", sseService.getActiveClientsCount());
        return stats;
    }

    /**
     * Obtener todas las alertas
     */
    @GetMapping
    public ResponseEntity<List<AlertaDTO>> obtenerTodas() {
        logger.info("GET /api/v1/alertas - Obteniendo todas las alertas");
        try {
            List<AlertaDTO> alertas = alertaService.obtenerTodas().stream()
                    .map(AlertaDTO::fromEntity)
                    .collect(Collectors.toList());
            logger.info("Se obtuvieron {} alertas", alertas.size());
            return ResponseEntity.ok(alertas);
        } catch (Exception e) {
            logger.error("Error al obtener todas las alertas: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtener alertas activas
     */
    @GetMapping("/activas")
    public ResponseEntity<List<AlertaDTO>> obtenerActivas() {
        logger.info("GET /api/v1/alertas/activas - Obteniendo alertas activas");
        try {
            List<AlertaDTO> alertas = alertaService.obtenerActivas().stream()
                    .map(AlertaDTO::fromEntity)
                    .collect(Collectors.toList());
            logger.info("Se obtuvieron {} alertas activas", alertas.size());
            return ResponseEntity.ok(alertas);
        } catch (Exception e) {
            logger.error("Error al obtener alertas activas: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtener alertas inactivas
     */
    @GetMapping("/inactivas")
    public ResponseEntity<List<AlertaDTO>> obtenerInactivas() {
        logger.info("GET /api/v1/alertas/inactivas - Obteniendo alertas inactivas");
        try {
            List<AlertaDTO> alertas = alertaService.obtenerInactivas().stream()
                    .map(AlertaDTO::fromEntity)
                    .collect(Collectors.toList());
            logger.info("Se obtuvieron {} alertas inactivas", alertas.size());
            return ResponseEntity.ok(alertas);
        } catch (Exception e) {
            logger.error("Error al obtener alertas inactivas: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtener alerta por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<AlertaDTO> obtenerPorId(@PathVariable Long id) {
        logger.info("GET /api/v1/alertas/{} - Obteniendo alerta por ID", id);
        try {
            return alertaService.obtenerPorId(id)
                    .map(alerta -> {
                        AlertaDTO dto = AlertaDTO.fromEntity(alerta);
                        logger.info("Alerta encontrada: {}", dto.getNombre());
                        return ResponseEntity.ok(dto);
                    })
                    .orElseGet(() -> {
                        logger.warn("Alerta no encontrada con ID: {}", id);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            logger.error("Error al obtener alerta por ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ===== ENDPOINTS POR PRODUCTO =====

    /**
     * Obtener alertas por producto específico
     */
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<AlertaDTO>> obtenerPorProducto(@PathVariable Long productoId) {
        logger.info("GET /api/v1/alertas/producto/{} - Obteniendo alertas por producto", productoId);
        try {
            List<AlertaDTO> alertas = alertaService.obtenerPorProducto(productoId).stream()
                    .map(AlertaDTO::fromEntity)
                    .collect(Collectors.toList());
            logger.info("Se obtuvieron {} alertas para el producto {}", alertas.size(), productoId);
            return ResponseEntity.ok(alertas);
        } catch (Exception e) {
            logger.error("Error al obtener alertas por producto {}: {}", productoId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtener alertas activas por producto
     */
    @GetMapping("/producto/{productoId}/activas")
    public ResponseEntity<List<AlertaDTO>> obtenerActivasPorProducto(@PathVariable Long productoId) {
        logger.info("GET /api/v1/alertas/producto/{}/activas - Obteniendo alertas activas por producto", productoId);
        try {
            List<AlertaDTO> alertas = alertaService.obtenerActivasPorProducto(productoId).stream()
                    .map(AlertaDTO::fromEntity)
                    .collect(Collectors.toList());
            logger.info("Se obtuvieron {} alertas activas para el producto {}", alertas.size(), productoId);
            return ResponseEntity.ok(alertas);
        } catch (Exception e) {
            logger.error("Error al obtener alertas activas por producto {}: {}", productoId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtener alertas por código de producto
     */
    @GetMapping("/producto/codigo/{codigoProducto}")
    public ResponseEntity<List<AlertaDTO>> obtenerPorCodigoProducto(@PathVariable String codigoProducto) {
        logger.info("GET /api/v1/alertas/producto/codigo/{} - Obteniendo alertas por código de producto",
                codigoProducto);
        try {
            List<AlertaDTO> alertas = alertaService.obtenerPorCodigoProducto(codigoProducto).stream()
                    .map(AlertaDTO::fromEntity)
                    .collect(Collectors.toList());
            logger.info("Se obtuvieron {} alertas para el código de producto {}", alertas.size(), codigoProducto);
            return ResponseEntity.ok(alertas);
        } catch (Exception e) {
            logger.error("Error al obtener alertas por código de producto {}: {}", codigoProducto, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Verificar si un producto tiene alertas activas
     */
    @GetMapping("/producto/{productoId}/tiene-activas")
    public ResponseEntity<Map<String, Object>> tieneAlertasActivas(@PathVariable Long productoId) {
        logger.info("GET /api/v1/alertas/producto/{}/tiene-activas - Verificando alertas activas", productoId);
        try {
            boolean tieneAlertas = alertaService.tieneAlertasActivas(productoId);
            long cantidad = alertaService.contarActivasPorProducto(productoId);

            Map<String, Object> resultado = new HashMap<>();
            resultado.put("tieneAlertasActivas", tieneAlertas);
            resultado.put("cantidadAlertasActivas", cantidad);
            resultado.put("productoId", productoId);

            logger.info("Producto {} tiene {} alertas activas", productoId, cantidad);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            logger.error("Error al verificar alertas activas para producto {}: {}", productoId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ===== ENDPOINTS POR FECHA =====

    /**
     * Obtener alertas del día actual
     */
    @GetMapping("/hoy")
    public ResponseEntity<List<AlertaDTO>> obtenerDelDia() {
        logger.info("GET /api/v1/alertas/hoy - Obteniendo alertas del día");
        try {
            List<AlertaDTO> alertas = alertaService.obtenerDelDia().stream()
                    .map(AlertaDTO::fromEntity)
                    .collect(Collectors.toList());
            logger.info("Se obtuvieron {} alertas del día actual", alertas.size());
            return ResponseEntity.ok(alertas);
        } catch (Exception e) {
            logger.error("Error al obtener alertas del día: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtener alertas activas del día actual
     */
    @GetMapping("/hoy/activas")
    public ResponseEntity<List<AlertaDTO>> obtenerActivasDelDia() {
        logger.info("GET /api/v1/alertas/hoy/activas - Obteniendo alertas activas del día");
        try {
            List<AlertaDTO> alertas = alertaService.obtenerActivasDelDia().stream()
                    .map(AlertaDTO::fromEntity)
                    .collect(Collectors.toList());
            logger.info("Se obtuvieron {} alertas activas del día actual", alertas.size());
            return ResponseEntity.ok(alertas);
        } catch (Exception e) {
            logger.error("Error al obtener alertas activas del día: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtener alertas de los últimos N días
     */
    @GetMapping("/ultimos-dias/{dias}")
    public ResponseEntity<List<AlertaDTO>> obtenerUltimosDias(@PathVariable int dias) {
        logger.info("GET /api/v1/alertas/ultimos-dias/{} - Obteniendo alertas de últimos días", dias);

        if (dias <= 0 || dias > 365) {
            logger.warn("Parámetro 'dias' inválido: {}", dias);
            return ResponseEntity.badRequest().build();
        }

        try {
            List<AlertaDTO> alertas = alertaService.obtenerUltimosDias(dias).stream()
                    .map(AlertaDTO::fromEntity)
                    .collect(Collectors.toList());
            logger.info("Se obtuvieron {} alertas de los últimos {} días", alertas.size(), dias);
            return ResponseEntity.ok(alertas);
        } catch (Exception e) {
            logger.error("Error al obtener alertas de últimos {} días: {}", dias, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtener alertas por rango de fechas
     */
    @GetMapping("/rango")
    public ResponseEntity<List<AlertaDTO>> obtenerPorRangoFechas(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaInicio,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaFin) {

        logger.info("GET /api/v1/alertas/rango - Obteniendo alertas entre {} y {}", fechaInicio, fechaFin);

        if (fechaInicio.after(fechaFin)) {
            logger.warn("Fecha de inicio posterior a fecha fin: {} > {}", fechaInicio, fechaFin);
            return ResponseEntity.badRequest().build();
        }

        try {
            List<AlertaDTO> alertas = alertaService.obtenerPorRangoFechas(fechaInicio, fechaFin).stream()
                    .map(AlertaDTO::fromEntity)
                    .collect(Collectors.toList());
            logger.info("Se obtuvieron {} alertas en el rango de fechas", alertas.size());
            return ResponseEntity.ok(alertas);
        } catch (Exception e) {
            logger.error("Error al obtener alertas por rango de fechas: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ===== ENDPOINTS DE BÚSQUEDA =====

    /**
     * Buscar alertas por nombre del producto
     */
    @GetMapping("/buscar/producto")
    public ResponseEntity<List<AlertaDTO>> buscarPorNombreProducto(@RequestParam String nombre) {
        logger.info("GET /api/v1/alertas/buscar/producto?nombre={} - Buscando por nombre de producto", nombre);
        try {
            List<AlertaDTO> alertas = alertaService.buscarPorNombreProducto(nombre).stream()
                    .map(AlertaDTO::fromEntity)
                    .collect(Collectors.toList());
            logger.info("Se encontraron {} alertas para el producto '{}'", alertas.size(), nombre);
            return ResponseEntity.ok(alertas);
        } catch (Exception e) {
            logger.error("Error al buscar alertas por nombre de producto '{}': {}", nombre, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Buscar alertas por tipo
     */
    @GetMapping("/buscar/tipo")
    public ResponseEntity<List<AlertaDTO>> buscarPorTipo(@RequestParam String tipo) {
        logger.info("GET /api/v1/alertas/buscar/tipo?tipo={} - Buscando por tipo", tipo);
        try {
            List<AlertaDTO> alertas = alertaService.buscarPorTipo(tipo).stream()
                    .map(AlertaDTO::fromEntity)
                    .collect(Collectors.toList());
            logger.info("Se encontraron {} alertas del tipo '{}'", alertas.size(), tipo);
            return ResponseEntity.ok(alertas);
        } catch (Exception e) {
            logger.error("Error al buscar alertas por tipo '{}': {}", tipo, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ===== ENDPOINTS DE ESTADÍSTICAS =====

    /**
     * Obtener estadísticas de alertas
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        logger.info("GET /api/v1/alertas/estadisticas - Obteniendo estadísticas");
        try {
            long totalActivas = alertaService.contarActivas();
            List<AlertaDTO> ultimasAlertas = alertaService.obtenerUltimasActivas(5).stream()
                    .map(AlertaDTO::fromEntity)
                    .collect(Collectors.toList());
            List<AlertaDTO> alertasDelDia = alertaService.obtenerActivasDelDia().stream()
                    .map(AlertaDTO::fromEntity)
                    .collect(Collectors.toList());

            // Contar por nivel de urgencia
            Map<String, Long> porUrgencia = ultimasAlertas.stream()
                    .collect(Collectors.groupingBy(
                            AlertaDTO::getNivelUrgencia,
                            Collectors.counting()));

            Map<String, Object> estadisticas = new HashMap<>();
            estadisticas.put("totalActivas", totalActivas);
            estadisticas.put("alertasDelDia", alertasDelDia.size());
            estadisticas.put("ultimas5Alertas", ultimasAlertas);
            estadisticas.put("distribucionPorUrgencia", porUrgencia);

            logger.info("Estadísticas generadas: {} activas, {} del día", totalActivas, alertasDelDia.size());
            return ResponseEntity.ok(estadisticas);
        } catch (Exception e) {
            logger.error("Error al obtener estadísticas: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtener las últimas N alertas activas
     */
    @GetMapping("/ultimas/{limite}")
    public ResponseEntity<List<AlertaDTO>> obtenerUltimas(@PathVariable int limite) {
        logger.info("GET /api/v1/alertas/ultimas/{} - Obteniendo últimas alertas", limite);

        if (limite <= 0 || limite > 100) {
            limite = 10; // Valor por defecto
            logger.info("Límite ajustado a valor por defecto: {}", limite);
        }

        try {
            List<AlertaDTO> alertas = alertaService.obtenerUltimasActivas(limite).stream()
                    .map(AlertaDTO::fromEntity)
                    .collect(Collectors.toList());
            logger.info("Se obtuvieron {} últimas alertas activas", alertas.size());
            return ResponseEntity.ok(alertas);
        } catch (Exception e) {
            logger.error("Error al obtener últimas {} alertas: {}", limite, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtener alertas críticas
     */
    @GetMapping("/criticas")
    public ResponseEntity<List<AlertaDTO>> obtenerCriticas() {
        logger.info("GET /api/v1/alertas/criticas - Obteniendo alertas críticas");
        try {
            List<AlertaDTO> alertas = alertaService.obtenerActivas().stream()
                    .map(AlertaDTO::fromEntity)
                    .filter(dto -> "CRITICA".equals(dto.getNivelUrgencia()))
                    .collect(Collectors.toList());
            logger.info("Se obtuvieron {} alertas críticas", alertas.size());
            return ResponseEntity.ok(alertas);
        } catch (Exception e) {
            logger.error("Error al obtener alertas críticas: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ===== ENDPOINTS DE CAMBIO DE ESTADO =====

    /**
     * Marcar alerta como inactiva (resolver)
     */
    @PatchMapping("/resolver/{id}")
    public ResponseEntity<Map<String, String>> resolverAlerta(@PathVariable Long id) {
        logger.info("PATCH /api/v1/alertas/{}/resolver - Resolviendo alerta", id);

        try {
            boolean exito = alertaService.marcarComoInactiva(id);
            Map<String, String> response = new HashMap<>();

            if (exito) {
                response.put("message", "Alerta resuelta correctamente");
                logger.info("Alerta {} resuelta exitosamente", id);
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "No se pudo resolver la alerta");
                logger.warn("No se pudo resolver la alerta {}", id);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (RuntimeException e) {
            logger.error("Error al resolver alerta con ID {}: {}", id, e.getMessage());
            Map<String, String> response = new HashMap<>();
            response.put("message", "Alerta no encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            logger.error("Error inesperado al resolver alerta: {}", e.getMessage(), e);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Error interno del servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Marcar alerta como activa (reactivar)
     */
    @PatchMapping("/reactivar/{id}")
    public ResponseEntity<Map<String, String>> reactivarAlerta(@PathVariable Long id) {
        logger.info("PATCH /api/v1/alertas/{}/reactivar - Reactivando alerta", id);

        try {
            boolean exito = alertaService.marcarComoActiva(id);
            Map<String, String> response = new HashMap<>();

            if (exito) {
                response.put("message", "Alerta reactivada correctamente");
                logger.info("Alerta {} reactivada exitosamente", id);
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "No se pudo reactivar la alerta");
                logger.warn("No se pudo reactivar la alerta {}", id);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (RuntimeException e) {
            logger.error("Error al reactivar alerta con ID {}: {}", id, e.getMessage());
            Map<String, String> response = new HashMap<>();
            response.put("message", "Alerta no encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            logger.error("Error inesperado al reactivar alerta: {}", e.getMessage(), e);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Error interno del servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Cambiar estado de alerta (toggle)
     */
    @PatchMapping("/cambiar-estado/{id}")
    public ResponseEntity<Map<String, String>> cambiarEstado(@PathVariable Long id) {
        logger.info("PATCH /api/v1/alertas/{}/cambiar-estado - Cambiando estado", id);

        try {
            boolean exito = alertaService.cambiarEstado(id);
            Map<String, String> response = new HashMap<>();

            if (exito) {
                response.put("message", "Estado de alerta cambiado correctamente");
                logger.info("Estado de alerta {} cambiado exitosamente", id);
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "No se pudo cambiar el estado de la alerta");
                logger.warn("No se pudo cambiar el estado de la alerta {}", id);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (RuntimeException e) {
            logger.error("Error al cambiar estado de alerta con ID {}: {}", id, e.getMessage());
            Map<String, String> response = new HashMap<>();
            response.put("message", "Alerta no encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            logger.error("Error inesperado al cambiar estado: {}", e.getMessage(), e);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Error interno del servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Resolver todas las alertas de un producto
     */
    @PatchMapping("/producto/{productoId}/resolver-todas")
    public ResponseEntity<Map<String, String>> resolverTodasDelProducto(@PathVariable Long productoId) {
        logger.info("PATCH /api/v1/alertas/producto/{}/resolver-todas - Resolviendo todas las alertas del producto",
                productoId);

        try {
            int alertasResueltas = alertaService.marcarAlertasProductoComoInactivas(productoId);
            Map<String, String> response = new HashMap<>();
            response.put("message", String.format("Se resolvieron %d alertas del producto", alertasResueltas));
            logger.info("Se resolvieron {} alertas del producto {}", alertasResueltas, productoId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al resolver alertas del producto {}: {}", productoId, e.getMessage(), e);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Error al resolver alertas del producto");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ===== ENDPOINTS DE RESUMEN/DASHBOARD =====

    /**
     * Obtener resumen para dashboard
     */
    @GetMapping("/resumen")
    public ResponseEntity<AlertaService.AlertaResumen> obtenerResumen() {
        logger.info("GET /api/v1/alertas/resumen - Obteniendo resumen para dashboard");
        try {
            AlertaService.AlertaResumen resumen = alertaService.obtenerResumen();
            logger.info("Resumen generado: {} alertas activas", resumen.getTotalActivas());
            return ResponseEntity.ok(resumen);
        } catch (Exception e) {
            logger.error("Error al obtener resumen: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint para notificar nueva alerta (llamado por el trigger de MySQL)
     */
    @PostMapping("/notificar")
    @CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "*")
    public ResponseEntity<Map<String, String>> notificarNuevaAlerta(@RequestBody AlertaDTO alertaDTO) {
        logger.info("Notificación de nueva alerta recibida: {}", alertaDTO.getId());
        try {
            alertaService.obtenerPorId(alertaDTO.getId())
                    .ifPresent(alerta -> {
                        logger.info("Notificando nueva alerta a clientes SSE: {}", alerta.getId());
                        sseService.sendAlerta(alerta);
                    });

            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Alerta notificada exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al notificar nueva alerta: {}", e.getMessage(), e);
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Error al notificar alerta: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}