package model.account;

import enums.CompoundingPeriod;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "savings_account")
@DiscriminatorValue("SAVINGS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder(toBuilder = true)
public class SavingsAccount extends Account {
    @Column(name = "interest_rate", nullable = false)
    private BigDecimal interestRate;

    @Column(name = "compounding_period", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CompoundingPeriod compoundingPeriod = CompoundingPeriod.MONTHLY;

    @Column(name = "min_balance_for_interest")
    @Builder.Default
    private BigDecimal minBalanceForInterest = BigDecimal.ZERO;

    @Column(name = "last_interest_applied_at")
    private LocalDateTime lastInterestAppliedAt;
}
