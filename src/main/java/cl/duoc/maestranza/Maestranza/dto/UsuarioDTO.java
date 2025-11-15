package cl.duoc.maestranza.Maestranza.dto;

import cl.duoc.maestranza.Maestranza.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private Long id;
    private String username;
    private String email;
    private String nombre;
    private String apellido;
    private boolean activo;
    private Set<String> roles = new HashSet<>();

    public static UsuarioDTO fromEntity(Usuario entity) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setEmail(entity.getEmail());
        dto.setNombre(entity.getNombre());
        dto.setApellido(entity.getApellido());
        dto.setActivo(entity.isActivo());
        
        // Mapear roles
        if (entity.getRoles() != null) {
            dto.setRoles(entity.getRoles().stream()
                    .map(rol -> rol.getNombre().name())
                    .collect(Collectors.toSet()));
        }
        
        return dto;
    }
}