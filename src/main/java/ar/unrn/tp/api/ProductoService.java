package ar.unrn.tp.api;

import ar.unrn.tp.dto.ProductoRecord;
import ar.unrn.tp.modelo.Producto;

import java.util.List;

public interface ProductoService {
    //validar que sea una categoría existente y que codigo no se repita
    void crearProducto(String codigo, String descripcion, Double precio, Long idCategoria, String marca);

    //validar que sea un producto existente
    void modificarProducto(Long idProducto, String descripcion, String codigo, Double precio, String marca, Long idCategoria, Long version);

    //Devuelve todos los productos
    List<Producto> listarProductos();

    ProductoRecord buscarProducto(Long idProducto);
}