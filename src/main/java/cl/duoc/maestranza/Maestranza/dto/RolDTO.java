package cl.duoc.maestranza.Maestranza.dto;

import cl.duoc.maestranza.Maestranza.model.TipoRol;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolDTO {
    private Integer id;
    private TipoRol nombre;
}