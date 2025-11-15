package cl.duoc.maestranza.Maestranza.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"categoria", "historialPrecios", "alertas"})
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    @Column(nullable = false)
    private Integer stock;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "fecha_ingreso")
    private LocalDateTime fechaIngreso;

    @Column(length = 100)
    private String ubicacion;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "umbral_stock")
    private Integer umbralStock;

    // Relación muchos a uno con Categoria
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoria_id", nullable = false)
    @JsonBackReference("categoria-productos")
    private Categoria categoria;

    // Relación uno a muchos con HistorialPrecios
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JsonManagedReference
    @OrderBy("fechaCreacion DESC") // El más reciente primero
    private List<HistorialPrecios> historialPrecios;

    // Relación uno a muchos con Alerta
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference("producto-alertas")
    @OrderBy("fecha DESC")
    private List<Alerta> alertas;

    @PrePersist
    protected void onCreate() {
        if (fechaIngreso == null) {
            fechaIngreso = LocalDateTime.now();
        }
        if (activo == null) {
            activo = true;
        }
    }

    @Transient
    public Double getPrecioActual() {
        if (historialPrecios != null && !historialPrecios.isEmpty()) {
            return historialPrecios.get(0).getPrecio();
        }
        return 0.0;
    }
}