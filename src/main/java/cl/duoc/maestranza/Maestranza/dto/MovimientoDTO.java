package cl.duoc.maestranza.Maestranza.dto;

import cl.duoc.maestranza.Maestranza.model.Movimiento;
import cl.duoc.maestranza.Maestranza.model.TipoMovimiento;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoDTO {

    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fecha;

    private Long usuarioId;
    private UsuarioDTO usuario;

    private Long productoId;
    private ProductoDTO producto;

    private Integer cantidad;

    private TipoMovimiento tipo;

    private String descripcion;

    private String productoCodigo;

    private String productoNombre;

    private String imagePath;

    // Constructor básico para crear movimiento
    public MovimientoDTO(Long usuarioId, Long productoId, Integer cantidad, TipoMovimiento tipo, String descripcion) {
        this.usuarioId = usuarioId;
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.fecha = LocalDateTime.now();
    }

    public static MovimientoDTO fromEntity(Movimiento entity) {
        MovimientoDTO dto = new MovimientoDTO();
        dto.setId(entity.getId());
        dto.setFecha(entity.getFecha());
        dto.setCantidad(entity.getCantidad());
        dto.setTipo(entity.getTipo());
        dto.setDescripcion(entity.getDescripcion());
        dto.setProductoCodigo(entity.getProductoCodigo());
        dto.setProductoNombre(entity.getProductoNombre());
        dto.setImagePath(entity.getImagePath());

        // Mapear usuario
        if (entity.getUsuario() != null) {
            dto.setUsuarioId(entity.getUsuario().getId());
            dto.setUsuario(UsuarioDTO.fromEntity(entity.getUsuario()));
        }

        // Mapear producto
        if (entity.getProducto() != null) {
            dto.setProductoId(entity.getProducto().getId());
            dto.setProducto(ProductoDTO.fromEntity(entity.getProducto()));
        }

        return dto;
    }

    public Movimiento toEntity() {
        Movimiento entity = new Movimiento();
        entity.setId(this.id);
        entity.setFecha(this.fecha);
        entity.setCantidad(this.cantidad);
        entity.setTipo(this.tipo);
        entity.setDescripcion(this.descripcion);
        entity.setProductoCodigo(this.productoCodigo);
        entity.setProductoNombre(this.productoNombre);
        entity.setImagePath(this.imagePath);
        return entity;
    }
} 