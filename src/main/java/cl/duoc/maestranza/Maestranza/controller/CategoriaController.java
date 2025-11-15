package cl.duoc.maestranza.Maestranza.controller;

import cl.duoc.maestranza.Maestranza.dto.CategoriaDTO;
import cl.duoc.maestranza.Maestranza.model.Categoria;
import cl.duoc.maestranza.Maestranza.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    // Obtener todas las categorías
    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> obtenerTodas() {
        List<Categoria> categorias = categoriaService.obtenerTodas();
        List<CategoriaDTO> categoriasDTO = categorias.stream()
                .map(CategoriaDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categoriasDTO);
    }

    // Obtener categoría por ID
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaDTO> obtenerPorId(@PathVariable Long id) {
        Optional<Categoria> categoria = categoriaService.obtenerPorId(id);
        return categoria.map(c -> ResponseEntity.ok(CategoriaDTO.fromEntity(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Obtener categoría por nombre
    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<CategoriaDTO> obtenerPorNombre(@PathVariable String nombre) {
        Optional<Categoria> categoria = categoriaService.obtenerPorNombre(nombre);
        return categoria.map(c -> ResponseEntity.ok(CategoriaDTO.fromEntity(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear nueva categoría
    @PostMapping
    public ResponseEntity<CategoriaDTO> crear(@RequestBody CategoriaDTO categoriaDTO) {
        try {
            Categoria categoria = categoriaDTO.toEntity();
            Categoria nuevaCategoria = categoriaService.crear(categoria);
            return new ResponseEntity<>(CategoriaDTO.fromEntity(nuevaCategoria), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Actualizar categoría existente
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaDTO> actualizar(@PathVariable Long id, @RequestBody CategoriaDTO categoriaDTO) {
        try {
            Categoria categoria = categoriaDTO.toEntity();
            Categoria categoriaActualizada = categoriaService.actualizar(id, categoria);
            return ResponseEntity.ok(CategoriaDTO.fromEntity(categoriaActualizada));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar categoría
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            categoriaService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Verificar si una categoría existe
    @GetMapping("/{id}/existe")
    public ResponseEntity<Boolean> existe(@PathVariable Long id) {
        boolean existe = categoriaService.existe(id);
        return ResponseEntity.ok(existe);
    }
}