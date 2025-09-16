package utils.concurrency;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public final class AccountLocks {
    private static final Map<Long, ReentrantLock> LOCKS = new ConcurrentHashMap<>();

    private AccountLocks() {
        throw new IllegalStateException("'AccountLocks' cannot be instantiated.");
    }

    public static ReentrantLock of(Long accountId) {
        return LOCKS.computeIfAbsent(accountId, id -> new ReentrantLock(true));
    }
}
