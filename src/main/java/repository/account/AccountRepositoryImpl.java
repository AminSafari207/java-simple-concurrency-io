package repository.account;

import jakarta.persistence.EntityManager;
import model.account.Account;
import repository.base.AbstractBaseRepository;

import java.util.List;
import java.util.Optional;

public class AccountRepositoryImpl extends AbstractBaseRepository<Account, Long> implements AccountRepository {
    public AccountRepositoryImpl(EntityManager em) {
        super(em, Account.class);
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        var list = em.createQuery("select a from " + getEntityName() + " a where a.accountNumber = :accountNumber", Account.class)
                .setParameter("accountNumber", accountNumber)
                .setMaxResults(1)
                .getResultList();

        return list.isEmpty() ? Optional.empty() : Optional.of(list.getFirst());
    }

    @Override
    public List<Account> findAllByCustomerId(Long customerId) {
        return em.createQuery("select a from " + getEntityName() + " a where a.customer.id = :cid", Account.class)
                .setParameter("cid", customerId)
                .getResultList();
    }
}
