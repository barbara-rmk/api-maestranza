package cl.duoc.maestranza.Maestranza.dto;

import cl.duoc.maestranza.Maestranza.model.Producto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {

    private Long id;

    private String codigo;

    private String nombre;

    private String descripcion;

    private Integer stock;

    private String imageUrl;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaIngreso;

    private String ubicacion;

    private Boolean activo;

    private Integer umbralStock;

    // Relación con categoría
    private Long categoriaId;
    private CategoriaDTO categoria;

    // Historial de precios
    private List<HistorialPreciosDTO> historialPrecios;

    // Campo para precio inicial/nuevo (para operaciones create/update)
    private Double precio;

    // Constructor básico para crear producto
    public ProductoDTO(String codigo, String nombre, Integer stock, Long categoriaId, Double precio) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.stock = stock;
        this.categoriaId = categoriaId;
        this.precio = precio;
        this.activo = true;
        this.umbralStock = 5;
        this.ubicacion = "Almacén Principal";
        this.fechaIngreso = LocalDateTime.now();
    }

    public static ProductoDTO fromEntity(Producto entity) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(entity.getId());
        dto.setCodigo(entity.getCodigo());
        dto.setNombre(entity.getNombre());
        dto.setDescripcion(entity.getDescripcion());
        dto.setStock(entity.getStock());
        dto.setImageUrl(entity.getImageUrl());
        dto.setFechaIngreso(entity.getFechaIngreso());
        dto.setUbicacion(entity.getUbicacion());
        dto.setActivo(entity.getActivo());
        dto.setUmbralStock(entity.getUmbralStock());

        // Mapear categoría
        if (entity.getCategoria() != null) {
            dto.setCategoriaId(entity.getCategoria().getId());
            dto.setCategoria(CategoriaDTO.fromEntity(entity.getCategoria()));
        }

        // Mapear historial de precios
        if (entity.getHistorialPrecios() != null) {
            dto.setHistorialPrecios(
                    entity.getHistorialPrecios().stream()
                            .map(HistorialPreciosDTO::fromEntity)
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }

    public Producto toEntity() {
        Producto entity = new Producto();
        entity.setId(this.id);
        entity.setCodigo(this.codigo);
        entity.setNombre(this.nombre);
        entity.setDescripcion(this.descripcion);
        entity.setStock(this.stock);
        entity.setImageUrl(this.imageUrl);
        entity.setFechaIngreso(this.fechaIngreso);
        entity.setUbicacion(this.ubicacion);
        entity.setActivo(this.activo);
        entity.setUmbralStock(this.umbralStock);
        return entity;
    }

    public Double getPrecioActual() {
        if (historialPrecios != null && !historialPrecios.isEmpty()) {
            return historialPrecios.get(0).getPrecio();
        }
        return precio != null ? precio : 0.0;
    }
}