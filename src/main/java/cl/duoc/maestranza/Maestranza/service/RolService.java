package cl.duoc.maestranza.Maestranza.service;

import cl.duoc.maestranza.Maestranza.dto.RolDTO;
import cl.duoc.maestranza.Maestranza.model.Rol;
import cl.duoc.maestranza.Maestranza.model.TipoRol;
import cl.duoc.maestranza.Maestranza.repository.RolRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RolService {

    private static final Logger logger = LoggerFactory.getLogger(RolService.class);

    @Autowired
    private RolRepository rolRepository;

    /**
     * Convierte un Rol a RolDTO
     */
    private RolDTO convertToDTO(Rol rol) {
        RolDTO dto = new RolDTO();
        dto.setId(rol.getId());
        dto.setNombre(rol.getNombre());
        return dto;
    }

    /**
     * Obtener todos los roles
     */
    public List<RolDTO> obtenerTodos() {
        logger.info("Obteniendo todos los roles");
        return rolRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener rol por ID
     */
    public Optional<RolDTO> obtenerPorId(Integer id) {
        logger.info("Buscando rol con ID: {}", id);
        return rolRepository.findById(id)
                .map(this::convertToDTO);
    }

    /**
     * Obtener rol por nombre
     */
    public Optional<RolDTO> obtenerPorNombre(TipoRol nombre) {
        logger.info("Buscando rol con nombre: {}", nombre);
        return rolRepository.findByNombre(nombre)
                .map(this::convertToDTO);
    }

    /**
     * Crear un nuevo rol
     */
    public RolDTO crear(Rol rol) {
        logger.info("Creando nuevo rol: {}", rol);
        return convertToDTO(rolRepository.save(rol));
    }

    /**
     * Actualizar un rol existente
     */
    public Optional<RolDTO> actualizar(Integer id, Rol rolActualizado) {
        logger.info("Actualizando rol con ID {}: {}", id, rolActualizado);
        return rolRepository.findById(id)
                .map(rolExistente -> {
                    rolExistente.setNombre(rolActualizado.getNombre());
                    return convertToDTO(rolRepository.save(rolExistente));
                });
    }

    /**
     * Eliminar un rol
     */
    public boolean eliminar(Integer id) {
        logger.info("Eliminando rol con ID: {}", id);
        if (rolRepository.existsById(id)) {
            rolRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Comprobar si existe un rol por nombre
     */
    public boolean existeRolPorNombre(TipoRol nombre) {
        logger.info("Verificando existencia de rol con nombre: {}", nombre);
        return rolRepository.existsByNombre(nombre);
    }
}