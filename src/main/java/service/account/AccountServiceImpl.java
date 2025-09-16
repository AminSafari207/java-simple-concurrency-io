package service.account;

import enums.AccountStatus;
import exceptions.account.AccountNotFoundException;
import exceptions.account.InsufficientFundsException;
import exceptions.account.InvalidCurrencyException;
import jakarta.persistence.EntityManagerFactory;
import model.account.Account;
import model.common.Money;
import model.transaction.Transaction;
import enums.TransactionType;
import repository.account.AccountRepository;
import repository.account.AccountRepositoryImpl;
import repository.transaction.TransactionRepository;
import repository.transaction.TransactionRepositoryImpl;
import service.base.TransactionalService;
import utils.concurrency.AccountLocks;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

public class AccountServiceImpl extends TransactionalService implements AccountService {

    public AccountServiceImpl(EntityManagerFactory emf) {
        super(emf);
    }

    @Override
    public Account deposit(Long accountId, Money amount, String note) {
        validateAmountPositive(amount);

        ReentrantLock lock = AccountLocks.of(accountId);

        lock.lock();

        try {
            return executeTransactionFunction(em -> {
                AccountRepository accountRepo = new AccountRepositoryImpl(em);
                TransactionRepository txnRepo = new TransactionRepositoryImpl(em);

                Account acc = accountRepo.findById(accountId).orElseThrow(() -> new AccountNotFoundException(accountId));

                validateAccountActive(acc);
                validateSameCurrency(acc, amount);

                Money newBal = acc.getBalance().plus(amount);
                acc.setBalance(newBal);

                accountRepo.save(acc);
                txnRepo.save(
                        Transaction.builder()
                                .account(acc)
                                .type(TransactionType.DEPOSIT)
                                .amount(amount)
                                .balanceAfter(newBal)
                                .narrative(note)
                                .build()
                );

                return acc;
            });
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Account withdraw(Long accountId, Money amount, String note) {
        validateAmountPositive(amount);

        ReentrantLock lock = AccountLocks.of(accountId);

        lock.lock();

        try {
            return executeTransactionFunction(em -> {
                AccountRepository accountRepo = new AccountRepositoryImpl(em);
                TransactionRepository txnRepo = new TransactionRepositoryImpl(em);

                Account acc = accountRepo.findById(accountId).orElseThrow(() -> new AccountNotFoundException(accountId));

                validateAccountActive(acc);
                validateSameCurrency(acc, amount);

                if (!acc.canDebit(amount)) {
                    throw new InsufficientFundsException("Insufficient funds or overdraft limit exceeded.");
                }

                Money newBal = acc.getBalance().minus(amount);
                acc.setBalance(newBal);

                accountRepo.save(acc);
                txnRepo.save(
                        Transaction.builder()
                                .account(acc)
                                .type(TransactionType.WITHDRAWAL)
                                .amount(amount)
                                .balanceAfter(newBal)
                                .narrative(note)
                                .build()
                );

                return acc;
            });
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void transfer(Long fromId, Long toId, Money amount, String note) {
        if (fromId.equals(toId)) throw new IllegalArgumentException("Cannot transfer to the same account.");
        validateAmountPositive(amount);

        long firstId = Math.min(fromId, toId);
        long secondId = Math.max(fromId, toId);

        ReentrantLock firstLock = AccountLocks.of(firstId);
        ReentrantLock secondLock = AccountLocks.of(secondId);

        firstLock.lock();

        try {
            secondLock.lock();

            try {
                executeTransactionConsumer(em -> {
                    AccountRepository accountRepo = new AccountRepositoryImpl(em);
                    TransactionRepository txnRepo = new TransactionRepositoryImpl(em);

                    Account from = accountRepo.findById(fromId).orElseThrow(() -> new AccountNotFoundException(fromId));
                    Account to = accountRepo.findById(toId).orElseThrow(() -> new AccountNotFoundException(toId));

                    validateAccountActive(from);
                    validateAccountActive(to);
                    validateSameCurrency(from, amount);
                    validateSameCurrency(to, amount);

                    if (!from.getBalance().getCurrency().equals(to.getBalance().getCurrency())) {
                        throw new InvalidCurrencyException("Accounts have different currencies.");
                    }

                    if (!from.canDebit(amount)) {
                        throw new InsufficientFundsException("Insufficient funds or overdraft limit exceeded.");
                    }

                    String correlationId = UUID.randomUUID().toString();

                    Money fromNew = from.getBalance().minus(amount);
                    from.setBalance(fromNew);

                    accountRepo.save(from);
                    txnRepo.save(
                            Transaction.builder()
                                    .account(from)
                                    .type(TransactionType.TRANSFER_OUT)
                                    .amount(amount)
                                    .balanceAfter(fromNew)
                                    .correlationId(correlationId)
                                    .narrative(note)
                                    .build()
                    );

                    Money toNew = to.getBalance().plus(amount);
                    to.setBalance(toNew);

                    accountRepo.save(to);
                    txnRepo.save(
                            Transaction.builder()
                                    .account(to)
                                    .type(TransactionType.TRANSFER_IN)
                                    .amount(amount)
                                    .balanceAfter(toNew)
                                    .correlationId(correlationId)
                                    .narrative(note)
                                    .build()
                    );
                });
            } finally {
                secondLock.unlock();
            }
        } finally {
            firstLock.unlock();
        }
    }

    ////////////////////////////////////
    /// Validators /////////////////////
    ////////////////////////////////////

    private void validateAmountPositive(Money amount) {
        if (amount == null || amount.getAmount() == null || amount.isNegativeOrZero()) {
            throw new IllegalArgumentException("Amount must be higher than 0.");
        }
    }

    private void validateSameCurrency(Account acc, Money m) {
        if (acc.getBalance() == null) {
            throw new IllegalStateException("Account balance has not been initialized.");
        }

        if (!Objects.equals(acc.getBalance().getCurrency(), m.getCurrency())) {
            throw new InvalidCurrencyException(
                    "Currency mismatch: account=" + acc.getBalance().getCurrency() +
                    ", amount=" + m.getCurrency()
            );
        }
    }

    private void validateAccountActive(Account acc) {
        if (
                acc.getStatus() == null ||
                acc.getStatus() == AccountStatus.CLOSED ||
                acc.getStatus() == AccountStatus.FROZEN
        ) {
            throw new IllegalStateException("Account is not ACTIVE.");
        }
    }
}
