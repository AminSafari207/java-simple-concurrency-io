package utils;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

public class ValidationUtils {
    private ValidationUtils() {
        throw new IllegalStateException("'ValidationUtils' cannot be instantiated.");
    }

    public static void validateIdLong(Long id, String logName) {
        validateNotNull(id, logName);
        if (id < 0) throw new IllegalArgumentException(logName + " must be 0 or positive.");
    }

    public static void validateString(String str, String logName) {
        validateNotNull(str, logName);
        if (str.trim().isEmpty())
            throw new IllegalArgumentException(logName + " cannot be empty.");
    }

    public static void validateString(String str, int minLength, String logName) {
        validateString(str, logName);
        if (str.trim().length() < minLength)
            throw new IllegalArgumentException(logName + " must have at least " + minLength + " characters.");
    }

    public static void validateNotNull(Object obj, String logName) {
        if (obj == null)
            throw new NullPointerException(logName + " cannot be null.");
    }

    public static void validateNotEmpty(Collection<?> collection, String logName) {
        validateNotNull(collection, logName);
        if (collection.isEmpty())
            throw new IllegalArgumentException(logName + " cannot be empty.");
    }

    public static void validateMapBasics(Map<?, ?> map, String logName) {
        validateNotNull(map, logName);
        if (map.isEmpty()) throw new IllegalArgumentException(logName + " cannot be empty.");
    }

    public static void validateLocalDate(LocalDate date, String logName) {
        validateNotNull(date, logName);
        if (date.isAfter(LocalDate.now())) throw new IllegalArgumentException(logName + " cannot be in the future.");
        if (date.isBefore(LocalDate.of(1900, 1, 1))) throw new IllegalArgumentException(logName + " cannot be older than 1900/01/01.");
    }
}
