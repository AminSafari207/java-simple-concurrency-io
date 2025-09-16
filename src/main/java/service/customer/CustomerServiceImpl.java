package service.customer;

import enums.CompoundingPeriod;
import exceptions.account.DuplicateAccountNumberException;
import exceptions.customer.DuplicateEmailException;
import jakarta.persistence.EntityManagerFactory;
import model.account.Account;
import model.account.CheckingAccount;
import model.account.SavingsAccount;
import model.common.Money;
import model.customer.Customer;
import repository.RepositoryProvider;
import repository.account.AccountRepository;
import repository.account.AccountRepositoryImpl;
import repository.customer.CustomerRepository;
import repository.customer.CustomerRepositoryImpl;
import service.base.TransactionalService;
import utils.ValidationUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class CustomerServiceImpl extends TransactionalService implements CustomerService {
    public CustomerServiceImpl(EntityManagerFactory emf) {
        super(emf);
    }

    @Override
    public Customer createCustomer(Customer newCustomer) {
        return executeTransactionFunction(em -> {
            CustomerRepository customerRepo = new CustomerRepositoryImpl(em);

            String email = newCustomer.getEmail();

            if (customerRepo.findByEmail(email).isPresent()) {
                throw new DuplicateEmailException(email);
            }

            return customerRepo.save(newCustomer);
        });
    }

    @Override
    public Optional<Customer> getCustomer(Long customerId) {
        return executeTransactionFunction(em -> new CustomerRepositoryImpl(em).findById(customerId));
    }

    @Override
    public List<Customer> getAllCustomers() {
        return executeTransactionFunction(em -> new CustomerRepositoryImpl(em).findAll());
    }

    @Override
    public List<Account> getCustomerAccounts(Long customerId) {
        return executeTransactionFunction(em -> new AccountRepositoryImpl(em).findAllByCustomerId(customerId));
    }

    @Override
    public CheckingAccount openCheckingAccount(Long customerId, CheckingAccount newAccount) {
        validateCheckingAccount(newAccount);

        return openAccount(customerId, newAccount);
    }

    @Override
    public SavingsAccount openSavingsAccount(Long customerId, SavingsAccount newAccount) {
        validateSavingsAccount(newAccount);

        return openAccount(customerId, newAccount);
    }

    public <T extends Account> T openAccount(Long customerId, T newAccount) {
        return executeTransactionFunction(em -> {
            CustomerRepository customerRepo = new CustomerRepositoryImpl(em);
            AccountRepository accountRepo = new AccountRepositoryImpl(em);

            Customer owner = customerRepo.findById(customerId).orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));
            String newAccountNumber = newAccount.getAccountNumber();

            if (accountRepo.findByAccountNumber(newAccountNumber).isPresent()) {
                throw new DuplicateAccountNumberException(newAccountNumber);
            }

            newAccount.setCustomer(owner);

            return (T) accountRepo.save(newAccount);
        });
    }

    ////////////////////////////////////
    /// Validators /////////////////////
    ////////////////////////////////////

    private void validateMoneyInitialized(Money money) {
        if (money == null || money.getAmount() == null || money.getCurrency() == null) {
            throw new IllegalArgumentException("Opening balance must have amount and currency.");
        }

        if (money.isNegative()) {
            throw new IllegalArgumentException("Opening balance cannot be negative.");
        }
    }

    private void validateNonNegative(BigDecimal v, String logName) {
        if (v != null && v.signum() < 0) {
            throw new IllegalArgumentException(logName + " must be >= 0");
        }
    }

    private void validatePercent(BigDecimal v, String logName) {
        if (v == null) {
            throw new IllegalArgumentException(logName + " must not be null");
        }

        if (v.signum() < 0 || v.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException(logName + " must be between 0 and 100");
        }
    }

    private void validateCheckingAccount(CheckingAccount acc) {
        ValidationUtils.validateNotNull(acc, "Account");
        ValidationUtils.validateString(acc.getAccountNumber(), "accountNumber");
        validateMoneyInitialized(acc.getBalance());
        validateNonNegative(acc.getOverdraftLimit(), "overdraftLimit");
        validateNonNegative(acc.getMaintenanceFee(), "maintenanceFee");
        validateNonNegative(acc.getWithdrawalFee(), "withdrawalFee");
    }

    private void validateSavingsAccount(SavingsAccount acc) {
        ValidationUtils.validateNotNull(acc, "Account");
        ValidationUtils.validateString(acc.getAccountNumber(), "accountNumber");
        validateMoneyInitialized(acc.getBalance());
        validatePercent(acc.getInterestRate(), "interestRate");
        validateNonNegative(acc.getMinBalanceForInterest(), "minBalanceForInterest");
    }
}
