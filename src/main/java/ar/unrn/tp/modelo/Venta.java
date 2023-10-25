package ar.unrn.tp.modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "venta")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private LocalDateTime fecha;
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Cliente cliente;
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Tarjeta tarjeta;
    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_venta")
    private List<ProductoVendido> productosVendidos;
    private Double montoTotal;
    @Column(unique = true)
    private String codigo;

    public Venta(LocalDateTime fecha, Cliente cliente, Tarjeta tarjeta, List<Producto> productosVendidos, Double montoTotal, String codigo) {
        this.productosVendidos = new ArrayList<>();
        this.fecha = fecha;
        this.cliente = cliente;
        this.tarjeta = tarjeta;
        this.montoTotal = montoTotal;
        this.codigo = codigo;
        agregarProductos(productosVendidos);
    }

    private void agregarProductos(List<Producto> productosVendidos) {
        productosVendidos.stream()
                .map(p -> new ProductoVendido(p.getDescripcion(), p.getCodigo(), p.getPrecio(), p.getMarca(), p.getCategoria()))
                .forEach(this.productosVendidos::add);
    }

}