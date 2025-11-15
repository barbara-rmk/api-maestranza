package cl.duoc.maestranza.Maestranza.service;

import cl.duoc.maestranza.Maestranza.model.Categoria;
import cl.duoc.maestranza.Maestranza.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    // Obtener todas las categorías
    public List<Categoria> obtenerTodas() {
        return categoriaRepository.findAll();
    }

    // Obtener categoría por ID
    public Optional<Categoria> obtenerPorId(Long id) {
        return categoriaRepository.findById(id);
    }

    // Obtener categoría por nombre
    public Optional<Categoria> obtenerPorNombre(String nombre) {
        return categoriaRepository.findByNombre(nombre);
    }

    // Crear nueva categoría
    public Categoria crear(Categoria categoria) {
        // Validar que el nombre no exista
        if (categoriaRepository.findByNombre(categoria.getNombre()).isPresent()) {
            throw new RuntimeException("Ya existe una categoría con el nombre: " + categoria.getNombre());
        }
        return categoriaRepository.save(categoria);
    }

    // Actualizar categoría existente
    public Categoria actualizar(Long id, Categoria categoriaActualizada) {
        return categoriaRepository.findById(id)
                .map(categoria -> {
                    categoria.setNombre(categoriaActualizada.getNombre());
                    categoria.setDescripcion(categoriaActualizada.getDescripcion());
                    return categoriaRepository.save(categoria);
                })
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id));
    }

    // Eliminar categoría
    public void eliminar(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new RuntimeException("Categoría no encontrada con ID: " + id);
        }
        categoriaRepository.deleteById(id);
    }

    // Verificar si una categoría existe
    public boolean existe(Long id) {
        return categoriaRepository.existsById(id);
    }
}