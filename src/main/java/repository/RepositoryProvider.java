package repository;

import jakarta.persistence.EntityManager;
import repository.account.AccountRepository;
import repository.account.AccountRepositoryImpl;
import repository.transaction.TransactionRepository;
import repository.transaction.TransactionRepositoryImpl;

public class RepositoryProvider {
    private final EntityManager em;

    public RepositoryProvider(EntityManager em) {
        this.em = em;
    }

    public AccountRepository getAccountRepo() {
        return new AccountRepositoryImpl(em);
    }

    public TransactionRepository getTxnRepo() {
        return new TransactionRepositoryImpl(em);
    }
}
