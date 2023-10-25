package ar.unrn.tp.jpa.servicios;

import ar.unrn.tp.api.ProductoService;
import ar.unrn.tp.dto.ProductoRecord;
import ar.unrn.tp.modelo.Categoria;
import ar.unrn.tp.modelo.Marca;
import ar.unrn.tp.modelo.Producto;
import jakarta.persistence.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoServiceJPA implements ProductoService {
    private EntityManagerFactory emf;

    public ProductoServiceJPA(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public void crearProducto(String codigo, String descripcion, Double precio, Long idCategoria, String marca) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            TypedQuery<Producto> q = em.createQuery("SELECT p FROM Producto p WHERE p.codigo = :codigo", Producto.class);
            q.setParameter("codigo", codigo);
            List<Producto> productos = q.getResultList();
            if (!productos.isEmpty())
                throw new RuntimeException("El codigo ya existe.");

            Categoria categoria = em.find(Categoria.class, idCategoria);
            if (categoria == null)
                throw new RuntimeException("La categoria no existe.");

            Producto p = new Producto(descripcion, codigo, precio, new Marca(marca), categoria);
            em.persist(p);
            tx.commit();
        } catch (PersistenceException pe) {
            tx.rollback();
            throw new RuntimeException("El codigo ya existe.");
        } catch (Exception e) {
            tx.rollback();
            throw new RuntimeException(e);
        } finally {
            if (em != null && em.isOpen())
                em.close();
        }
    }

    @Override
    public void modificarProducto(Long idProducto, String descripcion, String codigo, Double precio, String marca, Long idCategoria, Long version) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Producto p = em.find(Producto.class, idProducto);
            if (p == null)
                throw new RuntimeException("El producto no existe.");
            Categoria categoria = em.find(Categoria.class, idCategoria);
            if (categoria == null)
                throw new RuntimeException("La categoria no existe.");

            /*p.setDescripcion(descripcion);
            p.setPrecio(precio);
            p.setCodigo(codigo);
            p.setMarca(new Marca(marca));
            p.setCategoria(categoria);
            p.setVersion(version);*/

            Producto productoModificado = new Producto(descripcion, codigo, precio, new Marca(marca), categoria);

            productoModificado.setId(idProducto);
            productoModificado.setVersion(version);

            em.merge(productoModificado);

            tx.commit();
        } catch (OptimisticLockException o) {
            tx.rollback();
            throw new RuntimeException("Error de concurrencia: El producto ha sido modificado por otro usuario.");
        } catch (Exception e) {
            tx.rollback();
            if (e.getCause() != null && e.getCause().getClass()
                    .equals(OptimisticLockException.class)) {
                throw new RuntimeException("Error de concurrencia: El producto ha sido modificado por otro usuario.");
            }
            throw e;
        } finally {
            if (em != null && em.isOpen())
                em.close();
        }
    }

    @Override
    public List<Producto> listarProductos() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            TypedQuery<Producto> q = em.createQuery("SELECT p FROM Producto p", Producto.class);
            tx.commit();

            return q.getResultList();
        } catch (Exception e) {
            tx.rollback();
            throw new RuntimeException(e);
        } finally {
            if (em != null && em.isOpen())
                em.close();
        }
    }

    @Override
    public ProductoRecord buscarProducto(Long idProducto) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Producto producto = em.find(Producto.class, idProducto);
            if (producto == null)
                throw new RuntimeException("El producto no existe.");
            tx.commit();
            return new ProductoRecord(idProducto,
                    producto.getDescripcion(),
                    producto.getCodigo(),
                    producto.getPrecio(),
                    producto.getMarca().getNombre(),
                    producto.getCategoria().getId(),
                    producto.getVersion());
        } catch (Exception e) {
            tx.rollback();
            throw new RuntimeException(e);
        } finally {
            if (em != null && em.isOpen())
                em.close();
        }
    }
}