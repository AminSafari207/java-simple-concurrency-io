package service.customer;

import model.account.Account;
import model.account.CheckingAccount;
import model.account.SavingsAccount;
import model.customer.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    Customer createCustomer(Customer newCustomer);

    Optional<Customer> getCustomer(Long customerId);
    List<Customer> getAllCustomers();
    List<Account> getCustomerAccounts(Long customerId);

    CheckingAccount openCheckingAccount(Long customerId, CheckingAccount newAccount);
    SavingsAccount openSavingsAccount (Long customerId, SavingsAccount  newAccount);

}
