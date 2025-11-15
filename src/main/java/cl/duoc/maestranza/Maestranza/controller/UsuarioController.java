package cl.duoc.maestranza.Maestranza.controller;

import cl.duoc.maestranza.Maestranza.dto.ActualizarUsuarioDTO;
import cl.duoc.maestranza.Maestranza.dto.UsuarioDTO;
import cl.duoc.maestranza.Maestranza.model.Usuario;
import cl.duoc.maestranza.Maestranza.service.UsuarioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Obtener todos los usuarios
     */
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> obtenerTodos() {
        logger.info("Petición para obtener todos los usuarios");
        List<UsuarioDTO> usuarios = usuarioService.obtenerTodos();
        return ResponseEntity.ok(usuarios);
    }

    /**
     * Obtener usuario por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obtenerPorId(@PathVariable Long id) {
        logger.info("Petición para obtener usuario con ID: {}", id);
        return usuarioService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtener usuario por nombre de usuario
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<UsuarioDTO> obtenerPorUsername(@PathVariable String username) {
        logger.info("Petición para obtener usuario con username: {}", username);
        return usuarioService.obtenerPorUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crearUsuario(@Valid @RequestBody Usuario usuario) {
        logger.info("Petición para crear usuario: {}", usuario.getUsername());

        try {
            // Verificar si el nombre de usuario ya existe
            if (usuarioService.existeUsername(usuario.getUsername())) {
                logger.warn("Intento de crear usuario con username existente: {}", usuario.getUsername());
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: El nombre de usuario ya está en uso"));
            }

            // Verificar si el correo ya existe
            if (usuarioService.existeEmail(usuario.getEmail())) {
                logger.warn("Intento de crear usuario con email existente: {}", usuario.getEmail());
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: El correo electrónico ya está en uso"));
            }

            UsuarioDTO nuevoUsuario = usuarioService.crear(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
        } catch (IllegalArgumentException e) {
            // Captura excepciones específicas de validación lanzadas por el servicio
            logger.error("Error de validación al crear usuario: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        } catch (Exception e) {
            // Captura cualquier otra excepción inesperada
            logger.error("Error inesperado al crear usuario: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error al procesar la solicitud: " + e.getMessage()));
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @Valid @RequestBody ActualizarUsuarioDTO dto) {
        logger.info("Petición para actualizar usuario con ID: {}", id);

        try {
            // Obtener el usuario actual para verificaciones
            Optional<UsuarioDTO> usuarioActual = usuarioService.obtenerPorId(id);
            if (usuarioActual.isEmpty()) {
                logger.warn("Intento de actualizar usuario no existente con ID: {}", id);
                return ResponseEntity.notFound().build();
            }

            // Verificar si el email ya existe y no pertenece al usuario que se está actualizando
            if (dto.getEmail() != null && !dto.getEmail().equals(usuarioActual.get().getEmail())
                    && usuarioService.existeEmail(dto.getEmail())) {
                logger.warn("Intento de actualizar usuario con email existente: {}", dto.getEmail());
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: El correo electrónico ya está en uso"));
            }

            return usuarioService.actualizar(id, dto)
                    .map(usuarioActualizado -> {
                        logger.info("Usuario actualizado correctamente: {}", id);
                        return ResponseEntity.ok(usuarioActualizado);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            logger.error("Error de validación al actualizar usuario: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Error inesperado al actualizar usuario: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error al procesar la solicitud: " + e.getMessage()));
        }
    }

    /**
     * Eliminar un usuario (solo administradores)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        logger.info("Petición para eliminar usuario con ID: {}", id);

        if (usuarioService.eliminar(id)) {
            return ResponseEntity.ok(new MessageResponse("Usuario eliminado correctamente"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Desactivar un usuario
     */
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<?> desactivarUsuario(@PathVariable Long id) {
        logger.info("Petición para desactivar usuario con ID: {}", id);

        return usuarioService.desactivarUsuario(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Activar un usuario
     */
    @PatchMapping("/{id}/activar")
    public ResponseEntity<?> activarUsuario(@PathVariable Long id) {
        try {
            UsuarioDTO usuarioActivado = usuarioService.activarUsuario(id);
            return ResponseEntity.ok(usuarioActivado);
        } catch (RuntimeException e) {
            logger.error("Error al activar usuario con ID {}: {}", id, e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Error al activar usuario: " + e.getMessage()));
        }
    }

    /**
     * Clase para respuestas de mensaje
     */
    class MessageResponse {
        private String message;

        public MessageResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
