package repository.account;

import model.account.Account;
import repository.base.BaseRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends BaseRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String accountNumber);
    List<Account> findAllByCustomerId(Long customerId);
}
