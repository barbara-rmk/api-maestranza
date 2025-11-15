package cl.duoc.maestranza.Maestranza.controller;

import cl.duoc.maestranza.Maestranza.dto.RolDTO;
import cl.duoc.maestranza.Maestranza.model.Rol;
import cl.duoc.maestranza.Maestranza.model.TipoRol;
import cl.duoc.maestranza.Maestranza.service.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@CrossOrigin(origins = "*")
public class RolController {

    @Autowired
    private RolService rolService;

    @GetMapping
    public ResponseEntity<List<RolDTO>> obtenerTodos() {
        List<RolDTO> roles = rolService.obtenerTodos();
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RolDTO> obtenerPorId(@PathVariable Integer id) {
        return rolService.obtenerPorId(id)
                .map(rol -> new ResponseEntity<>(rol, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/nombre/{nombre}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<RolDTO> obtenerPorNombre(@PathVariable TipoRol nombre) {
        return rolService.obtenerPorNombre(nombre)
                .map(rol -> new ResponseEntity<>(rol, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<RolDTO> crear(@RequestBody Rol rol) {
        if (rolService.existeRolPorNombre(rol.getNombre())) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        RolDTO nuevoRol = rolService.crear(rol);
        return new ResponseEntity<>(nuevoRol, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<RolDTO> actualizar(@PathVariable Integer id, @RequestBody Rol rol) {
        return rolService.actualizar(id, rol)
                .map(rolActualizado -> new ResponseEntity<>(rolActualizado, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (rolService.eliminar(id)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}