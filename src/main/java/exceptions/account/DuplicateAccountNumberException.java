package exceptions.account;

public class DuplicateAccountNumberException extends RuntimeException {
    public DuplicateAccountNumberException(String accNo) {
        super("Account number already exists: " + accNo);
    }
}
