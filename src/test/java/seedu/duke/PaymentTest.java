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
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.duke.command.AddOrderCommand;
import seedu.duke.command.AddPaymentCommand;

/**
 * Tests payment parsing, order state changes, and the displayed change.
 */
class PaymentTest {
    private OrderList orders;
    private ProductList products;
    private ByteArrayOutputStream output;
    private Ui ui;

    /**
     * Creates a separate order history and output stream for each test.
     */
    @BeforeEach
    public void setUp() {
        orders = new OrderList();
        products = new ProductList();
        output = new ByteArrayOutputStream();
        ui = new Ui(new ByteArrayInputStream(new byte[0]),
                new PrintStream(output, true, StandardCharsets.UTF_8));
    }

    @Test
    public void parse_validPayments_acceptsIntegersDecimalsAndSpacing() throws SystemException {
        for (String amount : List.of("0", "20", "20.5", "20.50", "00.10")) {
            assertInstanceOf(AddPaymentCommand.class,
                    new Parser().parse("  ORDER  ADD  PAY   c/You Jie  a/" + amount + "  "));
        }
        assertInstanceOf(AddOrderCommand.class, new Parser().parse("order add c/Pay p/Apple q/1"));
    }

    @Test
    public void parse_invalidPayments_rejectsBadOrRepeatedFields() {
        for (String fields : List.of("", "c/You Jie", "a/20", "a/20 c/You Jie", "c/ a/20",
                "c/You Jie a/", "c/You Jie a/-1", "c/You Jie a/1.001", "c/You Jie a/NaN",
                "c/You Jie a/Infinity", "c/You Jie a/1e2", "c/You Jie a/0x10", "c/You Jie a/+2",
                "c/You Jie a/1,50", "c/You Jie a/.50", "c/You Jie a/2.", "c/You Jie a/twenty",
                "c/You Jie c/Alice a/20", "c/You Jie p/Apple a/20", "c/You Jie a/20 a/30",
                "c/You Jie a/20 extra", "You Jie a/20")) {
            SystemException error = assertThrows(SystemException.class,
                    () -> new Parser().parse("order add pay " + fields), fields);
            assertTrue(error.getMessage().contains("Format: order add pay c/CUSTOMER a/AMOUNT"), fields);
        }
    }

    @Test
    public void execute_exactPayment_marksOrderPaidAndReturnsZeroChange() throws SystemException {
        Order order = addOrder("You Jie", 20.40, 1);
        pay("You Jie", "20.40");
        assertTrue(order.isPaid());
        assertEquals("    You Jie has paid $20.40! The change to return is: $0.00."
                + System.lineSeparator(), output.toString(StandardCharsets.UTF_8));
    }

    @Test
    public void execute_excessPayment_printsExactChange() throws SystemException {
        addOrder("BigChungus", 20.40, 1);
        pay("BigChungus", "20.50");
        assertEquals("    BigChungus has paid $20.50! The change to return is: $0.10."
                + System.lineSeparator(), output.toString(StandardCharsets.UTF_8));
    }

    @Test
    public void execute_multipleOrders_paysOnlyMatchingUnpaidActiveOrders() throws SystemException {
        Order first = addOrder("You Jie", 0.10, 3);
        Order second = addOrder("YOU JIE", 0.20, 1);
        Order cancelled = addOrder("You Jie", 100, 1);
        cancelled.cancel();
        Order alreadyPaid = addOrder("You Jie", 10, 1);
        alreadyPaid.markPaid();
        Order other = addOrder("You", 4, 1);

        pay("you jie", "0.50");

        assertTrue(first.isPaid());
        assertTrue(second.isPaid());
        assertFalse(cancelled.isPaid());
        assertTrue(alreadyPaid.isPaid());
        assertFalse(other.isPaid());
        assertTrue(output.toString(StandardCharsets.UTF_8).contains("$0.00"));
        assertEquals(5, orders.size());
    }

    @Test
    public void execute_insufficientPayment_keepsEveryOrderUnpaid() throws SystemException {
        Order first = addOrder("You Jie", 4, 1);
        Order second = addOrder("You Jie", 6, 1);
        SystemException error = assertThrows(SystemException.class, () -> pay("You Jie", "5"));
        assertEquals("Insufficient payment. Customer You Jie still needs to pay $5.00.", error.getMessage());
        assertFalse(first.isPaid());
        assertFalse(second.isPaid());
        assertEquals("", output.toString(StandardCharsets.UTF_8));
    }

