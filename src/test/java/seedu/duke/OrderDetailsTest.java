package seedu.duke;

import java.math.BigDecimal;

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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.duke.command.OrderDetailsCommand;

/**
 * Checks customer lookup, input validation, and individual order tables.
 */
class OrderDetailsTest {
    private OrderList orders;
    private ByteArrayOutputStream output;
    private Ui ui;

    /**
     * Creates an empty history and captures UI output for each test.
     */
    @BeforeEach
    public void setUp() {
        orders = new OrderList();
        output = new ByteArrayOutputStream();
        ui = new Ui(new ByteArrayInputStream(new byte[0]),
                new PrintStream(output, true, StandardCharsets.UTF_8));
    }

    @Test
    public void parse_validCommand_acceptsMixedCaseAndSpaces() throws SystemException {
        assertInstanceOf(OrderDetailsCommand.class,
                new Parser().parse("  OrDeR   DeTaIlS   c/You Jie  "));
    }

    @Test
    public void parse_invalidFields_reportsFormat() {
        for (String arguments : List.of("", "c/", "c/   ", "You Jie", "a/20", "name c/You Jie",
                "c/You Jie c/Alice", "c/You Jie p/Apple", "c/You Jie q/1", "c/You Jie a/20")) {
            SystemException error = assertThrows(SystemException.class,
                    () -> new Parser().parse("order details " + arguments));
            assertTrue(error.getMessage().contains("Format: order details c/CUSTOMER"));
        }
    }

    @Test
    public void execute_twoProducts_printsAlignedTableAndTotals() throws SystemException {
        orders.create("YouJie", List.of(new OrderItem(new Product("BigMac", 4), 5),
                new OrderItem(new Product("Lettuce", 10), 3)));
        runDetails("YouJie");
        assertEquals(List.of(
                "    Here are YouJie's order details:",
                "    Order 1 (Active):",
                "    +---------+---------+--------+",
                "    | Product |     Qty |   Cost |",
                "    +---------+---------+--------+",
                "    | BigMac  |       5 | $20.00 |",
                "    | Lettuce |       3 | $30.00 |",
                "    +---------+---------+--------+",
                "    | Total   | 8 items | $50.00 |",
                "    +---------+---------+--------+"), output.toString(StandardCharsets.UTF_8).lines().toList());
    }

    @Test
    public void execute_multipleOrders_includesCancelledOrdersAndExcludesOtherCustomers() throws SystemException {
        Order first = orders.create("You Jie", List.of(new OrderItem(new Product("Apple", 2), 1)));
        orders.create("You", List.of(new OrderItem(new Product("Other", 100), 1)));
        Order cancelled = orders.create("YOU JIE", List.of(new OrderItem(new Product("Carrot", 3), 2)));
        cancelled.cancel();
        runDetails("you jie");
        String text = output.toString(StandardCharsets.UTF_8);
        assertTrue(text.contains("Order 1 (Active):"));
        assertTrue(text.contains("Order 3 (Cancelled):"));
        assertTrue(text.indexOf("Order 1") < text.indexOf("Order 3"));
        assertTrue(text.contains("$2.00"));
        assertTrue(text.contains("$6.00"));
        assertTrue(text.contains("1 item"));
        assertFalse(text.contains("Order 2"));
        assertFalse(text.contains("Other"));
        assertFalse(first.isCancelled());
        assertTrue(cancelled.isCancelled());
        assertEquals(3, orders.size());
    }

    @Test
    public void execute_missingCustomer_displaysHelpfulMessage() throws SystemException {
        orders.create("You Jie", List.of());
        runDetails("You");
        assertEquals("    No orders found for You." + System.lineSeparator(),
                output.toString(StandardCharsets.UTF_8));
    }

    @Test
    public void execute_emptyHistory_displaysHelpfulMessage() throws SystemException {
        runDetails("You Jie");
        assertEquals("    No orders found for You Jie." + System.lineSeparator(),
                output.toString(StandardCharsets.UTF_8));
    }

    @Test
    public void execute_longProductName_keepsTableAligned() throws SystemException {
        orders.create("You Jie", List.of(new OrderItem(new Product("Organic Coffee Beans", 12), 10)));
        runDetails("You Jie");
        List<String> lines = output.toString(StandardCharsets.UTF_8).lines().toList();
        assertTrue(lines.get(5).contains("Organic Coffee Beans"));
        for (String line : lines.subList(2, lines.size())) {
            assertEquals(lines.get(2).length(), line.length());
        }
    }

    @Test
    public void execute_repeatedLookup_keepsStockAndOrderContents() throws SystemException {
        Product product = new Product("Apple", 1.25);
        product.setStock(10);
        Order order = orders.create("You Jie", List.of(new OrderItem(product, 2)));
        runDetails("You Jie");
        String firstOutput = output.toString(StandardCharsets.UTF_8);
        output.reset();
        runDetails("You Jie");
        assertEquals(firstOutput, output.toString(StandardCharsets.UTF_8));
        assertEquals(10, product.getStock());
        assertEquals(2, order.getItems().get(0).getQuantity());
        assertEquals(BigDecimal.valueOf(2.50).setScale(2), order.getTotal());
    }

    /**
     * Runs the command through the application's parser and execution interface.
     */
    private void runDetails(String customer) throws SystemException {
        new Parser().parse("order details c/" + customer)
                .execute(new ProductList(), orders, new ExpenseList(), new StockList(), ui);
    }
}
