package cl.duoc.maestranza.Maestranza.dto;

import cl.duoc.maestranza.Maestranza.model.HistorialPrecios;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistorialPreciosDTO {

    private Long id;

    private Double precio;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaCreacion;

    private Long productoId;

    // Constructor para crear nuevo precio
    public HistorialPreciosDTO(Double precio, Long productoId) {
        this.precio = precio;
        this.productoId = productoId;
        this.fechaCreacion = LocalDateTime.now();
    }

    public static HistorialPreciosDTO fromEntity(HistorialPrecios entity) {
        return new HistorialPreciosDTO(
                entity.getId(),
                entity.getPrecio(),
                entity.getFechaCreacion(),
                entity.getProducto().getId()
        );
    }

    public HistorialPrecios toEntity() {
        HistorialPrecios entity = new HistorialPrecios();
        entity.setId(this.id);
        entity.setPrecio(this.precio);
        entity.setFechaCreacion(this.fechaCreacion);
        return entity;
    }
}