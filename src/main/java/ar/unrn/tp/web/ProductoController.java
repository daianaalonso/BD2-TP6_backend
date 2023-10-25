package ar.unrn.tp.web;

import ar.unrn.tp.api.ProductoService;
import ar.unrn.tp.dto.ProductoRecord;
import ar.unrn.tp.modelo.Producto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/producto")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:5173/")
public class ProductoController {
    private final ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<Producto>> listarProductos() {
        return new ResponseEntity<>(productoService.listarProductos(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoRecord> buscarProducto(@PathVariable Long id) {
        return new ResponseEntity<>(productoService.buscarProducto(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> modificarProducto(@PathVariable Long id, @RequestBody ProductoRecord productoRecord) {
        productoService.modificarProducto(
                id,
                productoRecord.descripcion(),
                productoRecord.codigo(),
                productoRecord.precio(),
                productoRecord.marca(),
                productoRecord.idCategoria(),
                productoRecord.version()
        );
        Map<String, String> response = new HashMap<>();
        response.put("result", "Producto modificado correctamente");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
