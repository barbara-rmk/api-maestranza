package cl.duoc.maestranza.Maestranza.repository;

import cl.duoc.maestranza.Maestranza.model.HistorialPrecios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialPreciosRepository extends JpaRepository<HistorialPrecios, Long> {

    List<HistorialPrecios> findByProductoIdOrderByFechaCreacionDesc(Long productoId);

    @Query("SELECT h FROM HistorialPrecios h WHERE h.producto.id = :productoId ORDER BY h.fechaCreacion DESC LIMIT 1")
    HistorialPrecios findUltimoPrecioPorProducto(@Param("productoId") Long productoId);
}