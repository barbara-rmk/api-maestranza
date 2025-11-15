package cl.duoc.maestranza.Maestranza.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "historial_precios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"producto"})
public class HistorialPrecios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double precio;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    // Relación muchos a uno con Producto
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    @JsonBackReference // Evita recursión infinita en JSON
    private Producto producto;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
}