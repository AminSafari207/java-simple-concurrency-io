package model.transaction;

import enums.TransactionType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import model.account.Account;
import model.common.BaseEntity;
import model.common.Money;

@Entity
@Table(name = "transaction")
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Transaction extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount",   column = @Column(name = "amount", nullable = false)),
            @AttributeOverride(name = "currency", column = @Column(name = "currency", nullable = false))
    })
    private Money amount;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount",   column = @Column(name = "balance_after_amount", nullable = false)),
            @AttributeOverride(name = "currency", column = @Column(name = "balance_after_currency", nullable = false))
    })
    private Money balanceAfter;

    @Column(name = "correlation_id")
    private String correlationId;

    @Column(name = "narrative")
    private String narrative;
}
