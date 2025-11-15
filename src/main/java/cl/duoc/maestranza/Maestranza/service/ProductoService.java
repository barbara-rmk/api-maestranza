package cl.duoc.maestranza.Maestranza.service;

import cl.duoc.maestranza.Maestranza.dto.ProductoDTO;
import cl.duoc.maestranza.Maestranza.model.Producto;
import cl.duoc.maestranza.Maestranza.model.Categoria;
import cl.duoc.maestranza.Maestranza.model.HistorialPrecios;
import cl.duoc.maestranza.Maestranza.model.Movimiento;
import cl.duoc.maestranza.Maestranza.model.Alerta;
import cl.duoc.maestranza.Maestranza.repository.ProductoRepository;
import cl.duoc.maestranza.Maestranza.repository.MovimientoRepository;
import cl.duoc.maestranza.Maestranza.repository.AlertaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private HistorialPreciosService historialPreciosService;

    @Autowired
    private MovimientoRepository movimientoRepository;

    @Autowired
    private AlertaRepository alertaRepository;

    // ========== MÉTODOS QUE RETORNAN DTOs ==========

    // Obtener todos los productos como DTOs
    public List<ProductoDTO> obtenerTodosDTO() {
        return productoRepository.findAll().stream()
                .map(ProductoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Obtener producto por ID como DTO
    public Optional<ProductoDTO> obtenerPorIdDTO(Long id) {
        return productoRepository.findById(id)
                .map(ProductoDTO::fromEntity);
    }

    // Obtener productos por categoría como DTOs
    public List<ProductoDTO> obtenerPorCategoriaDTO(Long categoriaId) {
        return productoRepository.findByCategoriaId(categoriaId).stream()
                .map(ProductoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Obtener productos con stock mayor a cierta cantidad como DTOs
    public List<ProductoDTO> obtenerConStockDTO(Integer stockMinimo) {
        return productoRepository.findByStockGreaterThan(stockMinimo).stream()
                .map(ProductoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Obtener productos agotados como DTOs
    public List<ProductoDTO> obtenerAgotadosDTO() {
        return productoRepository.findByStock(0).stream()
                .map(ProductoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Buscar productos por nombre o código como DTOs
    public List<ProductoDTO> buscarDTO(String termino) {
        return productoRepository.buscarPorNombreOCodigo(termino, termino).stream()
                .map(ProductoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Obtener productos con stock bajo como DTOs
    public List<ProductoDTO> obtenerConStockBajoDTO() {
        return productoRepository.findAll().stream()
                .filter(producto -> producto.getStock() <= producto.getUmbralStock())
                .map(ProductoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // ========== OPERACIONES CRUD CON DTOs ==========

    // Crear nuevo producto usando DTO
    @Transactional
    public ProductoDTO crearProducto(ProductoDTO productoDTO) {
        System.out.println("=== CREANDO PRODUCTO CON DTO ===");
        System.out.println("ProductoDTO recibido: " + productoDTO);

        // Validar que la categoría existe
        Categoria categoria = categoriaService.obtenerPorId(productoDTO.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + productoDTO.getCategoriaId()));

        // Validar que el código no exista
        if (productoRepository.findAll().stream()
                .anyMatch(p -> p.getCodigo().equals(productoDTO.getCodigo()))) {
            throw new RuntimeException("Ya existe un producto con el código: " + productoDTO.getCodigo());
        }

        // Convertir DTO a entidad y configurar campos
        Producto producto = productoDTO.toEntity();
        producto.setCategoria(categoria);
        producto.setUbicacion(productoDTO.getUbicacion() != null ? productoDTO.getUbicacion() : "Almacén Principal");
        producto.setUmbralStock(productoDTO.getUmbralStock() != null ? productoDTO.getUmbralStock() : 5);
        producto.setFechaIngreso(LocalDateTime.now());
        producto.setActivo(true);

        // Guardar el producto primero
        Producto productoGuardado = productoRepository.save(producto);
        System.out.println("Producto guardado: " + productoGuardado);

        // Crear historial de precio inicial
        if (productoDTO.getPrecio() != null && productoDTO.getPrecio() > 0) {
            HistorialPrecios precioInicial = historialPreciosService.crearPrecioInicial(productoGuardado, productoDTO.getPrecio());
            System.out.println("Precio inicial creado: " + precioInicial);
        }

        // Recargar el producto con el historial y convertir a DTO
        return ProductoDTO.fromEntity(
                productoRepository.findById(productoGuardado.getId()).orElse(productoGuardado)
        );
    }

    // Actualizar producto usando DTO
    @Transactional
    public ProductoDTO actualizarProducto(Long id, ProductoDTO productoDTO) {
        System.out.println("=== ACTUALIZANDO PRODUCTO CON DTO ===");
        System.out.println("ID: " + id + ", ProductoDTO: " + productoDTO);

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));

        // Actualizar campos básicos
        if (productoDTO.getCodigo() != null) {
            producto.setCodigo(productoDTO.getCodigo());
        }
        if (productoDTO.getNombre() != null) {
            producto.setNombre(productoDTO.getNombre());
        }
        if (productoDTO.getDescripcion() != null) {
            producto.setDescripcion(productoDTO.getDescripcion());
        }
        if (productoDTO.getStock() != null) {
            producto.setStock(productoDTO.getStock());
        }
        if (productoDTO.getImageUrl() != null) {
            producto.setImageUrl(productoDTO.getImageUrl());
        }
        if (productoDTO.getUbicacion() != null) {
            producto.setUbicacion(productoDTO.getUbicacion());
        }
        if (productoDTO.getUmbralStock() != null) {
            producto.setUmbralStock(productoDTO.getUmbralStock());
        }
        if (productoDTO.getActivo() != null) {
            producto.setActivo(productoDTO.getActivo());
        }

        // Actualizar categoría si se proporciona
        if (productoDTO.getCategoriaId() != null) {
            Categoria categoria = categoriaService.obtenerPorId(productoDTO.getCategoriaId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + productoDTO.getCategoriaId()));
            producto.setCategoria(categoria);
        }

        // Actualizar precio si es diferente al actual
        if (productoDTO.getPrecio() != null && productoDTO.getPrecio() > 0) {
            Double precioActual = historialPreciosService.obtenerPrecioActual(id);
            if (!productoDTO.getPrecio().equals(precioActual)) {
                historialPreciosService.agregarPrecio(id, productoDTO.getPrecio());
                System.out.println("Precio actualizado de " + precioActual + " a " + productoDTO.getPrecio());
            }
        }

        // Guardar cambios
        Producto productoActualizado = productoRepository.save(producto);
        System.out.println("Producto actualizado: " + productoActualizado);

        return ProductoDTO.fromEntity(productoActualizado);
    }

    // Eliminar producto
    @Transactional
    public void eliminarProducto(Long id) {
        System.out.println("=== ELIMINANDO PRODUCTO CON ID: " + id + " ===");
        
        // Verificar si el producto existe
        if (!productoRepository.existsById(id)) {
            throw new RuntimeException("Producto no encontrado con ID: " + id);
        }

        try {
            // 1. Eliminar movimientos asociados
            List<Movimiento> movimientos = movimientoRepository.findByProductoIdOrderByFechaDesc(id);
            if (!movimientos.isEmpty()) {
                System.out.println("Eliminando " + movimientos.size() + " movimientos asociados");
                movimientoRepository.deleteAll(movimientos);
            }

            // 2. Eliminar alertas asociadas
            List<Alerta> alertas = alertaRepository.findByProductoIdOrderByFechaDesc(id);
            if (!alertas.isEmpty()) {
                System.out.println("Eliminando " + alertas.size() + " alertas asociadas");
                alertaRepository.deleteAll(alertas);
            }

            // 3. Eliminar historial de precios (aunque tiene cascade, lo hacemos explícito)
            historialPreciosService.eliminarHistorialPorProducto(id);
            System.out.println("Historial de precios eliminado");

            // 4. Finalmente eliminar el producto
            productoRepository.deleteById(id);
            System.out.println("Producto eliminado exitosamente");

        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar el producto: " + e.getMessage());
        }
    }

    // Desactivar producto usando DTO
    public ProductoDTO desactivarProducto(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));

        producto.setActivo(false);
        Producto productoDesactivado = productoRepository.save(producto);
        return ProductoDTO.fromEntity(productoDesactivado);
    }

    // Reactivar producto usando DTO
    public ProductoDTO reactivarProducto(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));

        producto.setActivo(true);
        Producto productoReactivado = productoRepository.save(producto);
        return ProductoDTO.fromEntity(productoReactivado);
    }

    // Actualizar stock usando DTO
    public ProductoDTO actualizarStock(Long id, Integer nuevoStock) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));

        producto.setStock(nuevoStock);
        Producto productoActualizado = productoRepository.save(producto);
        return ProductoDTO.fromEntity(productoActualizado);
    }

    // ========== MÉTODOS DE COMPATIBILIDAD (Retornan entidades) ==========

    // Obtener todos los productos (entidades) - para compatibilidad
    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    // Obtener producto por ID (entidad) - para compatibilidad
    public Optional<Producto> obtenerPorId(Long id) {
        return productoRepository.findById(id);
    }

    // Obtener productos por categoría (entidades) - para compatibilidad
    public List<Producto> obtenerPorCategoria(Long categoriaId) {
        return productoRepository.findByCategoriaId(categoriaId);
    }

    // Obtener productos con stock mayor a cierta cantidad (entidades)
    public List<Producto> obtenerConStock(Integer stockMinimo) {
        return productoRepository.findByStockGreaterThan(stockMinimo);
    }

    // Obtener productos agotados (entidades)
    public List<Producto> obtenerAgotados() {
        return productoRepository.findByStock(0);
    }

    // Buscar productos por nombre o código (entidades)
    public List<Producto> buscar(String termino) {
        return productoRepository.buscarPorNombreOCodigo(termino, termino);
    }

    // Verificar si un producto está en stock bajo
    public boolean estaEnStockBajo(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));

        return producto.getStock() <= producto.getUmbralStock();
    }

    // Obtener productos con stock bajo (entidades)
    public List<Producto> obtenerConStockBajo() {
        return productoRepository.findAll().stream()
                .filter(producto -> producto.getStock() <= producto.getUmbralStock())
                .toList();
    }

    // Desactivar producto (entidad) - para compatibilidad
    public Producto desactivar(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));

        producto.setActivo(false);
        return productoRepository.save(producto);
    }

    // Reactivar producto (entidad) - para compatibilidad
    public Producto reactivar(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));

        producto.setActivo(true);
        return productoRepository.save(producto);
    }
}