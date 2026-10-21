package seedu.duke.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.duke.Order;
import seedu.duke.OrderItem;
import seedu.duke.OrderList;
import seedu.duke.Product;
import seedu.duke.ProductList;
import seedu.duke.SystemException;
import seedu.duke.Ui;

/**
 * Checks the sales command's visible totals and its handling of cancelled orders.
 */
class ListSalesCommandTest {
    @Test
    public void execute_noOrders_displaysEmptySalesAndZeroTotal() throws SystemException {
        String output = executeSales(new OrderList());

        assertEquals("    Sales (0):" + System.lineSeparator()
                + "    Nothing here yet. Confirm an order with: order add c/CUSTOMER p/PRODUCT q/QUANTITY"
                + System.lineSeparator() + "    Total sales: $0.00" + System.lineSeparator(), output);
    }

    @Test
    public void execute_mixedOrders_totalsOnlyConfirmedSales() throws SystemException {
        Product apple = new Product("Apple", 1.50);
        Product pear = new Product("Pear", 2.25);
        OrderList orders = new OrderList();
        orders.create("Alice", List.of(new OrderItem(apple, 2), new OrderItem(pear, 1)));
        orders.create("Cancelled Customer", List.of(new OrderItem(pear, 10))).cancel();
        orders.create("Bob", List.of(new OrderItem(apple, 1)));

        String output = executeSales(orders);

        assertTrue(output.contains("Sales (2):"));
        assertTrue(output.contains("Alice"));
        assertTrue(output.contains("Bob"));
        assertTrue(output.contains("$5.25"));
        assertTrue(output.contains("Total sales: $6.75"));
        assertFalse(output.contains("Cancelled Customer"));
        assertTrue(output.indexOf("Alice") < output.indexOf("Bob"));
        assertEquals(3, orders.size());
    }

    @Test
    public void execute_orderCancelledBetweenRuns_updatesSalesTotal() throws SystemException {
        OrderList orders = new OrderList();
        Order order = orders.create("Alice", List.of(new OrderItem(new Product("Apple", 1.50), 2)));

        assertTrue(executeSales(orders).contains("Total sales: $3.00"));
        order.cancel();
        String output = executeSales(orders);

        assertTrue(output.contains("Sales (0):"));
        assertTrue(output.contains("Total sales: $0.00"));
        assertFalse(output.contains("Alice"));
        assertEquals(1, orders.size());
    }

    /**
     * Runs the command with captured output so tests do not depend on the real console.
     */
    private String executeSales(OrderList orders) throws SystemException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(new ByteArrayInputStream(new byte[0]),
                new PrintStream(output, true, StandardCharsets.UTF_8));
        new ListSalesCommand().execute(new ProductList(), orders, ui);
        return output.toString(StandardCharsets.UTF_8);
    }
}
