package seedu.duke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.duke.command.GenerateReceiptsCommand;

/**
 * Exercises receipt parsing and output through the same parser and UI used by the application.
 */
class GenerateReceiptsTest {
    @Test
    public void parse_validCommand_acceptsSpacesAndCommandCase() throws SystemException {
        assertInstanceOf(GenerateReceiptsCommand.class,
                new Parser().parse("  GENERATE   RECEIPTS   c/Jonas Low  "));
    }

    @Test
    public void parse_invalidCommand_reportsExpectedFormat() {
        for (String input : List.of("generate", "generate receipt c/Jonas", "generate receipts",
                "generate receipts Jonas", "generate receipts c/", "generate receipts c/   ",
                "generate receipts c/Jonas c/Alice", "generate receipts c/Jonas p/Apple")) {
            SystemException error = assertThrows(SystemException.class, () -> new Parser().parse(input));
            assertTrue(error.getMessage().contains("Format: generate receipts c/CUSTOMER"));
        }
    }

    @Test
    public void execute_multipleOrders_reusesOrderTablesAndCanBeRepeated() throws SystemException {
        Product apple = new Product("Apple", 1.50);
        apple.setStock(10);
        OrderList orders = new OrderList();
        Order first = orders.create("Jonas Low", List.of(new OrderItem(apple, 2)));
        orders.create("Jonas Low", List.of(new OrderItem(apple, 1))).cancel();
        orders.create("Alice", List.of(new OrderItem(apple, 1)));
        Order last = orders.create("Jonas Low", List.of(new OrderItem(apple, 3)));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = uiFor(output);

        new Parser().parse("generate receipts c/Jonas Low").execute(new ProductList(), orders, ui);
        String receipts = output.toString(StandardCharsets.UTF_8);
        String expected = "Receipts for Jonas Low (2):" + System.lineSeparator();
        for (Order order : List.of(first, last)) {
            ByteArrayOutputStream existingOutput = new ByteArrayOutputStream();
            uiFor(existingOutput).showOrderCreated(order);
            String existing = existingOutput.toString(StandardCharsets.UTF_8);
            String table = existing.substring(existing.indexOf(System.lineSeparator())
                    + System.lineSeparator().length());
            expected += "Receipt for order " + order.getId() + " - Jonas Low" + System.lineSeparator() + table;
        }
        assertEquals(expected, receipts);
        assertTrue(receipts.contains("$3.00"));
        assertTrue(receipts.contains("$4.50"));
        assertFalse(receipts.contains("Alice"));
        assertFalse(receipts.contains("Receipt for order 2"));
        output.reset();
        new Parser().parse("generate receipts c/Jonas Low").execute(new ProductList(), orders, ui);
        assertEquals(receipts, output.toString(StandardCharsets.UTF_8));
        assertEquals(10, apple.getStock());
        assertEquals(4, orders.size());
    }

    @Test
    public void execute_cancelledCustomer_displaysNoEligibleOrders() throws SystemException {
        OrderList orders = new OrderList();
        orders.create("Jonas", List.of()).cancel();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        new Parser().parse("generate receipts c/Jonas").execute(new ProductList(), orders, uiFor(output));
        assertTrue(output.toString(StandardCharsets.UTF_8)
                .contains("No paid, non-cancelled orders found for this customer."));
    }

    /**
     * Creates a UI whose output can be compared with the existing order table format.
     */
    private Ui uiFor(ByteArrayOutputStream output) {
        return new Ui(new ByteArrayInputStream(new byte[0]),
                new PrintStream(output, true, StandardCharsets.UTF_8));
    }
}
