package cl.duoc.maestranza.Maestranza.controller;

import cl.duoc.maestranza.Maestranza.dto.HistorialPreciosDTO;
import cl.duoc.maestranza.Maestranza.model.HistorialPrecios;
import cl.duoc.maestranza.Maestranza.service.HistorialPreciosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/historial-precios")
public class HistorialPreciosController {

    @Autowired
    private HistorialPreciosService historialPreciosService;

    // Obtener todo el historial de precios (para administración)
    @GetMapping
    public ResponseEntity<List<HistorialPreciosDTO>> obtenerTodos() {
        List<HistorialPrecios> historial = historialPreciosService.obtenerTodos();
        List<HistorialPreciosDTO> historialDTO = historial.stream()
                .map(HistorialPreciosDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(historialDTO);
    }

    // Obtener historial de precios por producto
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<HistorialPreciosDTO>> obtenerHistorialPorProducto(@PathVariable Long productoId) {
        List<HistorialPrecios> historial = historialPreciosService.obtenerHistorialPorProducto(productoId);
        if (historial.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<HistorialPreciosDTO> historialDTO = historial.stream()
                .map(HistorialPreciosDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(historialDTO);
    }

    // Obtener último precio de un producto
    @GetMapping("/producto/{productoId}/ultimo")
    public ResponseEntity<HistorialPreciosDTO> obtenerUltimoPrecio(@PathVariable Long productoId) {
        Optional<HistorialPrecios> ultimoPrecio = historialPreciosService.obtenerUltimoPrecio(productoId);
        return ultimoPrecio.map(precio -> ResponseEntity.ok(HistorialPreciosDTO.fromEntity(precio)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Obtener precio actual de un producto
    @GetMapping("/producto/{productoId}/precio-actual")
    public ResponseEntity<Double> obtenerPrecioActual(@PathVariable Long productoId) {
        Double precioActual = historialPreciosService.obtenerPrecioActual(productoId);
        if (precioActual <= 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(precioActual);
    }

    // Agregar nuevo precio a un producto
    @PostMapping("/producto/{productoId}")
    public ResponseEntity<HistorialPreciosDTO> agregarPrecio(
            @PathVariable Long productoId,
            @RequestBody HistorialPreciosDTO historialPreciosDTO) {
        try {
            HistorialPrecios nuevoHistorial = historialPreciosService.agregarPrecio(productoId, historialPreciosDTO.getPrecio());
            return new ResponseEntity<>(HistorialPreciosDTO.fromEntity(nuevoHistorial), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Verificar si un producto tiene historial de precios
    @GetMapping("/producto/{productoId}/tiene-historial")
    public ResponseEntity<Boolean> tieneHistorial(@PathVariable Long productoId) {
        boolean tieneHistorial = historialPreciosService.tieneHistorial(productoId);
        return ResponseEntity.ok(tieneHistorial);
    }

    // Eliminar historial de precios de un producto (uso administrativo)
    @DeleteMapping("/producto/{productoId}")
    public ResponseEntity<Void> eliminarHistorialPorProducto(@PathVariable Long productoId) {
        try {
            historialPreciosService.eliminarHistorialPorProducto(productoId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}