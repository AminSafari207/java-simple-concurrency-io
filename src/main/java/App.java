import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import model.account.CheckingAccount;
import model.account.SavingsAccount;
import model.common.Money;
import model.customer.Customer;
import service.account.AccountService;
import service.account.AccountServiceImpl;
import service.customer.CustomerService;
import service.customer.CustomerServiceImpl;
import utils.PrintUtils;

import java.math.BigDecimal;
import java.util.List;

public class App {
    public static void main(String[] args) {
        try (EntityManagerFactory emf = Persistence.createEntityManagerFactory("postgresql")) {

            CustomerService customerService = new CustomerServiceImpl(emf);
            AccountService accountService = new AccountServiceImpl(emf);

            Customer alice = customerService.createCustomer(
                    Customer.builder()
                            .firstName("Alice")
                            .lastName("Nguyen")
                            .email("alice@example.com")
                            .phoneNumber("+1-202-555-0101")
                            .build()
            );

            Customer bob = customerService.createCustomer(
                    Customer.builder()
                            .firstName("Bob")
                            .lastName("Khan")
                            .email("bob@example.com")
                            .phoneNumber("+1-202-555-0102")
                            .build()
            );

            Customer cara = customerService.createCustomer(
                    Customer.builder()
                            .firstName("Cara")
                            .lastName("Smith")
                            .email("cara@example.com")
                            .phoneNumber("+1-202-555-0103")
                            .build()
            );

            var a1 = customerService.openCheckingAccount(
                    alice.getId(),
                    CheckingAccount.builder()
                            .accountNumber("CHK-1001")
                            .balance(Money.of(new BigDecimal("1500.00"), "USD"))
                            .overdraftLimit(new BigDecimal("300.00"))
                            .maintenanceFee(new BigDecimal("5.00"))
                            .withdrawalFee(new BigDecimal("1.00"))
                            .build()
            );

            var a2 = customerService.openSavingsAccount(
                    alice.getId(),
                    SavingsAccount.builder()
                            .accountNumber("SAV-1002")
                            .balance(Money.of(new BigDecimal("2500.00"), "USD"))
                            .interestRate(new BigDecimal("2.75"))
                            .build()
            );

            var b1 = customerService.openCheckingAccount(
                    bob.getId(),
                    CheckingAccount.builder()
                            .accountNumber("CHK-2001")
                            .balance(Money.of(new BigDecimal("800.00"), "USD"))
                            .overdraftLimit(new BigDecimal("200.00"))
                            .build()
            );

            var c1 = customerService.openSavingsAccount(
                    cara.getId(),
                    SavingsAccount.builder()
                            .accountNumber("SAV-3001")
                            .balance(Money.of(new BigDecimal("1200.00"), "USD"))
                            .interestRate(new BigDecimal("3.10"))
                            .build()
            );

            accountService.deposit(
                    a1.getId(),
                    Money.of(new BigDecimal("200.00"), "USD"),
                    "Initial top-up"
            );
            accountService.withdraw(
                    a1.getId(),
                    Money.of(new BigDecimal("50.00"), "USD"),
                    "ATM cash"
            );
            accountService.transfer(
                    a1.getId(),
                    b1.getId(), Money.of(new BigDecimal("125.00"), "USD"),
                    "Pay Bob back"
            );
            accountService.deposit(
                    b1.getId(),
                    Money.of(new BigDecimal("400.00"), "USD"),
                    "Salary partial"
            );
            accountService.transfer(
                    b1.getId(),
                    c1.getId(), Money.of(new BigDecimal("200.00"), "USD"),
                    "Gift to Cara"
            );
            accountService.withdraw(
                    c1.getId(),
                    Money.of(new BigDecimal("75.00"), "USD"),
                    "Groceries"
            );
            accountService.deposit(
                    a2.getId(),
                    Money.of(new BigDecimal("300.00"), "USD"),
                    "Move to savings"
            );

            PrintUtils.printList(
                    "Customers Details",
                    customerService.getAllCustomers()
            );
        }
    }
}
