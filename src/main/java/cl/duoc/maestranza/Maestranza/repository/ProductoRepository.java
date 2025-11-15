package cl.duoc.maestranza.Maestranza.repository;

import cl.duoc.maestranza.Maestranza.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByCategoriaId(Long categoriaId);

    List<Producto> findByStockGreaterThan(Integer stock);

    List<Producto> findByStock(Integer stock);

    @Query("SELECT p FROM Producto p WHERE p.nombre LIKE %:nombre% OR p.codigo LIKE %:codigo%")
    List<Producto> buscarPorNombreOCodigo(@Param("nombre") String nombre, @Param("codigo") String codigo);
}

