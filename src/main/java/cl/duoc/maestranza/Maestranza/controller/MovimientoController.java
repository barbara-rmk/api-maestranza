package cl.duoc.maestranza.Maestranza.controller;

import cl.duoc.maestranza.Maestranza.dto.MovimientoDTO;
import cl.duoc.maestranza.Maestranza.model.TipoMovimiento;
import cl.duoc.maestranza.Maestranza.service.MovimientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/movimientos")
public class MovimientoController {

    @Autowired
    private MovimientoService movimientoService;

    // ========== ENDPOINTS PRINCIPALES ==========

    // Obtener todos los movimientos
    @GetMapping
    public ResponseEntity<List<MovimientoDTO>> obtenerTodos() {
        List<MovimientoDTO> movimientos = movimientoService.obtenerTodosDTO();
        return ResponseEntity.ok(movimientos);
    }

    // Obtener movimiento por ID
    @GetMapping("/{id}")
    public ResponseEntity<MovimientoDTO> obtenerPorId(@PathVariable Long id) {
        Optional<MovimientoDTO> movimiento = movimientoService.obtenerPorIdDTO(id);
        return movimiento.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<MovimientoDTO> crear(@RequestBody MovimientoDTO movimientoDTO) {
        try {
            MovimientoDTO nuevoMovimiento = movimientoService.crearMovimiento(movimientoDTO);
            return new ResponseEntity<>(nuevoMovimiento, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            e.printStackTrace(); // <--- Esto imprime el error en la consola
            return ResponseEntity.badRequest().build();
        }
    }

    // Actualizar un movimiento existente
    @PutMapping("/{id}")
    public ResponseEntity<MovimientoDTO> actualizar(@PathVariable Long id, @RequestBody MovimientoDTO movimientoDTO) {
        try {
            MovimientoDTO movimientoActualizado = movimientoService.actualizarMovimiento(id, movimientoDTO);
            return ResponseEntity.ok(movimientoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Eliminar un movimiento
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            movimientoService.eliminarMovimiento(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ========== ENDPOINTS DE FILTRADO ==========

    // Obtener movimientos por producto
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<MovimientoDTO>> obtenerPorProducto(@PathVariable Long productoId) {
        List<MovimientoDTO> movimientos = movimientoService.obtenerPorProductoDTO(productoId);
        return ResponseEntity.ok(movimientos);
    }

    // Obtener movimientos por usuario
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<MovimientoDTO>> obtenerPorUsuario(@PathVariable Long usuarioId) {
        List<MovimientoDTO> movimientos = movimientoService.obtenerPorUsuarioDTO(usuarioId);
        return ResponseEntity.ok(movimientos);
    }

    // Obtener movimientos por tipo
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<MovimientoDTO>> obtenerPorTipo(@PathVariable TipoMovimiento tipo) {
        List<MovimientoDTO> movimientos = movimientoService.obtenerPorTipoDTO(tipo);
        return ResponseEntity.ok(movimientos);
    }

    // Obtener movimientos por producto y tipo
    @GetMapping("/producto/{productoId}/tipo/{tipo}")
    public ResponseEntity<List<MovimientoDTO>> obtenerPorProductoYTipo(
            @PathVariable Long productoId, 
            @PathVariable TipoMovimiento tipo) {
        List<MovimientoDTO> movimientos = movimientoService.obtenerPorProductoYTipoDTO(productoId, tipo);
        return ResponseEntity.ok(movimientos);
    }

    // Obtener movimientos por usuario y tipo
    @GetMapping("/usuario/{usuarioId}/tipo/{tipo}")
    public ResponseEntity<List<MovimientoDTO>> obtenerPorUsuarioYTipo(
            @PathVariable Long usuarioId, 
            @PathVariable TipoMovimiento tipo) {
        List<MovimientoDTO> movimientos = movimientoService.obtenerPorUsuarioYTipoDTO(usuarioId, tipo);
        return ResponseEntity.ok(movimientos);
    }

    // ========== ENDPOINTS DE BÚSQUEDA ==========

    // Buscar movimientos por código de producto
    @GetMapping("/buscar/codigo")
    public ResponseEntity<List<MovimientoDTO>> buscarPorCodigoProducto(@RequestParam String productoCodigo) {
        List<MovimientoDTO> movimientos = movimientoService.buscarPorCodigoProductoDTO(productoCodigo);
        return ResponseEntity.ok(movimientos);
    }

    // Buscar movimientos por nombre de producto
    @GetMapping("/buscar/nombre")
    public ResponseEntity<List<MovimientoDTO>> buscarPorNombreProducto(@RequestParam String productoNombre) {
        List<MovimientoDTO> movimientos = movimientoService.buscarPorNombreProductoDTO(productoNombre);
        return ResponseEntity.ok(movimientos);
    }

    // ========== ENDPOINTS DE FECHAS ==========

    // Obtener movimientos por rango de fechas
    @GetMapping("/fechas")
    public ResponseEntity<List<MovimientoDTO>> obtenerPorRangoFechas(
            @RequestParam LocalDateTime fechaInicio,
            @RequestParam LocalDateTime fechaFin) {
        List<MovimientoDTO> movimientos = movimientoService.obtenerPorRangoFechasDTO(fechaInicio, fechaFin);
        return ResponseEntity.ok(movimientos);
    }

    // Obtener movimientos por producto y rango de fechas
    @GetMapping("/producto/{productoId}/fechas")
    public ResponseEntity<List<MovimientoDTO>> obtenerPorProductoYRangoFechas(
            @PathVariable Long productoId,
            @RequestParam LocalDateTime fechaInicio,
            @RequestParam LocalDateTime fechaFin) {
        List<MovimientoDTO> movimientos = movimientoService.obtenerPorProductoYRangoFechasDTO(productoId, fechaInicio, fechaFin);
        return ResponseEntity.ok(movimientos);
    }

    // Obtener movimientos por usuario y rango de fechas
    @GetMapping("/usuario/{usuarioId}/fechas")
    public ResponseEntity<List<MovimientoDTO>> obtenerPorUsuarioYRangoFechas(
            @PathVariable Long usuarioId,
            @RequestParam LocalDateTime fechaInicio,
            @RequestParam LocalDateTime fechaFin) {
        List<MovimientoDTO> movimientos = movimientoService.obtenerPorUsuarioYRangoFechasDTO(usuarioId, fechaInicio, fechaFin);
        return ResponseEntity.ok(movimientos);
    }

    // Obtener movimientos por tipo y rango de fechas
    @GetMapping("/tipo/{tipo}/fechas")
    public ResponseEntity<List<MovimientoDTO>> obtenerPorTipoYRangoFechas(
            @PathVariable TipoMovimiento tipo,
            @RequestParam LocalDateTime fechaInicio,
            @RequestParam LocalDateTime fechaFin) {
        List<MovimientoDTO> movimientos = movimientoService.obtenerPorTipoYRangoFechasDTO(tipo, fechaInicio, fechaFin);
        return ResponseEntity.ok(movimientos);
    }

    // ========== ENDPOINTS DE FILTROS MÚLTIPLES ==========

    // Buscar movimientos con filtros múltiples
    @GetMapping("/filtros")
    public ResponseEntity<List<MovimientoDTO>> buscarConFiltros(
            @RequestParam(required = false) Long productoId,
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) TipoMovimiento tipo,
            @RequestParam(required = false) LocalDateTime fechaInicio,
            @RequestParam(required = false) LocalDateTime fechaFin) {
        List<MovimientoDTO> movimientos = movimientoService.buscarConFiltrosDTO(productoId, usuarioId, tipo, fechaInicio, fechaFin);
        return ResponseEntity.ok(movimientos);
    }

    // ========== ENDPOINTS DE ESTADÍSTICAS ==========

    // Contar movimientos por tipo
    @GetMapping("/contar/tipo/{tipo}")
    public ResponseEntity<Long> contarPorTipo(@PathVariable TipoMovimiento tipo) {
        long cantidad = movimientoService.contarPorTipo(tipo);
        return ResponseEntity.ok(cantidad);
    }

    // Contar movimientos por producto
    @GetMapping("/contar/producto/{productoId}")
    public ResponseEntity<Long> contarPorProducto(@PathVariable Long productoId) {
        long cantidad = movimientoService.contarPorProducto(productoId);
        return ResponseEntity.ok(cantidad);
    }

    // Contar movimientos por usuario
    @GetMapping("/contar/usuario/{usuarioId}")
    public ResponseEntity<Long> contarPorUsuario(@PathVariable Long usuarioId) {
        long cantidad = movimientoService.contarPorUsuario(usuarioId);
        return ResponseEntity.ok(cantidad);
    }

    // Contar movimientos por producto y tipo
    @GetMapping("/contar/producto/{productoId}/tipo/{tipo}")
    public ResponseEntity<Long> contarPorProductoYTipo(
            @PathVariable Long productoId, 
            @PathVariable TipoMovimiento tipo) {
        long cantidad = movimientoService.contarPorProductoYTipo(productoId, tipo);
        return ResponseEntity.ok(cantidad);
    }

    // ========== ENDPOINTS DE TIPOS DE MOVIMIENTO ==========

    // Obtener todos los tipos de movimiento disponibles
    @GetMapping("/tipos")
    public ResponseEntity<TipoMovimiento[]> obtenerTipos() {
        TipoMovimiento[] tipos = TipoMovimiento.values();
        return ResponseEntity.ok(tipos);
    }
} 