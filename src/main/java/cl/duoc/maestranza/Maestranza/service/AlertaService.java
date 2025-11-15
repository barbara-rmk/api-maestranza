package cl.duoc.maestranza.Maestranza.service;

import cl.duoc.maestranza.Maestranza.model.Alerta;
import cl.duoc.maestranza.Maestranza.repository.AlertaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
public class AlertaService {

    private static final Logger logger = LoggerFactory.getLogger(AlertaService.class);

    @Autowired
    private AlertaRepository alertaRepository;

    @Autowired
    private SseService sseService;

    @Scheduled(fixedDelay = 10000) // Verificar cada 10 segundos
    public void verificarNuevasAlertas() {
        logger.info("Verificando nuevas alertas...");

        try {
            // Calculamos un tiempo "hace 15 segundos" para buscar solo alertas muy recientes
            // Esto asegura que solo se envíen alertas que realmente son nuevas
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.SECOND, -10);
            Date haceUnMomento = cal.getTime();

            // Buscamos SOLO alertas creadas en los últimos 15 segundos
            List<Alerta> nuevasAlertas = alertaRepository.findByFechaBetweenAndActivoTrueOrderByFechaDesc(
                    haceUnMomento, new Date());

            if (!nuevasAlertas.isEmpty()) {
                logger.info("Se encontraron {} nuevas alertas para notificar", nuevasAlertas.size());
                nuevasAlertas.forEach(alerta -> {
                    sseService.sendAlerta(alerta);
                    logger.debug("Notificación enviada para alerta: {} ({})", alerta.getNombre(), alerta.getId());
                });
            }
        } catch (Exception e) {
            logger.error("Error al verificar nuevas alertas: {}", e.getMessage(), e);
        }
    }
    // ===== CONSULTAS GENERALES =====

    /**
     * Obtener todas las alertas ordenadas por fecha (más recientes primero)
     */
    public List<Alerta> obtenerTodas() {
        logger.info("Obteniendo todas las alertas");
        return alertaRepository.findAllByOrderByFechaDesc();
    }

    /**
     * Obtener solo alertas activas
     */
    public List<Alerta> obtenerActivas() {
        logger.info("Obteniendo alertas activas");
        return alertaRepository.findByActivoTrueOrderByFechaDesc();
    }

    /**
     * Obtener solo alertas inactivas
     */
    public List<Alerta> obtenerInactivas() {
        logger.info("Obteniendo alertas inactivas");
        return alertaRepository.findByActivoFalseOrderByFechaDesc();
    }

    /**
     * Obtener alerta por ID
     */
    public Optional<Alerta> obtenerPorId(Long id) {
        logger.info("Buscando alerta con ID: {}", id);
        return alertaRepository.findById(id);
    }

    // ===== CONSULTAS POR PRODUCTO =====

    /**
     * Obtener todas las alertas de un producto específico
     */
    public List<Alerta> obtenerPorProducto(Long productoId) {
        logger.info("Obteniendo alertas para producto con ID: {}", productoId);
        return alertaRepository.findByProductoIdOrderByFechaDesc(productoId);
    }

    /**
     * Obtener alertas activas de un producto específico
     */
    public List<Alerta> obtenerActivasPorProducto(Long productoId) {
        logger.info("Obteniendo alertas activas para producto con ID: {}", productoId);
        return alertaRepository.findByProductoIdAndActivoTrueOrderByFechaDesc(productoId);
    }

    /**
     * Obtener alertas por código de producto
     */
    public List<Alerta> obtenerPorCodigoProducto(String codigoProducto) {
        logger.info("Obteniendo alertas para producto con código: {}", codigoProducto);
        return alertaRepository.findByProductoCodigo(codigoProducto);
    }

    // ===== CONSULTAS POR FECHA =====

    /**
     * Obtener alertas entre dos fechas
     */
    public List<Alerta> obtenerPorRangoFechas(Date fechaInicio, Date fechaFin) {
        logger.info("Obteniendo alertas entre {} y {}", fechaInicio, fechaFin);
        return alertaRepository.findByFechaBetweenOrderByFechaDesc(fechaInicio, fechaFin);
    }

    /**
     * Obtener alertas activas desde una fecha específica
     */
    public List<Alerta> obtenerActivasDesde(Date fechaDesde) {
        logger.info("Obteniendo alertas activas desde: {}", fechaDesde);
        return alertaRepository.findAlertasActivasDesde(fechaDesde);
    }

    /**
     * Obtener alertas del día actual
     */
    public List<Alerta> obtenerDelDia() {
        logger.info("Obteniendo alertas del día actual");
        return alertaRepository.findAlertasDelDia();
    }

    /**
     * Obtener alertas activas del día actual
     */
    public List<Alerta> obtenerActivasDelDia() {
        logger.info("Obteniendo alertas activas del día actual");
        return alertaRepository.findAlertasActivasDelDia();
    }

    /**
     * Obtener alertas de los últimos N días
     */
    public List<Alerta> obtenerUltimosDias(int dias) {
        logger.info("Obteniendo alertas de los últimos {} días", dias);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -dias);
        Date fechaDesde = cal.getTime();
        return alertaRepository.findAlertasActivasDesde(fechaDesde);
    }

    // ===== CONSULTAS DE BÚSQUEDA =====

    /**
     * Buscar alertas por nombre del producto
     */
    public List<Alerta> buscarPorNombreProducto(String nombreProducto) {
        logger.info("Buscando alertas por nombre de producto: {}", nombreProducto);
        return alertaRepository.findByProductoNombreContaining(nombreProducto);
    }

    /**
     * Buscar alertas por tipo (nombre de alerta)
     */
    public List<Alerta> buscarPorTipo(String tipo) {
        logger.info("Buscando alertas por tipo: {}", tipo);
        return alertaRepository.findByNombreContainingIgnoreCaseOrderByFechaDesc(tipo);
    }

    // ===== CONSULTAS DE CONTEO Y ESTADÍSTICAS =====

    /**
     * Contar total de alertas activas
     */
    public long contarActivas() {
        logger.info("Contando alertas activas");
        return alertaRepository.countByActivoTrue();
    }

    /**
     * Contar alertas por producto
     */
    public long contarPorProducto(Long productoId) {
        logger.info("Contando alertas para producto con ID: {}", productoId);
        return alertaRepository.countByProductoId(productoId);
    }

    /**
     * Contar alertas activas por producto
     */
    public long contarActivasPorProducto(Long productoId) {
        logger.info("Contando alertas activas para producto con ID: {}", productoId);
        return alertaRepository.countByProductoIdAndActivoTrue(productoId);
    }

    /**
     * Obtener las últimas N alertas activas
     */
    public List<Alerta> obtenerUltimasActivas(int limite) {
        logger.info("Obteniendo las últimas {} alertas activas", limite);
        return alertaRepository.findTopAlertasActivas(limite);
    }

    // ===== OPERACIONES DE CAMBIO DE ESTADO =====

    /**
     * Marcar una alerta específica como inactiva
     */
    @Transactional
    public boolean marcarComoInactiva(Long id) {
        logger.info("Marcando alerta como inactiva con ID: {}", id);

        if (!alertaRepository.existsById(id)) {
            logger.warn("No se encontró alerta con ID: {}", id);
            throw new RuntimeException("Alerta no encontrada con ID: " + id);
        }

        int filasAfectadas = alertaRepository.marcarComoInactiva(id);
        boolean exito = filasAfectadas > 0;

        if (exito) {
            logger.info("Alerta marcada como inactiva exitosamente. ID: {}", id);
        } else {
            logger.warn("No se pudo marcar la alerta como inactiva. ID: {}", id);
        }

        return exito;
    }

    /**
     * Marcar una alerta específica como activa
     */
    @Transactional
    public boolean marcarComoActiva(Long id) {
        logger.info("Marcando alerta como activa con ID: {}", id);

        if (!alertaRepository.existsById(id)) {
            logger.warn("No se encontró alerta con ID: {}", id);
            throw new RuntimeException("Alerta no encontrada con ID: " + id);
        }

        int filasAfectadas = alertaRepository.marcarComoActiva(id);
        boolean exito = filasAfectadas > 0;

        if (exito) {
            logger.info("Alerta marcada como activa exitosamente. ID: {}", id);
        } else {
            logger.warn("No se pudo marcar la alerta como activa. ID: {}", id);
        }

        return exito;
    }

    /**
     * Marcar todas las alertas de un producto como inactivas
     */
    @Transactional
    public int marcarAlertasProductoComoInactivas(Long productoId) {
        logger.info("Marcando todas las alertas del producto como inactivas. Producto ID: {}", productoId);

        int filasAfectadas = alertaRepository.marcarAlertasProductoComoInactivas(productoId);
        logger.info("Se marcaron {} alertas como inactivas para el producto ID: {}", filasAfectadas, productoId);

        return filasAfectadas;
    }

    /**
     * Cambiar estado de una alerta (toggle activa/inactiva)
     */
    @Transactional
    public boolean cambiarEstado(Long id) {
        logger.info("Cambiando estado de alerta con ID: {}", id);

        Optional<Alerta> alertaOpt = alertaRepository.findById(id);
        if (alertaOpt.isEmpty()) {
            logger.warn("No se encontró alerta con ID: {}", id);
            throw new RuntimeException("Alerta no encontrada con ID: " + id);
        }

        Alerta alerta = alertaOpt.get();
        boolean nuevoEstado = !alerta.getActivo();

        int filasAfectadas = nuevoEstado ? alertaRepository.marcarComoActiva(id)
                : alertaRepository.marcarComoInactiva(id);

        boolean exito = filasAfectadas > 0;

        if (exito) {
            logger.info("Estado de alerta cambiado exitosamente. ID: {}, Nuevo estado: {}", id,
                    nuevoEstado ? "ACTIVA" : "INACTIVA");
        } else {
            logger.warn("No se pudo cambiar el estado de la alerta. ID: {}", id);
        }

        return exito;
    }

    // ===== VERIFICACIONES =====

    /**
     * Verificar si un producto tiene alertas activas
     */
    public boolean tieneAlertasActivas(Long productoId) {
        logger.info("Verificando si producto tiene alertas activas. Producto ID: {}", productoId);
        return alertaRepository.existsByProductoIdAndActivoTrue(productoId);
    }

    /**
     * Verificar si una alerta existe
     */
    public boolean existe(Long id) {
        return alertaRepository.existsById(id);
    }

    // ===== MÉTODOS DE UTILIDAD =====

    /**
     * Obtener resumen de alertas para dashboard
     */
    public AlertaResumen obtenerResumen() {
        logger.info("Generando resumen de alertas");

        long totalActivas = alertaRepository.countByActivoTrue();
        List<Alerta> ultimasAlertas = alertaRepository.findTopAlertasActivas(5);
        List<Alerta> alertasDelDia = alertaRepository.findAlertasActivasDelDia();

        AlertaResumen resumen = new AlertaResumen();
        resumen.setTotalActivas(totalActivas);
        resumen.setUltimasAlertas(ultimasAlertas.size());
        resumen.setAlertasDelDia(alertasDelDia.size());
        resumen.setUltimas5Alertas(ultimasAlertas);

        return resumen;
    }

    /**
     * Notificar a todos los clientes SSE sobre una nueva alerta
     */

    // ===== CLASE INTERNA PARA RESUMEN =====

    public static class AlertaResumen {
        private long totalActivas;
        private int ultimasAlertas;
        private int alertasDelDia;
        private List<Alerta> ultimas5Alertas;

        // Constructors
        public AlertaResumen() {
        }

        public AlertaResumen(long totalActivas, int ultimasAlertas, int alertasDelDia, List<Alerta> ultimas5Alertas) {
            this.totalActivas = totalActivas;
            this.ultimasAlertas = ultimasAlertas;
            this.alertasDelDia = alertasDelDia;
            this.ultimas5Alertas = ultimas5Alertas;
        }

        // Getters y Setters
        public long getTotalActivas() {
            return totalActivas;
        }

        public void setTotalActivas(long totalActivas) {
            this.totalActivas = totalActivas;
        }

        public int getUltimasAlertas() {
            return ultimasAlertas;
        }

        public void setUltimasAlertas(int ultimasAlertas) {
            this.ultimasAlertas = ultimasAlertas;
        }

        public int getAlertasDelDia() {
            return alertasDelDia;
        }

        public void setAlertasDelDia(int alertasDelDia) {
            this.alertasDelDia = alertasDelDia;
        }

        public List<Alerta> getUltimas5Alertas() {
            return ultimas5Alertas;
        }

        public void setUltimas5Alertas(List<Alerta> ultimas5Alertas) {
            this.ultimas5Alertas = ultimas5Alertas;
        }
    }
}