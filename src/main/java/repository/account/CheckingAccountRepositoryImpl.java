package repository.account;

import jakarta.persistence.EntityManager;
import model.account.CheckingAccount;
import repository.base.AbstractBaseRepository;

public class CheckingAccountRepositoryImpl extends AbstractBaseRepository<CheckingAccount, Long> implements CheckingAccountRepository {
    public CheckingAccountRepositoryImpl(EntityManager em) {
        super(em, CheckingAccount.class);
    }
}
