package repository.account;

import jakarta.persistence.EntityManager;
import model.account.SavingsAccount;
import repository.base.AbstractBaseRepository;

public class SavingsAccountRepositoryImpl extends AbstractBaseRepository<SavingsAccount, Long> implements SavingsAccountRepository {
    public SavingsAccountRepositoryImpl(EntityManager em) {
        super(em, SavingsAccount.class);
    }
}
