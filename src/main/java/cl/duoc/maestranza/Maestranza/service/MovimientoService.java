package cl.duoc.maestranza.Maestranza.service;

import cl.duoc.maestranza.Maestranza.dto.MovimientoDTO;
import cl.duoc.maestranza.Maestranza.model.Movimiento;
import cl.duoc.maestranza.Maestranza.model.Producto;
import cl.duoc.maestranza.Maestranza.model.Usuario;
import cl.duoc.maestranza.Maestranza.model.TipoMovimiento;
import cl.duoc.maestranza.Maestranza.repository.MovimientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MovimientoService {

    @Autowired
    private MovimientoRepository movimientoRepository;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private UsuarioService usuarioService;

    // ========== MÉTODOS QUE RETORNAN DTOs ==========

    // Obtener todos los movimientos como DTOs
    public List<MovimientoDTO> obtenerTodosDTO() {
        return movimientoRepository.findAll().stream()
                .map(MovimientoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Obtener movimiento por ID como DTO
    public Optional<MovimientoDTO> obtenerPorIdDTO(Long id) {
        return movimientoRepository.findById(id)
                .map(MovimientoDTO::fromEntity);
    }

    // Obtener movimientos por producto como DTOs
    public List<MovimientoDTO> obtenerPorProductoDTO(Long productoId) {
        return movimientoRepository.findByProductoIdOrderByFechaDesc(productoId).stream()
                .map(MovimientoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Obtener movimientos por usuario como DTOs
    public List<MovimientoDTO> obtenerPorUsuarioDTO(Long usuarioId) {
        return movimientoRepository.findByUsuarioIdOrderByFechaDesc(usuarioId).stream()
                .map(MovimientoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Obtener movimientos por tipo como DTOs
    public List<MovimientoDTO> obtenerPorTipoDTO(TipoMovimiento tipo) {
        return movimientoRepository.findByTipoOrderByFechaDesc(tipo).stream()
                .map(MovimientoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Obtener movimientos por producto y tipo como DTOs
    public List<MovimientoDTO> obtenerPorProductoYTipoDTO(Long productoId, TipoMovimiento tipo) {
        return movimientoRepository.findByProductoIdAndTipoOrderByFechaDesc(productoId, tipo).stream()
                .map(MovimientoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Obtener movimientos por usuario y tipo como DTOs
    public List<MovimientoDTO> obtenerPorUsuarioYTipoDTO(Long usuarioId, TipoMovimiento tipo) {
        return movimientoRepository.findByUsuarioIdAndTipoOrderByFechaDesc(usuarioId, tipo).stream()
                .map(MovimientoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Obtener movimientos por rango de fechas como DTOs
    public List<MovimientoDTO> obtenerPorRangoFechasDTO(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return movimientoRepository.findByFechaBetweenOrderByFechaDesc(fechaInicio, fechaFin).stream()
                .map(MovimientoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Obtener movimientos por producto y rango de fechas como DTOs
    public List<MovimientoDTO> obtenerPorProductoYRangoFechasDTO(Long productoId, LocalDateTime fechaInicio,
            LocalDateTime fechaFin) {
        return movimientoRepository.findByProductoIdAndFechaBetweenOrderByFechaDesc(productoId, fechaInicio, fechaFin)
                .stream()
                .map(MovimientoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Obtener movimientos por usuario y rango de fechas como DTOs
    public List<MovimientoDTO> obtenerPorUsuarioYRangoFechasDTO(Long usuarioId, LocalDateTime fechaInicio,
            LocalDateTime fechaFin) {
        return movimientoRepository.findByUsuarioIdAndFechaBetweenOrderByFechaDesc(usuarioId, fechaInicio, fechaFin)
                .stream()
                .map(MovimientoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Obtener movimientos por tipo y rango de fechas como DTOs
    public List<MovimientoDTO> obtenerPorTipoYRangoFechasDTO(TipoMovimiento tipo, LocalDateTime fechaInicio,
            LocalDateTime fechaFin) {
        return movimientoRepository.findByTipoAndFechaBetweenOrderByFechaDesc(tipo, fechaInicio, fechaFin).stream()
                .map(MovimientoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Buscar movimientos por código de producto como DTOs
    public List<MovimientoDTO> buscarPorCodigoProductoDTO(String productoCodigo) {
        return movimientoRepository.findByProductoCodigoOrderByFechaDesc(productoCodigo).stream()
                .map(MovimientoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Buscar movimientos por nombre de producto como DTOs
    public List<MovimientoDTO> buscarPorNombreProductoDTO(String productoNombre) {
        return movimientoRepository.findByProductoNombreContainingIgnoreCaseOrderByFechaDesc(productoNombre).stream()
                .map(MovimientoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Buscar movimientos con filtros múltiples como DTOs
    public List<MovimientoDTO> buscarConFiltrosDTO(Long productoId, Long usuarioId, TipoMovimiento tipo,
            LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return movimientoRepository.findMovimientosWithFilters(productoId, usuarioId, tipo, fechaInicio, fechaFin)
                .stream()
                .map(MovimientoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // ========== OPERACIONES CRUD CON DTOs ==========

    // Crear nuevo movimiento usando DTO
    @Transactional
    public MovimientoDTO crearMovimiento(MovimientoDTO movimientoDTO) {
        System.out.println("=== CREANDO MOVIMIENTO CON DTO ===");
        System.out.println("MovimientoDTO recibido: " + movimientoDTO);
        System.out.println("ImagePath del DTO: " + movimientoDTO.getImagePath());

        // Validar que el usuario existe
        Usuario usuario = usuarioService.obtenerPorIdEntity(movimientoDTO.getUsuarioId())
                .orElseThrow(
                        () -> new RuntimeException("Usuario no encontrado con ID: " + movimientoDTO.getUsuarioId()));

        // Validar que el producto existe
        Producto producto = productoService.obtenerPorId(movimientoDTO.getProductoId())
                .orElseThrow(
                        () -> new RuntimeException("Producto no encontrado con ID: " + movimientoDTO.getProductoId()));

        System.out.println("ImageUrl del producto: " + producto.getImageUrl());

        // Validar cantidad
        if (movimientoDTO.getCantidad() == null || movimientoDTO.getCantidad() <= 0) {
            throw new RuntimeException("La cantidad debe ser mayor a 0");
        }

        // Validar stock para salidas
        if (movimientoDTO.getTipo() == TipoMovimiento.SALIDA) {
            if (producto.getStock() < movimientoDTO.getCantidad()) {
                throw new RuntimeException("Stock insuficiente. Stock actual: " + producto.getStock() +
                        ", Cantidad solicitada: " + movimientoDTO.getCantidad());
            }
        }

        // Convertir DTO a entidad y configurar campos
        Movimiento movimiento = movimientoDTO.toEntity();
        movimiento.setUsuario(usuario);
        movimiento.setProducto(producto);
        movimiento.setProductoCodigo(producto.getCodigo());
        movimiento.setProductoNombre(producto.getNombre());

        // ✅ PRESERVAR imagePath del DTO (Cloudinary) o usar fallback del producto
        if (movimientoDTO.getImagePath() == null || movimientoDTO.getImagePath().trim().isEmpty()) {
            movimiento.setImagePath(producto.getImageUrl()); // Fallback a imagen del producto
            System.out.println("Usando imagen del producto como fallback: " + producto.getImageUrl());
        } else {
            movimiento.setImagePath(movimientoDTO.getImagePath()); // Usar imagen de Cloudinary
            System.out.println("Usando imagen de Cloudinary: " + movimientoDTO.getImagePath());
        }

        movimiento.setFecha(LocalDateTime.now());

        System.out.println("ImagePath final asignado: " + movimiento.getImagePath());

        // Guardar el movimiento
        Movimiento movimientoGuardado = movimientoRepository.save(movimiento);
        System.out.println("Movimiento guardado: " + movimientoGuardado);

        // Actualizar stock del producto
        actualizarStockProducto(producto, movimientoDTO.getCantidad(), movimientoDTO.getTipo());

        // Recargar el movimiento y convertir a DTO
        return MovimientoDTO.fromEntity(
                movimientoRepository.findById(movimientoGuardado.getId()).orElse(movimientoGuardado));
    }

    // Actualizar movimiento usando DTO
    @Transactional
    public MovimientoDTO actualizarMovimiento(Long id, MovimientoDTO movimientoDTO) {
        System.out.println("=== ACTUALIZANDO MOVIMIENTO CON DTO ===");
        System.out.println("ID: " + id + ", MovimientoDTO: " + movimientoDTO);

        Movimiento movimiento = movimientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado con ID: " + id));

        // Validar que el usuario existe si se proporciona
        if (movimientoDTO.getUsuarioId() != null) {
            Usuario usuario = usuarioService.obtenerPorIdEntity(movimientoDTO.getUsuarioId())
                    .orElseThrow(() -> new RuntimeException(
                            "Usuario no encontrado con ID: " + movimientoDTO.getUsuarioId()));
            movimiento.setUsuario(usuario);
        }

        // Validar que el producto existe si se proporciona
        if (movimientoDTO.getProductoId() != null) {
            Producto producto = productoService.obtenerPorId(movimientoDTO.getProductoId())
                    .orElseThrow(() -> new RuntimeException(
                            "Producto no encontrado con ID: " + movimientoDTO.getProductoId()));
            movimiento.setProducto(producto);
            movimiento.setProductoCodigo(producto.getCodigo());
            movimiento.setProductoNombre(producto.getNombre());

            // ✅ PRESERVAR imagePath del DTO o usar fallback del producto
            if (movimientoDTO.getImagePath() == null || movimientoDTO.getImagePath().trim().isEmpty()) {
                movimiento.setImagePath(producto.getImageUrl());
            } else {
                movimiento.setImagePath(movimientoDTO.getImagePath());
            }
        }

        // Actualizar imagePath si se proporciona explícitamente
        if (movimientoDTO.getImagePath() != null) {
            movimiento.setImagePath(movimientoDTO.getImagePath());
        }

        // Actualizar campos básicos
        if (movimientoDTO.getCantidad() != null) {
            // Revertir el stock anterior
            revertirStockProducto(movimiento.getProducto(), movimiento.getCantidad(), movimiento.getTipo());

            // Validar nueva cantidad para salidas
            if (movimientoDTO.getTipo() == TipoMovimiento.SALIDA) {
                if (movimiento.getProducto().getStock() < movimientoDTO.getCantidad()) {
                    throw new RuntimeException("Stock insuficiente para la nueva cantidad");
                }
            }

            movimiento.setCantidad(movimientoDTO.getCantidad());
            // Aplicar nueva cantidad
            actualizarStockProducto(movimiento.getProducto(), movimientoDTO.getCantidad(), movimientoDTO.getTipo());
        }

        if (movimientoDTO.getTipo() != null) {
            // Revertir el stock anterior
            revertirStockProducto(movimiento.getProducto(), movimiento.getCantidad(), movimiento.getTipo());
            movimiento.setTipo(movimientoDTO.getTipo());
            // Aplicar nuevo tipo
            actualizarStockProducto(movimiento.getProducto(), movimiento.getCantidad(), movimientoDTO.getTipo());
        }

        if (movimientoDTO.getDescripcion() != null) {
            movimiento.setDescripcion(movimientoDTO.getDescripcion());
        }

        if (movimientoDTO.getFecha() != null) {
            movimiento.setFecha(movimientoDTO.getFecha());
        }

        // Guardar cambios
        Movimiento movimientoActualizado = movimientoRepository.save(movimiento);
        System.out.println("Movimiento actualizado: " + movimientoActualizado);

        return MovimientoDTO.fromEntity(movimientoActualizado);
    }

    // Eliminar movimiento
    @Transactional
    public void eliminarMovimiento(Long id) {
        Movimiento movimiento = movimientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado con ID: " + id));

        // Revertir el stock antes de eliminar
        revertirStockProducto(movimiento.getProducto(), movimiento.getCantidad(), movimiento.getTipo());

        movimientoRepository.deleteById(id);
        System.out.println("Movimiento eliminado con ID: " + id);
    }

    // ========== MÉTODOS DE ENTIDADES ==========

    // Obtener todos los movimientos
    public List<Movimiento> obtenerTodos() {
        return movimientoRepository.findAll();
    }

    // Obtener movimiento por ID
    public Optional<Movimiento> obtenerPorId(Long id) {
        return movimientoRepository.findById(id);
    }

    // Obtener movimientos por producto
    public List<Movimiento> obtenerPorProducto(Long productoId) {
        return movimientoRepository.findByProductoIdOrderByFechaDesc(productoId);
    }

    // Obtener movimientos por usuario
    public List<Movimiento> obtenerPorUsuario(Long usuarioId) {
        return movimientoRepository.findByUsuarioIdOrderByFechaDesc(usuarioId);
    }

    // Obtener movimientos por tipo
    public List<Movimiento> obtenerPorTipo(TipoMovimiento tipo) {
        return movimientoRepository.findByTipoOrderByFechaDesc(tipo);
    }

    // ========== MÉTODOS DE ESTADÍSTICAS ==========

    // Contar movimientos por tipo
    public long contarPorTipo(TipoMovimiento tipo) {
        return movimientoRepository.countByTipo(tipo);
    }

    // Contar movimientos por producto
    public long contarPorProducto(Long productoId) {
        return movimientoRepository.countByProductoId(productoId);
    }

    // Contar movimientos por usuario
    public long contarPorUsuario(Long usuarioId) {
        return movimientoRepository.countByUsuarioId(usuarioId);
    }

    // Contar movimientos por producto y tipo
    public long contarPorProductoYTipo(Long productoId, TipoMovimiento tipo) {
        return movimientoRepository.countByProductoIdAndTipo(productoId, tipo);
    }

    // ========== MÉTODOS PRIVADOS ==========

    // Actualizar stock del producto según el tipo de movimiento
    private void actualizarStockProducto(Producto producto, Integer cantidad, TipoMovimiento tipo) {
        if (tipo == TipoMovimiento.ENTRADA) {
            producto.setStock(producto.getStock() + cantidad);
        } else if (tipo == TipoMovimiento.SALIDA) {
            producto.setStock(producto.getStock() - cantidad);
        }
        productoService.actualizarStock(producto.getId(), producto.getStock());
    }

    // Revertir stock del producto (para actualizaciones y eliminaciones)
    private void revertirStockProducto(Producto producto, Integer cantidad, TipoMovimiento tipo) {
        if (tipo == TipoMovimiento.ENTRADA) {
            producto.setStock(producto.getStock() - cantidad);
        } else if (tipo == TipoMovimiento.SALIDA) {
            producto.setStock(producto.getStock() + cantidad);
        }
        productoService.actualizarStock(producto.getId(), producto.getStock());
    }
}