// filepath: c:\Users\Pablo\Documents\GitHub\Maestranza\backend\src\main\java\cl\duoc\maestranza\Maestranza\payload\response\JwtResponse.java
package cl.duoc.maestranza.Maestranza.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private String nombre;
    private String apellido;
    private List<String> roles;
    private boolean activo;

    public JwtResponse(String token, Long id, String username, String email, String nombre, String apellido,
            List<String> roles, boolean activo) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.nombre = nombre;
        this.apellido = apellido;
        this.roles = roles;
        this.activo = activo;
    }
}