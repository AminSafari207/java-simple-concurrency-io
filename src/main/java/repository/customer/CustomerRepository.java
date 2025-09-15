package repository.customer;

import model.customer.Customer;
import repository.base.BaseRepository;

import java.util.Optional;

public interface CustomerRepository extends BaseRepository<Customer, Long> {
    Optional<Customer> findByEmail(String email);
}
