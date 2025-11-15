package cl.duoc.maestranza.Maestranza.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@Entity
@Data
@Table(name = "alertas")
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"producto"})
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(nullable = false)
    private String descripcion;
    
    @Column(nullable = false)
    private Date fecha;
    
    @Column(nullable = false)
    private Boolean activo = true;

    @PrePersist
    protected void onCreate(){
        this.fecha = new Date();
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id", nullable = false)
    @JsonBackReference("producto-alertas")
    private Producto producto;

    /**
     * Calcula el tiempo transcurrido desde la fecha de la alerta hasta ahora
     * Considera la zona horaria del sistema
     */
    @Transient
    public String getTiempoTranscurrido() {
        if (fecha == null) {
            return "Fecha no disponible";
        }
        
        // Obtener zona horaria del sistema (probablemente Chile)
        ZoneId zonaHorariaLocal = ZoneId.systemDefault();
        
        // Convertir Date a ZonedDateTime con zona horaria local
        ZonedDateTime fechaAlertaZoned = fecha.toInstant().atZone(zonaHorariaLocal);
        ZonedDateTime ahoraZoned = ZonedDateTime.now(zonaHorariaLocal);
        
        // Calcular la duración entre las fechas
        Duration duracion = Duration.between(fechaAlertaZoned, ahoraZoned);
        
        // Debug: imprimir información para diagnóstico
        System.out.println("=== DEBUG TIEMPO TRANSCURRIDO ===");
        System.out.println("Fecha alerta (Date): " + fecha);
        System.out.println("Fecha alerta (ZonedDateTime): " + fechaAlertaZoned);
        System.out.println("Ahora (ZonedDateTime): " + ahoraZoned);
        System.out.println("Zona horaria: " + zonaHorariaLocal);
        System.out.println("Duración en segundos: " + duracion.getSeconds());
        System.out.println("Duración en minutos: " + duracion.toMinutes());
        System.out.println("Duración en horas: " + duracion.toHours());
        System.out.println("===============================");
        
        return formatearTiempoTranscurrido(duracion);
    }
    
    /**
     * Formatea la duración en un string legible
     */
    private String formatearTiempoTranscurrido(Duration duracion) {
        long segundos = duracion.getSeconds();
        
        // Si el tiempo es negativo, la fecha está en el futuro
        if (segundos < 0) {
            return "En el futuro";
        }
        
        if (segundos < 60) {
            return "Hace menos de 1 minuto";
        } else if (segundos < 3600) { // Menos de 1 hora
            long minutos = segundos / 60;
            return String.format("Hace %d minuto%s", minutos, minutos != 1 ? "s" : "");
        } else if (segundos < 86400) { // Menos de 1 día
            long horas = segundos / 3600;
            return String.format("Hace %d hora%s", horas, horas != 1 ? "s" : "");
        } else if (segundos < 2592000) { // Menos de 30 días
            long dias = segundos / 86400;
            return String.format("Hace %d día%s", dias, dias != 1 ? "s" : "");
        } else if (segundos < 31536000) { // Menos de 1 año
            long meses = segundos / 2592000;
            return String.format("Hace %d mes%s", meses, meses != 1 ? "es" : "");
        } else {
            long años = segundos / 31536000;
            return String.format("Hace %d año%s", años, años != 1 ? "s" : "");
        }
    }
    
    @Transient
    public String getResumenCorto() {
        String codigoProducto = (producto != null && producto.getCodigo() != null) 
            ? producto.getCodigo() 
            : "N/A";
        
        return String.format("%s - %s (%s)", 
            codigoProducto, 
            nombre, 
            getTiempoTranscurrido());
    }
}