    @Test
    public void execute_unknownCustomer_reportsUnregisteredCustomer() throws SystemException {
        addOrder("You Jie", 1, 1);
        SystemException error = assertThrows(SystemException.class, () -> pay("You", "20"));
        ui.showError(error.getMessage());
        assertEquals("    Sorry! Customer You is not a registered customer." + System.lineSeparator(),
                output.toString(StandardCharsets.UTF_8));
    }

    @Test
    public void execute_emptyHistory_reportsUnregisteredCustomer() {
        SystemException error = assertThrows(SystemException.class, () -> pay("You Jie", "20"));
        assertTrue(error.getMessage().contains("not a registered customer"));
    }

    @Test
    public void execute_secondPayment_isNotAcceptedAgain() throws SystemException {
        Order first = addOrder("You Jie", 1, 1);
        pay("You Jie", "1");
        output.reset();
        SystemException error = assertThrows(SystemException.class, () -> pay("You Jie", "1"));
        assertTrue(error.getMessage().contains("no unpaid, non-cancelled orders"));
        assertEquals("", output.toString(StandardCharsets.UTF_8));
        Order later = addOrder("You Jie", 2, 1);
        pay("You Jie", "2");
        assertTrue(first.isPaid());
        assertTrue(later.isPaid());
    }

    @Test
    public void execute_onlyCancelledOrders_reportsNoOutstandingOrders() throws SystemException {
        Order order = addOrder("You Jie", 1, 1);
        order.cancel();
        SystemException error = assertThrows(SystemException.class, () -> pay("You Jie", "1"));
        assertTrue(error.getMessage().contains("no unpaid, non-cancelled orders"));
        assertFalse(order.isPaid());
    }

    @Test
    public void execute_zeroPayment_acceptsFreeOrderOnly() throws SystemException {
        Order free = addOrder("You Jie", 0, 1);
        pay("You Jie", "0");
        assertTrue(free.isPaid());
        Order charged = addOrder("You Jie", 1, 1);
        assertThrows(SystemException.class, () -> pay("You Jie", "0"));
        assertFalse(charged.isPaid());
    }

    @Test
    public void execute_payment_doesNotChangeStockOrOrderContents() throws SystemException {
        Product product = new Product("Apple", 1.25);
        product.setStock(10);
        Order order = orders.create("You Jie", List.of(new OrderItem(product, 2)));
        pay("You Jie", "3");
        assertEquals(10, product.getStock());
        assertEquals(2, order.getItems().get(0).getQuantity());
        assertEquals(2.50, order.getTotal());
        assertFalse(order.isCancelled());
    }

    @Test
    public void execute_differentLocale_keepsDecimalPoint() throws SystemException {
        Locale previous = Locale.getDefault();
        try {
            Locale.setDefault(Locale.GERMANY);
            addOrder("You Jie", 0.10, 3);
            pay("You Jie", "0.50");
            assertTrue(output.toString(StandardCharsets.UTF_8).contains("$0.20"));
        } finally {
            Locale.setDefault(previous);
        }
    }

    @Test
    public void markPaid_cancelledOrPaidOrder_rejectsInvalidTransition() throws SystemException {
        Order order = addOrder("You Jie", 1, 1);
        assertFalse(order.isPaid());
        order.markPaid();
        assertThrows(SystemException.class, order::markPaid);
        Order cancelled = addOrder("You Jie", 1, 1);
        cancelled.cancel();
        assertThrows(SystemException.class, cancelled::markPaid);
    }

    /**
     * Creates an order with one product line at the supplied price.
     */
    private Order addOrder(String customer, double price, int quantity) {
        return orders.create(customer, List.of(new OrderItem(new Product("Apple", price), quantity)));
    }

    /**
     * Runs payment through the application's parser and command interface.
     */
    private void pay(String customer, String amount) throws SystemException {
        new Parser().parse("order add pay c/" + customer + " a/" + amount)
                .execute(products, orders, new ExpenseList(), new StockList(), ui);
    }
}
