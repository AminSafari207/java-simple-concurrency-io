package model.common;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import utils.ValidationUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Embeddable
@Getter
@EqualsAndHashCode
public class Money {
    private BigDecimal amount;
    private String currency;

    protected Money() {}

    private Money(BigDecimal amount, String currency) {
        ValidationUtils.validateNotNull(amount, "amount");
        ValidationUtils.validateNotNull(currency, "currency");

        this.amount = amount.setScale(2, RoundingMode.HALF_EVEN);
        this.currency = currency.toUpperCase();
    }

    public static Money of(BigDecimal amount, String currency) {
        return new Money(amount, currency);
    }

    public Money plus(Money other) {
        validateSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money minus(Money other) {
        validateSameCurrency(other);
        return new Money(this.amount.subtract(other.amount), this.currency);
    }

    public boolean isGreaterOrEqual(Money other) {
        validateSameCurrency(other);
        return this.amount.compareTo(other.amount) >= 0;
    }

    public boolean isNegative() {
        return this.amount.signum() < 0;
    }

    public boolean isNegativeOrZero() {
        return this.amount.signum() <= 0;
    }

    private void validateSameCurrency(Money other) {
        if (!Objects.equals(this.currency, other.currency)) {
            throw new IllegalArgumentException("Currency mismatch: " + this.currency + " != " + other.currency);
        }
    }

    @Override
    public String toString() {
        return amount + " " + currency;
    }
}
