package cl.duoc.maestranza.Maestranza.service;

import cl.duoc.maestranza.Maestranza.dto.ActualizarUsuarioDTO;
import cl.duoc.maestranza.Maestranza.dto.UsuarioDTO;
import cl.duoc.maestranza.Maestranza.model.Rol;
import cl.duoc.maestranza.Maestranza.model.TipoRol;
import cl.duoc.maestranza.Maestranza.model.Usuario;
import cl.duoc.maestranza.Maestranza.repository.RolRepository;
import cl.duoc.maestranza.Maestranza.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RolRepository rolRepository;

    /**
     * Convierte un Usuario a UsuarioDTO (sin incluir la contraseña)
     */
    private UsuarioDTO convertToDTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setUsername(usuario.getUsername());
        dto.setEmail(usuario.getEmail());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setActivo(usuario.isActivo());

        // Convertir roles a strings correctamente
        Set<String> rolesString = usuario.getRoles().stream()
                .map(rol -> rol.getNombre().name())  // Aquí convertimos TipoRol a String
                .collect(Collectors.toSet());
        dto.setRoles(rolesString);

        return dto;
    }


    /**
     * Obtener todos los usuarios
     */
    public List<UsuarioDTO> obtenerTodos() {
        logger.info("Obteniendo todos los usuarios");
        return usuarioRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener usuario por ID
     */
    public Optional<UsuarioDTO> obtenerPorId(Long id) {
        logger.info("Buscando usuario con ID: {}", id);
        return usuarioRepository.findById(id)
                .map(this::convertToDTO);
    }

    /**
     * Obtener usuario por nombre de usuario
     */
    public Optional<UsuarioDTO> obtenerPorUsername(String username) {
        logger.info("Buscando usuario con username: {}", username);
        return usuarioRepository.findByUsername(username)
                .map(this::convertToDTO);
    }

    /**
     * Crear un nuevo usuario
     * @throws IllegalArgumentException si el username o el email ya están en uso
     */
    public UsuarioDTO crear(Usuario usuario) {
        logger.info("Creando nuevo usuario: {}", usuario.getUsername());

        // Validar que el username no exista
        if (existeUsername(usuario.getUsername())) {
            logger.error("Error al crear usuario: El nombre de usuario {} ya está en uso", usuario.getUsername());
            throw new IllegalArgumentException("El nombre de usuario ya está en uso");
        }

        // Validar que el email no exista
        if (existeEmail(usuario.getEmail())) {
            logger.error("Error al crear usuario: El correo {} ya está en uso", usuario.getEmail());
            throw new IllegalArgumentException("El correo electrónico ya está en uso");
        }

        // Encriptar la contraseña antes de guardar
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        return convertToDTO(usuarioGuardado);
    }

    /**
     * Actualiza un usuario existente con los datos proporcionados
     * Solo permite modificar: nombre, apellido, email y roles
     */
    public Optional<UsuarioDTO> actualizar(Long id, ActualizarUsuarioDTO dto) {
        logger.info("Actualizando usuario con ID: {}", id);

        return usuarioRepository.findById(id)
                .map(usuarioExistente -> {
                    // Actualizar datos básicos
                    if (dto.getEmail() != null) {
                        usuarioExistente.setEmail(dto.getEmail());
                    }

                    if (dto.getNombre() != null) {
                        usuarioExistente.setNombre(dto.getNombre());
                    }

                    if (dto.getApellido() != null) {
                        usuarioExistente.setApellido(dto.getApellido());
                    }

                    // Procesar roles si se proporcionan
                    if (dto.getRoles() != null && !dto.getRoles().isEmpty()) {
                        Set<Rol> roles = new HashSet<>();
                        for (String rolNombre : dto.getRoles()) {
                            try {
                                TipoRol tipoRol = TipoRol.valueOf(rolNombre);
                                rolRepository.findByNombre(tipoRol)
                                        .ifPresent(roles::add);
                            } catch (IllegalArgumentException e) {
                                logger.error("Rol no válido: {}", rolNombre);
                            }
                        }

                        if (!roles.isEmpty()) {
                            usuarioExistente.setRoles(roles);
                        }
                    }

                    return convertToDTO(usuarioRepository.save(usuarioExistente));
                });
    }

    /**
     * Eliminar un usuario
     */
    public boolean eliminar(Long id) {
        logger.info("Eliminando usuario con ID: {}", id);

        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Desactivar un usuario
     */
    public Optional<UsuarioDTO> desactivarUsuario(Long id) {
        logger.info("Desactivando usuario con ID: {}", id);

        return usuarioRepository.findById(id)
                .map(usuario -> {
                    usuario.setActivo(false);
                    return convertToDTO(usuarioRepository.save(usuario));
                });
    }

    /**
     * Comprobar si existe un usuario por nombre de usuario
     */
    public boolean existeUsername(String username) {
        return usuarioRepository.existsByUsername(username);
    }

    /**
     * Comprobar si existe un usuario por email
     */
    public boolean existeEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    /**
     * Activa un usuario estableciendo su estado como activo
     *
     * @param id ID del usuario a activar
     * @return DTO del usuario activado
     */
    public UsuarioDTO activarUsuario(Long id) {
        logger.info("Activando usuario con ID: {}", id);

        return usuarioRepository.findById(id)
                .map(usuario -> {
                    usuario.setActivo(true);
                    return convertToDTO(usuarioRepository.save(usuario));
                })
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    // ========== MÉTODOS QUE RETORNAN ENTIDADES ==========

    /**
     * Obtener usuario por ID como entidad
     */
    public Optional<Usuario> obtenerPorIdEntity(Long id) {
        logger.info("Buscando usuario entidad con ID: {}", id);
        return usuarioRepository.findById(id);
    }

    /**
     * Obtener usuario por nombre de usuario como entidad
     */
    public Optional<Usuario> obtenerPorUsernameEntity(String username) {
        logger.info("Buscando usuario entidad con username: {}", username);
        return usuarioRepository.findByUsername(username);
    }

    @PostConstruct
    public void logUsuariosAlIniciar() {
        System.out.println("=== USUARIOS ENCONTRADOS AL INICIAR LA APP ===");
        usuarioRepository.findAll().forEach(u ->
            System.out.println("Usuario: id=" + u.getId() + ", username=" + u.getUsername() + ", activo=" + u.isActivo())
        );
        System.out.println("=============================================");
    }
}