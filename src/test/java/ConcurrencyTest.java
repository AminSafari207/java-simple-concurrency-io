import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import model.account.CheckingAccount;
import model.common.Money;
import model.customer.Customer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.account.AccountService;
import service.account.AccountServiceImpl;
import service.customer.CustomerService;
import service.customer.CustomerServiceImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConcurrencyTest {

    private static EntityManagerFactory emf;
    private static AccountService accountService;
    private static CustomerService customerService;

    private static Long accountAId;
    private static Long accountBId;

    @BeforeAll
    static void init() {
        emf = Persistence.createEntityManagerFactory("h2-test");
        accountService = new AccountServiceImpl(emf);
        customerService = new CustomerServiceImpl(emf);

        Customer customerA = customerService.createCustomer(
                Customer.builder()
                        .firstName("Test")
                        .lastName("1")
                        .email("c1@example.com")
                        .phoneNumber("+10000000001")
                        .build()
        );

        Customer customerB = customerService.createCustomer(
                Customer.builder()
                        .firstName("Test")
                        .lastName("Beta")
                        .email("c2@example.com")
                        .phoneNumber("+10000000002")
                        .build()
        );

        var accA = customerService.openCheckingAccount(
                customerA.getId(),
                CheckingAccount.builder()
                        .accountNumber("CHK-A-123")
                        .balance(Money.of(new BigDecimal("100000.00"), "USD"))
                        .build()
        );

        var accB = customerService.openCheckingAccount(
                customerB.getId(),
                CheckingAccount.builder()
                        .accountNumber("CHK-B-456")
                        .balance(Money.of(new BigDecimal("300.00"), "USD"))
                        .build()
        );

        accountAId = accA.getId();
        accountBId = accB.getId();
    }

    @AfterAll
    static void shutdown() {
        if (emf != null) emf.close();
    }

    @Test
    void transferTotal() throws Exception {
        BigDecimal startBalanceA = getBalance(accountAId);
        BigDecimal startBalanceB = getBalance(accountBId);
        BigDecimal startTotal = startBalanceA.add(startBalanceB);

        int threadCount = 10_000;
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            Thread th = new Thread(() -> {
                accountService.transfer(
                        accountAId,
                        accountBId,
                        Money.of(new BigDecimal("5.00"), "USD"), "test");

                System.out.println(Thread.currentThread().getName() + " -> Transferring 5.00 USD");
            });

            th.start();
            threads.add(th);
        }

        for (Thread th : threads) th.join();

        BigDecimal endBalanceA = getBalance(accountAId);
        BigDecimal endBalanceB = getBalance(accountBId);
        BigDecimal endTotal = endBalanceA.add(endBalanceB);

        BigDecimal totalTransferred = new BigDecimal("5.00").multiply(BigDecimal.valueOf(threadCount));

        assertEquals(startBalanceA.subtract(totalTransferred), endBalanceA);
        assertEquals(startBalanceB.add(totalTransferred), endBalanceB);
        assertEquals(startTotal, endTotal);

        System.out.println();
        System.out.println("Transfer : A " + startBalanceA + " -> " + endBalanceA);
        System.out.println("Transfer : B " + startBalanceB + " -> " + endBalanceB);
        System.out.println();
    }

    private static BigDecimal getBalance(Long accountId) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("select a.balance.amount from Account a where a.id = :id", BigDecimal.class)
                    .setParameter("id", accountId)
                    .getSingleResult();
        }
    }
}
