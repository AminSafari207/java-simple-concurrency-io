package utils;

import java.util.List;

public class PrintUtils {
    private PrintUtils() {
        throw new IllegalStateException("'PrintUtils' cannot be instantiated.");
    }

    public static <T> void printList(String header, List<T> items) {
        String symbol = "#";
        String title = symbol.repeat(3) + " " + header + " " + symbol.repeat(3);
        String divider = symbol.repeat(title.length());

        System.out.println(divider);
        System.out.println(title);
        System.out.println(divider);
        System.out.println();

        for (T item : items) {
            System.out.println(item);
            System.out.println();
            System.out.println(divider);
            System.out.println();
        }
    }
}
