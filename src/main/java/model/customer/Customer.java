package model.customer;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import model.common.BaseEntity;
import model.account.Account;
import utils.ConvertUtils;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "customer",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_customer_email", columnNames = {"email"})
        })
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@SuperBuilder(toBuilder = true)
public class Customer extends BaseEntity {
    @NotBlank
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Account> accounts = new ArrayList<>();

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return "Full Name: " + getFullName() +
                "\nEmail: " + email +
                "\nPhone Number: " + phoneNumber +
                "\nAccounts: " + (accounts.isEmpty() ? "[]" : ConvertUtils.listToString(accounts, Account::getId));
    }
}
