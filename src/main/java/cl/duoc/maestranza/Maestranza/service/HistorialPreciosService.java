package cl.duoc.maestranza.Maestranza.service;

import cl.duoc.maestranza.Maestranza.model.HistorialPrecios;
import cl.duoc.maestranza.Maestranza.model.Producto;
import cl.duoc.maestranza.Maestranza.repository.HistorialPreciosRepository;
import cl.duoc.maestranza.Maestranza.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class HistorialPreciosService {

    @Autowired
    private HistorialPreciosRepository historialPreciosRepository;

    @Autowired
    private ProductoRepository productoRepository;

    // Obtener historial completo de un producto
    public List<HistorialPrecios> obtenerHistorialPorProducto(Long productoId) {
        return historialPreciosRepository.findByProductoIdOrderByFechaCreacionDesc(productoId);
    }

    // Obtener último precio de un producto
    public Optional<HistorialPrecios> obtenerUltimoPrecio(Long productoId) {
        List<HistorialPrecios> historial = historialPreciosRepository.findByProductoIdOrderByFechaCreacionDesc(productoId);
        return historial.isEmpty() ? Optional.empty() : Optional.of(historial.get(0));
    }

    // Agregar nuevo precio a un producto
    public HistorialPrecios agregarPrecio(Long productoId, Double precio) {
        // Verificar que el producto existe
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + productoId));

        // Validar que el precio sea válido
        if (precio == null || precio <= 0) {
            throw new RuntimeException("El precio debe ser mayor a 0");
        }

        // Crear nuevo historial de precio
        HistorialPrecios nuevoHistorial = new HistorialPrecios();
        nuevoHistorial.setPrecio(precio);
        nuevoHistorial.setProducto(producto);
        nuevoHistorial.setFechaCreacion(LocalDateTime.now());

        return historialPreciosRepository.save(nuevoHistorial);
    }

    // Crear precio inicial para un producto nuevo
    public HistorialPrecios crearPrecioInicial(Producto producto, Double precio) {
        if (precio == null || precio <= 0) {
            throw new RuntimeException("El precio inicial debe ser mayor a 0");
        }

        HistorialPrecios precioInicial = new HistorialPrecios();
        precioInicial.setPrecio(precio);
        precioInicial.setProducto(producto);
        precioInicial.setFechaCreacion(LocalDateTime.now());

        return historialPreciosRepository.save(precioInicial);
    }

    // Obtener precio actual de un producto
    public Double obtenerPrecioActual(Long productoId) {
        return obtenerUltimoPrecio(productoId)
                .map(HistorialPrecios::getPrecio)
                .orElse(0.0);
    }

    // Verificar si un producto tiene historial de precios
    public boolean tieneHistorial(Long productoId) {
        return !historialPreciosRepository.findByProductoIdOrderByFechaCreacionDesc(productoId).isEmpty();
    }

    // Eliminar historial de precios (usado cuando se elimina un producto)
    public void eliminarHistorialPorProducto(Long productoId) {
        List<HistorialPrecios> historial = historialPreciosRepository.findByProductoIdOrderByFechaCreacionDesc(productoId);
        historialPreciosRepository.deleteAll(historial);
    }

    // Obtener todos los historiales (para administración)
    public List<HistorialPrecios> obtenerTodos() {
        return historialPreciosRepository.findAll();
    }
}