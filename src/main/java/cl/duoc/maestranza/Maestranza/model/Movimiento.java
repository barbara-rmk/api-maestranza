package cl.duoc.maestranza.Maestranza.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false)
    private Integer cantidad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMovimiento tipo;

    @Column(length = 255)
    private String descripcion;

    @Column(name = "producto_codigo", length = 50)
    private String productoCodigo;

    @Column(name = "producto_nombre", length = 100)
    private String productoNombre;

    @Column(name = "image_path")
    private String imagePath;

    @Column(nullable = false)
    private Boolean activo = true;

    // Relación muchos a uno con Usuario
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonBackReference("usuario-movimientos")
    private Usuario usuario;

    // Relación muchos a uno con Producto
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id", nullable = false)
    @JsonBackReference("producto-movimientos")
    private Producto producto;

    @PrePersist
    protected void onCreate() {
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
    }
}
