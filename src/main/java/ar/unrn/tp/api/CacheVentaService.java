package ar.unrn.tp.api;

import ar.unrn.tp.modelo.Venta;

import java.util.List;
import java.util.Optional;

public interface CacheVentaService {

    void guardarVentas(Long idCliente, List<Venta> ventas);

    Optional<List<Venta>> listarVentasDeCliente(Long idCliente);

    void limpiarCache(Long idCliente);
}
