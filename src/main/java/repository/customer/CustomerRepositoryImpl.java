package repository.customer;

import jakarta.persistence.EntityManager;
import model.customer.Customer;
import repository.base.AbstractBaseRepository;

import java.util.List;
import java.util.Optional;

public class CustomerRepositoryImpl extends AbstractBaseRepository<Customer, Long> implements CustomerRepository {
    public CustomerRepositoryImpl(EntityManager em) {
        super(em, Customer.class);
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        List<Customer> list = em.createQuery("select c from " + getEntityName() + " c where c.email = :email", Customer.class)
                .setParameter("email", email)
                .setMaxResults(1)
                .getResultList();

        return list.isEmpty() ? Optional.empty() : Optional.of(list.getFirst());
    }
}
