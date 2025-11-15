package cl.duoc.maestranza.Maestranza.repository;

import cl.duoc.maestranza.Maestranza.model.Movimiento;
import cl.duoc.maestranza.Maestranza.model.TipoMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    // Buscar movimientos por producto
    List<Movimiento> findByProductoIdOrderByFechaDesc(Long productoId);

    // Buscar movimientos por usuario
    List<Movimiento> findByUsuarioIdOrderByFechaDesc(Long usuarioId);

    // Buscar movimientos por tipo
    List<Movimiento> findByTipoOrderByFechaDesc(TipoMovimiento tipo);

    // Buscar movimientos por producto y tipo
    List<Movimiento> findByProductoIdAndTipoOrderByFechaDesc(Long productoId, TipoMovimiento tipo);

    // Buscar movimientos por usuario y tipo
    List<Movimiento> findByUsuarioIdAndTipoOrderByFechaDesc(Long usuarioId, TipoMovimiento tipo);

    // Buscar movimientos por rango de fechas
    List<Movimiento> findByFechaBetweenOrderByFechaDesc(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    // Buscar movimientos por producto y rango de fechas
    List<Movimiento> findByProductoIdAndFechaBetweenOrderByFechaDesc(Long productoId, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    // Buscar movimientos por usuario y rango de fechas
    List<Movimiento> findByUsuarioIdAndFechaBetweenOrderByFechaDesc(Long usuarioId, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    // Buscar movimientos por tipo y rango de fechas
    List<Movimiento> findByTipoAndFechaBetweenOrderByFechaDesc(TipoMovimiento tipo, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    // Buscar movimientos por código de producto
    List<Movimiento> findByProductoCodigoOrderByFechaDesc(String productoCodigo);

    // Buscar movimientos por nombre de producto
    List<Movimiento> findByProductoNombreContainingIgnoreCaseOrderByFechaDesc(String productoNombre);

    // Query personalizada para buscar movimientos con filtros múltiples
    @Query("SELECT m FROM Movimiento m WHERE " +
           "(:productoId IS NULL OR m.producto.id = :productoId) AND " +
           "(:usuarioId IS NULL OR m.usuario.id = :usuarioId) AND " +
           "(:tipo IS NULL OR m.tipo = :tipo) AND " +
           "(:fechaInicio IS NULL OR m.fecha >= :fechaInicio) AND " +
           "(:fechaFin IS NULL OR m.fecha <= :fechaFin) " +
           "ORDER BY m.fecha DESC")
    List<Movimiento> findMovimientosWithFilters(
            @Param("productoId") Long productoId,
            @Param("usuarioId") Long usuarioId,
            @Param("tipo") TipoMovimiento tipo,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );

    // Contar movimientos por tipo
    long countByTipo(TipoMovimiento tipo);

    // Contar movimientos por producto
    long countByProductoId(Long productoId);

    // Contar movimientos por usuario
    long countByUsuarioId(Long usuarioId);

    // Contar movimientos por producto y tipo
    long countByProductoIdAndTipo(Long productoId, TipoMovimiento tipo);
}
