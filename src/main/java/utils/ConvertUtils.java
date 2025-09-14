package utils;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ConvertUtils {
    private ConvertUtils() {
        throw new IllegalStateException("'ConvertUtils' cannot be instantiated.");
    }

    public static <T> String listToString(List<T> list, Function<T, ?> mapper) {
        return list.stream()
                .map(mapper)
                .map(String::valueOf)
                .collect(Collectors.joining(", ", "[", "]"));
    }
}
