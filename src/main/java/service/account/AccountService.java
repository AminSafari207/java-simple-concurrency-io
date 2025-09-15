package service.account;

import model.account.Account;
import model.common.Money;

public interface AccountService {
    Account deposit(Long accountId, Money amount, String note);
    Account withdraw(Long accountId, Money amount, String note);
    void transfer(Long fromAccountId, Long toAccountId, Money amount, String note);
}
