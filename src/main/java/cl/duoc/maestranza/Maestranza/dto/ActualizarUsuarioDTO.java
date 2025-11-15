package cl.duoc.maestranza.Maestranza.dto;

import lombok.Data;

import java.util.Set;

@Data
public class ActualizarUsuarioDTO {
    private String email;
    private String nombre;
    private String apellido;
    private Set<String> roles; // Nombres de roles como strings
}