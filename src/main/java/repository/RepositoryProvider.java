package repository;

import jakarta.persistence.EntityManager;
import repository.account.AccountRepository;
import repository.account.AccountRepositoryImpl;
import repository.customer.CustomerRepository;
import repository.customer.CustomerRepositoryImpl;
import repository.transaction.TransactionRepository;
import repository.transaction.TransactionRepositoryImpl;

public class RepositoryProvider {
    private final EntityManager em;

    public RepositoryProvider(EntityManager em) {
        this.em = em;
    }

    public CustomerRepository customerRepo() {
        return new CustomerRepositoryImpl(em);
    }

    public AccountRepository accountRepo() {
        return new AccountRepositoryImpl(em);
    }

    public TransactionRepository txnRepo() {
        return new TransactionRepositoryImpl(em);
    }

}
