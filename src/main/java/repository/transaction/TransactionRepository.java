package repository.transaction;

import model.transaction.Transaction;
import repository.base.BaseRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends BaseRepository<Transaction, Long> {
    List<Transaction> findAllByAccountId(Long accountId);
    List<Transaction> findAllByAccountIdAndDateRange(Long accountId, LocalDateTime from, LocalDateTime to);
}
