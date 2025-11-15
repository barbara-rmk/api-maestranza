package cl.duoc.maestranza.Maestranza.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "categorias")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"productos"})
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    // Relación uno a muchos con Producto
    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("categoria-productos")
    private List<Producto> productos;
}