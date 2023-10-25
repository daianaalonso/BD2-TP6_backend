package ar.unrn.tp.modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "producto")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Version
    private Long version;
    private String descripcion;
    @Column(unique = true)
    private String codigo;
    private Double precio;
    @Embedded
    private Marca marca;
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Categoria categoria;

    public Producto(String descripcion, String codigo, Double precio, Marca marca, Categoria categoria) {
        if (esDatoVacio(codigo))
            throw new RuntimeException("El codigo debe ser valido");
        this.codigo = codigo;

        if (descripcion == null || descripcion.isEmpty())
            throw new RuntimeException("La descripcion debe ser valida");
        this.descripcion = descripcion;

        if (esDatoVacio(String.valueOf(precio)) || esDatoNulo(precio))
            throw new RuntimeException("El precio debe ser valido");
        this.precio = precio;

        if (esDatoNulo(categoria))
            throw new RuntimeException("La categoria debe ser valida");
        this.categoria = categoria;

        if (esDatoNulo(marca))
            throw new RuntimeException("La marca debe ser valida");
        this.marca = marca;
    }

    private boolean esDatoVacio(String dato) {
        return dato.equals("");
    }

    private boolean esDatoNulo(Object dato) {
        return dato == null;
    }

    public boolean suMarcaEs(String marca) {
        return this.marca.esMarca(marca);
    }
}