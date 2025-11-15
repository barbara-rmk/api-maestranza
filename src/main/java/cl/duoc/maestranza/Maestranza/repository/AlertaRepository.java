package cl.duoc.maestranza.Maestranza.repository;

import cl.duoc.maestranza.Maestranza.model.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    // ===== CONSULTAS GENERALES =====
    // Agregar este método
    List<Alerta> findByFechaBetweenAndActivoTrueOrderByFechaDesc(Date fechaInicio, Date fechaFin);

    // Obtener todas las alertas ordenadas por fecha (más recientes primero)
    List<Alerta> findAllByOrderByFechaDesc();

    // Obtener solo alertas activas - CORREGIDO: activo en lugar de activa
    List<Alerta> findByActivoTrueOrderByFechaDesc();

    // Obtener solo alertas inactivas - CORREGIDO: activo en lugar de activa
    List<Alerta> findByActivoFalseOrderByFechaDesc();

    // ===== CONSULTAS POR PRODUCTO =====

    // Obtener alertas por producto específico
    List<Alerta> findByProductoIdOrderByFechaDesc(Long productoId);

    // Obtener alertas activas por producto - CORREGIDO: activo en lugar de activa
    List<Alerta> findByProductoIdAndActivoTrueOrderByFechaDesc(Long productoId);

    // Obtener alertas por código de producto
    @Query("SELECT a FROM Alerta a WHERE a.producto.codigo = :codigoProducto ORDER BY a.fecha DESC")
    List<Alerta> findByProductoCodigo(@Param("codigoProducto") String codigoProducto);

    // ===== CONSULTAS POR FECHA =====

    // Obtener alertas por rango de fechas
    List<Alerta> findByFechaBetweenOrderByFechaDesc(Date fechaInicio, Date fechaFin);

    // Obtener alertas activas desde una fecha - CORREGIDO: activo en lugar de
    // activa
    @Query("SELECT a FROM Alerta a WHERE a.fecha >= :fechaDesde AND a.activo = true ORDER BY a.fecha DESC")
    List<Alerta> findAlertasActivasDesde(@Param("fechaDesde") Date fechaDesde);

    // Obtener alertas del día actual
    @Query("SELECT a FROM Alerta a WHERE DATE(a.fecha) = CURRENT_DATE ORDER BY a.fecha DESC")
    List<Alerta> findAlertasDelDia();

    // Obtener alertas activas del día actual - CORREGIDO: activo en lugar de activa
    @Query("SELECT a FROM Alerta a WHERE DATE(a.fecha) = CURRENT_DATE AND a.activo = true ORDER BY a.fecha DESC")
    List<Alerta> findAlertasActivasDelDia();

    // ===== CONSULTAS DE BÚSQUEDA =====

    // Buscar alertas por nombre del producto
    @Query("SELECT a FROM Alerta a WHERE a.producto.nombre LIKE %:nombreProducto% ORDER BY a.fecha DESC")
    List<Alerta> findByProductoNombreContaining(@Param("nombreProducto") String nombreProducto);

    // Buscar alertas por tipo (nombre de alerta)
    List<Alerta> findByNombreContainingIgnoreCaseOrderByFechaDesc(String tipo);

    // ===== CONSULTAS DE CONTEO =====

    // Contar alertas activas - CORREGIDO: activo en lugar de activa
    long countByActivoTrue();

    // Contar alertas por producto
    long countByProductoId(Long productoId);

    // Contar alertas activas por producto - CORREGIDO: activo en lugar de activa
    long countByProductoIdAndActivoTrue(Long productoId);

    // ===== CONSULTAS LIMITADAS =====

    // Obtener las últimas N alertas activas - CORREGIDO: activo en lugar de activa
    @Query("SELECT a FROM Alerta a WHERE a.activo = true ORDER BY a.fecha DESC LIMIT :limite")
    List<Alerta> findTopAlertasActivas(@Param("limite") int limite);

    // ===== OPERACIONES DE ESTADO =====

    // Marcar alerta como inactiva - CORREGIDO: activo en lugar de activa
    @Modifying
    @Transactional
    @Query("UPDATE Alerta a SET a.activo = false WHERE a.id = :id")
    int marcarComoInactiva(@Param("id") Long id);

    // Marcar alerta como activa - CORREGIDO: activo en lugar de activa
    @Modifying
    @Transactional
    @Query("UPDATE Alerta a SET a.activo = true WHERE a.id = :id")
    int marcarComoActiva(@Param("id") Long id);

    // Marcar todas las alertas de un producto como inactivas - CORREGIDO: activo en
    // lugar de activa
    @Modifying
    @Transactional
    @Query("UPDATE Alerta a SET a.activo = false WHERE a.producto.id = :productoId")
    int marcarAlertasProductoComoInactivas(@Param("productoId") Long productoId);

    // ===== VERIFICACIONES =====

    // Verificar si existe alguna alerta activa para un producto - CORREGIDO: activo
    // en lugar de activa
    boolean existsByProductoIdAndActivoTrue(Long productoId);
}