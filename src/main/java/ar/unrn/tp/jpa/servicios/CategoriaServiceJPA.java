package ar.unrn.tp.jpa.servicios;

import ar.unrn.tp.api.CategoriaService;
import ar.unrn.tp.modelo.Categoria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaServiceJPA implements CategoriaService {
    private EntityManagerFactory emf;

    public CategoriaServiceJPA(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public List<Categoria> listarCategoria() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            TypedQuery<Categoria> q = em.createQuery("SELECT c FROM Categoria c", Categoria.class);
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
}