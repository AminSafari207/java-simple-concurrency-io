package service.base;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.util.function.Consumer;
import java.util.function.Function;

public abstract class TransactionalService {
    protected final EntityManagerFactory emf;

    public TransactionalService(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void executeTransactionConsumer(Consumer<EntityManager> consumer) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            consumer.accept(em);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Transaction failed.", e);
        } finally {
            em.close();
        }
    }

    public <R> R executeTransactionFunction(Function<EntityManager, R> function) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            R res = function.apply(em);
            tx.commit();

            return res;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Transaction failed.", e);
        } finally {
            em.close();
        }
    }
}
