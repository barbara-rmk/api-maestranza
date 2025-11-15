package cl.duoc.maestranza.Maestranza.dto;

import cl.duoc.maestranza.Maestranza.model.Alerta;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertaDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date fecha;
    
    private Boolean activo;
    
    // Información del producto relacionado (sin referencias circulares)
    private Long productoId;
    private String productoCodigo;
    private String productoNombre;
    private Integer productoStock;
    private Integer productoUmbralStock;
    private String productoUbicacion;
    private String categoriaNombre;
    
    // Campos calculados útiles para el frontend
    private String tiempoTranscurrido;
    private String nivelUrgencia;

    /**
     * Convierte una entidad Alerta a DTO
     * Este es el método principal ya que solo leemos alertas
     */
    public static AlertaDTO fromEntity(Alerta alerta) {
        AlertaDTO dto = new AlertaDTO();
        dto.setId(alerta.getId());
        dto.setNombre(alerta.getNombre());
        dto.setDescripcion(alerta.getDescripcion());
        dto.setFecha(alerta.getFecha());
        dto.setActivo(alerta.getActivo());
        
        // Mapear información del producto
        if (alerta.getProducto() != null) {
            dto.setProductoId(alerta.getProducto().getId());
            dto.setProductoCodigo(alerta.getProducto().getCodigo());
            dto.setProductoNombre(alerta.getProducto().getNombre());
            dto.setProductoStock(alerta.getProducto().getStock());
            dto.setProductoUmbralStock(alerta.getProducto().getUmbralStock());
            dto.setProductoUbicacion(alerta.getProducto().getUbicacion());
            
            // Información de la categoría
            if (alerta.getProducto().getCategoria() != null) {
                dto.setCategoriaNombre(alerta.getProducto().getCategoria().getNombre());
            }
        }
        
        // Calcular campos adicionales
        dto.setTiempoTranscurrido(calcularTiempoTranscurrido(alerta.getFecha()));
        dto.setNivelUrgencia(determinarNivelUrgencia(alerta));
        
        return dto;
    }

    /**
     * Calcula el tiempo transcurrido desde la creación de la alerta
     */
    private static String calcularTiempoTranscurrido(Date fechaAlerta) {
        if (fechaAlerta == null) return "Desconocido";
        
        long diferencia = new Date().getTime() - fechaAlerta.getTime();
        long minutos = diferencia / (60 * 1000);
        long horas = diferencia / (60 * 60 * 1000);
        long dias = diferencia / (24 * 60 * 60 * 1000);
        
        if (dias > 0) {
            return dias == 1 ? "Hace 1 día" : "Hace " + dias + " días";
        } else if (horas > 0) {
            return horas == 1 ? "Hace 1 hora" : "Hace " + horas + " horas";
        } else if (minutos > 0) {
            return minutos == 1 ? "Hace 1 minuto" : "Hace " + minutos + " minutos";
        } else {
            return "Hace un momento";
        }
    }

    /**
     * Determina el nivel de urgencia basado en el tipo de alerta y tiempo
     */
    private static String determinarNivelUrgencia(Alerta alerta) {
        if (!alerta.getActivo()) {
            return "RESUELTA";
        }
        
        String nombre = alerta.getNombre().toLowerCase();
        long horasTranscurridas = (new Date().getTime() - alerta.getFecha().getTime()) / (60 * 60 * 1000);
        
        // Lógica de urgencia basada en el tipo de alerta
        if (nombre.contains("stock agotado") || nombre.contains("sin stock")) {
            return "CRITICA";
        } else if (nombre.contains("stock bajo") || nombre.contains("bajo umbral")) {
            return horasTranscurridas > 24 ? "ALTA" : "MEDIA";
        } else if (nombre.contains("precio") || nombre.contains("actualización")) {
            return "BAJA";
        }
        
        // Por defecto, basado en tiempo
        if (horasTranscurridas > 48) {
            return "ALTA";
        } else if (horasTranscurridas > 24) {
            return "MEDIA";
        } else {
            return "BAJA";
        }
    }

    /**
     * Verifica si la alerta está vencida (más de X días sin resolver)
     */
    public boolean estaVencida() {
        if (!activo) return false;
        
        long diasTranscurridos = (new Date().getTime() - fecha.getTime()) / (24 * 60 * 60 * 1000);
        return diasTranscurridos > 7; // Considerar vencida después de 7 días
    }

    /**
     * Verifica si es una alerta crítica de stock
     */
    public boolean esCriticaStock() {
        return activo && nombre != null && 
               (nombre.toLowerCase().contains("agotado") || 
                nombre.toLowerCase().contains("sin stock"));
    }

    /**
     * Obtiene un resumen corto para mostrar en listas
     */
    public String getResumenCorto() {
        return String.format("%s - %s (%s)", 
                productoCodigo != null ? productoCodigo : "N/A",
                nombre != null ? nombre : "Sin nombre",
                tiempoTranscurrido != null ? tiempoTranscurrido : "");
    }
}