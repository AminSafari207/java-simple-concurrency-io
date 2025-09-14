package model.account;

import enums.AccountStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import model.common.BaseEntity;
import model.common.Money;
import model.customer.Customer;

import java.time.LocalDateTime;

@Entity
@Table(name = "account")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "account_type")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder(toBuilder = true)
public abstract class Account extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String accountNumber;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(
                name = "amount",
                column = @Column(name = "balance_amount", nullable = false, precision = 19, scale = 2)
        ),
        @AttributeOverride(
                name = "currency",
                column = @Column(name = "balance_currency", nullable = false, length = 3)
        )
    })
    private Money balance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AccountStatus status = AccountStatus.ACTIVE;

    @Column
    private LocalDateTime closedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    protected void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public boolean canDebit(Money amount) {
        return balance != null && !amount.isNegative() && balance.isGreaterOrEqual(amount);
    }

    @Override
    public String toString() {
        return "Account ID: " + getId() +
                "\nAccount Number: " + accountNumber +
                "\nBalance: " + balance.getAmount() +
                "\nStatus: " + status +
                "\nCustomer ID: " + (customer == null ? null : customer.getId());
    }
}
