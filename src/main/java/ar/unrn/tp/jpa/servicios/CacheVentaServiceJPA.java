package ar.unrn.tp.jpa.servicios;

import ar.unrn.tp.api.CacheVentaService;
import ar.unrn.tp.modelo.Venta;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CacheVentaServiceJPA implements CacheVentaService {

    private Jedis jedis;

    private void start() {
        this.jedis = new Jedis("localhost", 6379);
    }

    private void close() {
        this.jedis.close();
    }

    @Override
    public void guardarVentas(Long idCliente, List<Venta> ventas) {
        this.start();
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

            try {
                String ventaJson = mapper.writeValueAsString(ventas);
                this.jedis.set("cliente:" + idCliente.toString(), ventaJson);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

        this.close();
    }

    @Override
    public Optional<List<Venta>> listarVentasDeCliente(Long idCliente) {
        this.start();
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        String ventasJson = this.jedis.get("cliente:" + idCliente.toString());
        if (ventasJson == null){
            return Optional.empty();
        }
        List<Venta> ventas = null;
            try {
                ventas = mapper.readValue(ventasJson, List.class);

            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        this.close();

        return Optional.ofNullable(ventas);
    }

    @Override
    public void limpiarCache(Long idCliente) {
        this.start();
        this.jedis.del("cliente:" + idCliente.toString());
        this.close();
    }
}
