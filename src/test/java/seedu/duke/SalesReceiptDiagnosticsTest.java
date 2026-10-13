package seedu.duke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

import seedu.duke.command.GenerateReceiptsCommand;
import seedu.duke.command.ListSalesCommand;

/**
 * Verifies internal contracts and diagnostic events for sales listing and receipt generation.
 */
class SalesReceiptDiagnosticsTest {
    @Test
    public void receipts_invalidInternalArguments_failAssertions() {
        assertThrows(AssertionError.class, () -> new GenerateReceiptsCommand(null));
        assertThrows(AssertionError.class, () -> new GenerateReceiptsCommand("  "));
        assertThrows(AssertionError.class, () -> new ReceiptGenerator().generate(null, "Alice"));
        assertThrows(AssertionError.class, () -> new ReceiptGenerator().generate(new OrderList(), ""));
    }

    @Test
    public void receipts_knownAndUnknownCustomers_logCountsWithoutNames() throws SystemException {
        OrderList orders = new OrderList();
        orders.create("Alice", List.of());
        orders.create("Alice", List.of()).cancel();
        try (CapturedLogs logs = new CapturedLogs(ReceiptGenerator.class)) {
            assertEquals(1, new ReceiptGenerator().generate(orders, "Alice").size());
            assertThrows(SystemException.class, () -> new ReceiptGenerator().generate(orders, "Bob"));
            assertEquals(List.of("Generated 1 receipt(s)",
                    "Receipt generation rejected: customer not found in order history"), logs.messages);
        }
    }

    @Test
    public void sales_confirmedOrders_logCountAndKeepUiOutput() throws SystemException {
        OrderList orders = new OrderList();
        orders.create("Alice", List.of());
        orders.create("Bob", List.of()).cancel();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(new ByteArrayInputStream(new byte[0]),
                new PrintStream(output, true, StandardCharsets.UTF_8));
        try (CapturedLogs logs = new CapturedLogs(ListSalesCommand.class)) {
            new ListSalesCommand().execute(new ProductList(), orders, new ExpenseList(), new StockList(), ui);
            assertEquals(List.of("Displayed 1 confirmed sale(s)"), logs.messages);
            assertTrue(output.toString(StandardCharsets.UTF_8).startsWith("    Sales (1):"));
        }
    }

    @Test
    public void sales_cancelledOrderReturnedBySource_failsContract() throws SystemException {
        Order cancelled = new Order(1, "Alice", List.of());
        cancelled.cancel();
        OrderList invalidSource = new OrderList() {
            @Override
            public List<Order> getConfirmedSales() {
                return List.of(cancelled);
            }
        };
        assertThrows(AssertionError.class, () -> new ListSalesCommand().execute(
                new ProductList(), invalidSource, new ExpenseList(), new StockList(), new Ui()));
    }

    /**
     * Captures a feature's diagnostic messages and restores its logger configuration after each test.
     */
    private static class CapturedLogs extends Handler implements AutoCloseable {
        private final List<String> messages = new ArrayList<>();
        private final Logger logger;
        private final Level previousLevel;
        private final boolean previousParentHandlers;

        CapturedLogs(Class<?> source) {
            logger = Logger.getLogger(source.getName());
            previousLevel = logger.getLevel();
            previousParentHandlers = logger.getUseParentHandlers();
            setLevel(Level.ALL);
            logger.setLevel(Level.FINE);
            logger.setUseParentHandlers(false);
            logger.addHandler(this);
        }

        @Override
        public void publish(LogRecord record) {
            messages.add(record.getMessage());
        }

        @Override
        public void flush() {
            // Messages are captured in memory immediately.
        }

        @Override
        public void close() {
            logger.removeHandler(this);
            logger.setLevel(previousLevel);
            logger.setUseParentHandlers(previousParentHandlers);
        }
    }
}
