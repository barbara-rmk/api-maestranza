package cl.duoc.maestranza.Maestranza.controller;

import cl.duoc.maestranza.Maestranza.dto.ProductoDTO;
import cl.duoc.maestranza.Maestranza.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    // Obtener todos los productos
    @GetMapping
    public ResponseEntity<List<ProductoDTO>> obtenerTodos() {
        List<ProductoDTO> productos = productoService.obtenerTodosDTO();
        return ResponseEntity.ok(productos);
    }

    // Obtener producto por ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> obtenerPorId(@PathVariable Long id) {
        Optional<ProductoDTO> producto = productoService.obtenerPorIdDTO(id);
        return producto.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Obtener productos por categoría
    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<ProductoDTO>> obtenerPorCategoria(@PathVariable Long categoriaId) {
        List<ProductoDTO> productos = productoService.obtenerPorCategoriaDTO(categoriaId);
        return ResponseEntity.ok(productos);
    }

    // Obtener productos con stock mayor a cierta cantidad
    @GetMapping("/stock/{stockMinimo}")
    public ResponseEntity<List<ProductoDTO>> obtenerConStock(@PathVariable Integer stockMinimo) {
        List<ProductoDTO> productos = productoService.obtenerConStockDTO(stockMinimo);
        return ResponseEntity.ok(productos);
    }

    // Obtener productos agotados
    @GetMapping("/agotados")
    public ResponseEntity<List<ProductoDTO>> obtenerAgotados() {
        List<ProductoDTO> productos = productoService.obtenerAgotadosDTO();
        return ResponseEntity.ok(productos);
    }

    // Obtener productos con stock bajo
    @GetMapping("/stock-bajo")
    public ResponseEntity<List<ProductoDTO>> obtenerConStockBajo() {
        List<ProductoDTO> productos = productoService.obtenerConStockBajoDTO();
        return ResponseEntity.ok(productos);
    }

    // Buscar productos por nombre o código
    @GetMapping("/buscar")
    public ResponseEntity<List<ProductoDTO>> buscar(@RequestParam String termino) {
        List<ProductoDTO> productos = productoService.buscarDTO(termino);
        return ResponseEntity.ok(productos);
    }

    // Crear un nuevo producto
    @PostMapping
    public ResponseEntity<ProductoDTO> crear(@RequestBody ProductoDTO productoDTO) {
        ProductoDTO nuevoProducto = productoService.crearProducto(productoDTO);
        return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED);
    }

    // Actualizar un producto existente
    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> actualizar(@PathVariable Long id, @RequestBody ProductoDTO productoDTO) {
        try {
            ProductoDTO productoActualizado = productoService.actualizarProducto(id, productoDTO);
            return ResponseEntity.ok(productoActualizado);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar un producto
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable Long id) {
        try {
            productoService.eliminarProducto(id);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Producto eliminado exitosamente");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Desactivar un producto
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<ProductoDTO> desactivar(@PathVariable Long id) {
        try {
            ProductoDTO producto = productoService.desactivarProducto(id);
            return ResponseEntity.ok(producto);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Reactivar un producto
    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<ProductoDTO> reactivar(@PathVariable Long id) {
        try {
            ProductoDTO producto = productoService.reactivarProducto(id);
            return ResponseEntity.ok(producto);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Actualizar stock de un producto
    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductoDTO> actualizarStock(@PathVariable Long id, @RequestParam Integer nuevoStock) {
        try {
            ProductoDTO producto = productoService.actualizarStock(id, nuevoStock);
            return ResponseEntity.ok(producto);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}