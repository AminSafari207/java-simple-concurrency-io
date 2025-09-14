package repository.transaction;

import jakarta.persistence.EntityManager;
import model.transaction.Transaction;
import repository.base.AbstractBaseRepository;

import java.time.LocalDateTime;
import java.util.List;

public class TransactionRepositoryImpl extends AbstractBaseRepository<Transaction, Long> implements TransactionRepository {
    public TransactionRepositoryImpl(EntityManager em) {
        super(em, Transaction.class);
    }

    @Override
    public List<Transaction> findAllByAccountId(Long accountId) {
        return em.createQuery("select t from " + getEntityName() + " t where t.account.id = :aid order by t.createdAt", Transaction.class)
                .setParameter("aid", accountId)
                .getResultList();
    }

    @Override
    public List<Transaction> findAllByAccountIdAndDateRange(Long accountId, LocalDateTime from, LocalDateTime to) {
        return em.createQuery(
                "select t from " + getEntityName() + " t " +
                        "where t.account.id = :aid and t.createdAt between :from and :to " +
                        "order by t.createdAt",
                        Transaction.class
                )
                .setParameter("aid", accountId)
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();
    }
}
