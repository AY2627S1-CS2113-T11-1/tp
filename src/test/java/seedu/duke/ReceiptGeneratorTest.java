package seedu.duke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Checks receipt eligibility and distinguishes unknown customers from cancelled purchases.
 */
class ReceiptGeneratorTest {
    @Test
    public void generate_matchingCustomer_returnsOnlyTheirConfirmedOrders() throws SystemException {
        OrderList orders = new OrderList();
        Order first = orders.create("Jonas Low", List.of());
        Order cancelled = orders.create("Jonas Low", List.of());
        cancelled.cancel();
        orders.create("Jonas", List.of());
        Order last = orders.create("Jonas Low", List.of());

        assertEquals(List.of(first, last), new ReceiptGenerator().generate(orders, "  JONAS LOW  "));
        assertEquals(4, orders.size());
        assertTrue(cancelled.isCancelled());
    }

    @Test
    public void generate_onlyCancelledOrders_returnsNoReceipts() throws SystemException {
        OrderList orders = new OrderList();
        orders.create("Jonas Low", List.of()).cancel();

        assertTrue(new ReceiptGenerator().generate(orders, "Jonas Low").isEmpty());
    }

    @Test
    public void generate_unknownCustomer_reportsMissingCustomer() {
        OrderList orders = new OrderList();
        orders.create("Jonas Low", List.of());

        SystemException error = assertThrows(SystemException.class,
                () -> new ReceiptGenerator().generate(orders, "Jonas"));
        assertEquals("Customer not in database.", error.getMessage());
        assertThrows(SystemException.class, () -> new ReceiptGenerator().generate(new OrderList(), "Jonas"));
    }
}
