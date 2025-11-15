package cl.duoc.maestranza.Maestranza.dto;

import cl.duoc.maestranza.Maestranza.model.Categoria;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaDTO {

    private Long id;

    private String nombre;

    private String descripcion;

    // Constructor para crear nueva categoría
    public CategoriaDTO(String nombre) {
        this.nombre = nombre;
    }

    // Constructor para crear categoría con descripción
    public CategoriaDTO(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public static CategoriaDTO fromEntity(Categoria entity) {
        return new CategoriaDTO(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion()
        );
    }

    public Categoria toEntity() {
        Categoria entity = new Categoria();
        entity.setId(this.id);
        entity.setNombre(this.nombre);
        entity.setDescripcion(this.descripcion);
        return entity;
    }
}