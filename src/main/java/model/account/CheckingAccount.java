package model.account;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import model.common.Money;

import java.math.BigDecimal;

@Entity
@Table(name = "checking_account")
@DiscriminatorValue("CHECKING")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder(toBuilder = true)
public class CheckingAccount extends Account {
    @Column(name = "overdraft_limit", nullable = false)
    @Builder.Default
    private BigDecimal overdraftLimit = BigDecimal.ZERO;

    @Column(name = "maintenance_fee")
    private BigDecimal maintenanceFee;

    @Column(name = "withdrawal_fee")
    private BigDecimal withdrawalFee;

    @Override
    public boolean canDebit(Money amount) {
        if (amount == null || amount.isNegative() || getBalance() == null) return false;

        return getBalance()
                .getAmount()
                .add(overdraftLimit)
                .compareTo(amount.getAmount()) >= 0;
    }
}
