package io;

import model.transaction.Transaction;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.format.DateTimeFormatter;

public class TransactionReportIO {
    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final Path FILE = Paths.get("reports", "transactions.txt");

    private TransactionReportIO() {
        throw new IllegalStateException("'TransactionReportIO' cannot be instantiated.");
    }

    public static synchronized void appendTransaction(Transaction t) {
        try {
            ensureDir(FILE.getParent());

            try (
                    BufferedWriter out = Files.newBufferedWriter(
                            FILE,
                            StandardCharsets.UTF_8,
                            StandardOpenOption.CREATE,
                            StandardOpenOption.APPEND
                    )
            ) {
                String createdAt = t.getCreatedAt() == null ? "" : t.getCreatedAt().format(ISO);
                Long accountId = t.getAccount() == null ? null : t.getAccount().getId();
                String type = t.getType() == null ? "" : t.getType().name();
                BigDecimal amount = t.getAmount() == null ? null : t.getAmount().getAmount();
                String currency = t.getAmount() == null ? "" : t.getAmount().getCurrency();
                BigDecimal balanceAfter = t.getBalanceAfter() == null ? null : t.getBalanceAfter().getAmount();
                String corr = t.getCorrelationId() == null ? "" : t.getCorrelationId();
                String note = t.getNarrative() == null ? "" : t.getNarrative();

                String line = String.format(
                        "[%s] Account=%s | Type=%s | Amount=%s %s | BalanceAfter=%s | CorrId=%s | Note=%s",
                        createdAt,
                        toGoodString(accountId),
                        toGoodString(type),
                        toGoodString(amount),
                        toGoodString(currency),
                        toGoodString(balanceAfter),
                        corr,
                        note
                );

                out.write(line);
                out.newLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to append transaction to " + FILE, e);
        }
    }

    private static void ensureDir(Path dir) throws IOException {
        if (dir != null) Files.createDirectories(dir);
    }

    private static String toGoodString(Object o) {
        return o == null ? "" : String.valueOf(o);
    }
}
