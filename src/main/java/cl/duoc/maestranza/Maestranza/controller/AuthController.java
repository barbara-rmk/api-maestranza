package cl.duoc.maestranza.Maestranza.controller;

import cl.duoc.maestranza.Maestranza.model.Rol;
import cl.duoc.maestranza.Maestranza.model.TipoRol;
import cl.duoc.maestranza.Maestranza.model.Usuario;
import cl.duoc.maestranza.Maestranza.payload.request.LoginRequest;
import cl.duoc.maestranza.Maestranza.payload.request.SignupRequest;
import cl.duoc.maestranza.Maestranza.payload.response.JwtResponse;
import cl.duoc.maestranza.Maestranza.payload.response.MessageResponse;
import cl.duoc.maestranza.Maestranza.repository.RolRepository;
import cl.duoc.maestranza.Maestranza.repository.UsuarioRepository;
import cl.duoc.maestranza.Maestranza.security.jwt.JwtTokenProvider;
import cl.duoc.maestranza.Maestranza.security.services.TokenBlacklistService;
import cl.duoc.maestranza.Maestranza.security.services.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            logger.info("Intento de login para usuario: {}", loginRequest.getUsername());

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.info("Autenticación exitosa para usuario: {}", loginRequest.getUsername());

            String jwt = tokenProvider.generateToken(authentication);
            logger.info("Token JWT generado correctamente");

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            // Obtener el estado activo del usuario
            Usuario usuario = usuarioRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Error: Usuario no encontrado."));

            return ResponseEntity.ok(new JwtResponse(
                    jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    userDetails.getNombre(),
                    userDetails.getApellido(),
                    roles,
                    usuario.isActivo()
            ));
        } catch (BadCredentialsException e) {
            logger.warn("Credenciales inválidas para usuario: {}", loginRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Error: Credenciales inválidas"));
        } catch (Exception e) {
            logger.error("Error en la autenticación para usuario {}: {}", 
                        loginRequest.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error en la autenticación"));
        }
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        // Validar username y email
        if (usuarioRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: El nombre de usuario ya está en uso"));
        }

        if (usuarioRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: El email ya está en uso"));
        }

        // Crear nuevo usuario
        Usuario usuario = new Usuario();
        usuario.setUsername(signUpRequest.getUsername());
        usuario.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        usuario.setEmail(signUpRequest.getEmail());
        usuario.setNombre(signUpRequest.getNombre());
        usuario.setApellido(signUpRequest.getApellido());
        usuario.setActivo(true);

        Set<String> strRoles = signUpRequest.getRoles();
        Set<Rol> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            Rol trabajadorRole = rolRepository.findByNombre(TipoRol.ROLE_TRABAJADOR)
                    .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
            roles.add(trabajadorRole);
        } else {
            strRoles.forEach(role -> {
                switch (role.toLowerCase()) {
                    case "administrador":
                        Rol adminRole = rolRepository.findByNombre(TipoRol.ROLE_ADMINISTRADOR)
                                .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
                        roles.add(adminRole);
                        break;
                    case "inventario":
                        Rol invRole = rolRepository.findByNombre(TipoRol.ROLE_INVENTARIO)
                                .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
                        roles.add(invRole);
                        break;
                    case "compras":
                        Rol compRole = rolRepository.findByNombre(TipoRol.ROLE_COMPRAS)
                                .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
                        roles.add(compRole);
                        break;
                    case "logistica":
                        Rol logRole = rolRepository.findByNombre(TipoRol.ROLE_LOGISTICA)
                                .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
                        roles.add(logRole);
                        break;
                    case "produccion":
                        Rol prodRole = rolRepository.findByNombre(TipoRol.ROLE_PRODUCCION)
                                .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
                        roles.add(prodRole);
                        break;
                    case "auditor":
                        Rol audRole = rolRepository.findByNombre(TipoRol.ROLE_AUDITOR)
                                .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
                        roles.add(audRole);
                        break;
                    case "gerencia":
                        Rol gerRole = rolRepository.findByNombre(TipoRol.ROLE_GERENCIA)
                                .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
                        roles.add(gerRole);
                        break;
                    default:
                        Rol trabajadorRole = rolRepository.findByNombre(TipoRol.ROLE_TRABAJADOR)
                                .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
                        roles.add(trabajadorRole);
                }
            });
        }

        usuario.setRoles(roles);
        usuarioRepository.save(usuario);

        return ResponseEntity.ok(new MessageResponse("Usuario registrado exitosamente!"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            
            // Validar el header de autorización
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.warn("Token no proporcionado o formato incorrecto en logout");
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Token no proporcionado o formato incorrecto"));
            }

            String jwt = authHeader.substring(7);

            // Obtener la fecha de expiración del token antes de invalidarlo
            Instant expiration = tokenProvider.getExpirationFromJWT(jwt);
            if (expiration != null) {
                tokenBlacklistService.blacklistToken(jwt, expiration);
            } else {
                // Fallback: usar expiración por defecto
                tokenBlacklistService.blacklistToken(jwt);
            }

            logger.info("Usuario ha cerrado sesión, token invalidado");
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Sesión cerrada exitosamente");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error durante el proceso de logout: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error al cerrar sesión"));
        }
    }

    @GetMapping("/validate-token")
    public ResponseEntity<?> validateToken(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                
                // Verificar que no esté en blacklist
                if (tokenBlacklistService.isBlacklisted(token)) {
                    logger.warn("Intento de validar token en blacklist");
                    Map<String, String> response = new HashMap<>();
                    response.put("error", "Token invalidado");
                    return ResponseEntity.status(401).body(response);
                }
                
                // Verificar que el token sea válido
                if (tokenProvider.isTokenValid(token)) {
                    String username = tokenProvider.getUsernameFromJWT(token);
                    Map<String, String> response = new HashMap<>();
                    response.put("status", "valid");
                    response.put("username", username);
                    return ResponseEntity.ok(response);
                }
            }
            
            Map<String, String> response = new HashMap<>();
            response.put("error", "Token inválido");
            return ResponseEntity.status(401).body(response);
            
        } catch (Exception e) {
            logger.error("Error validando token: {}", e.getMessage());
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error interno del servidor");
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyToken(HttpServletRequest request, Authentication authentication) {
        try {
            // Si llegamos aquí, el token ya fue validado por Spring Security
            // El objeto Authentication contiene la información del usuario autenticado

            if (authentication == null || !authentication.isAuthenticated()) {
                logger.warn("Usuario no autenticado");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("valid", false, "message", "No autenticado"));
            }

            String username = authentication.getName();

            // Verificar que el usuario existe y está activo
            Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
            if (usuarioOpt.isEmpty()) {
                logger.warn("Usuario no encontrado: {}", username);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("valid", false, "message", "Usuario no encontrado"));
            }

            Usuario usuario = usuarioOpt.get();
            if (!usuario.isActivo()) {
                logger.warn("Usuario inactivo: {}", username);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("valid", false, "message", "Usuario inactivo"));
            }

            Map<String, Object> response = new HashMap<>();
            response.put("valid", true);
            response.put("username", username);
            response.put("userId", usuario.getId());
            response.put("email", usuario.getEmail());
            response.put("timestamp", Instant.now());

            logger.info("Token verificado exitosamente para usuario: {}", username);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error verificando token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid", false, "message", "Error interno"));
        }
    }
}