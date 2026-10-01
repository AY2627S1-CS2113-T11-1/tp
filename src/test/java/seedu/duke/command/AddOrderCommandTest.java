package seedu.duke.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.duke.Order;
import seedu.duke.OrderList;
import seedu.duke.Product;
import seedu.duke.ProductList;
import seedu.duke.SystemException;
import seedu.duke.Ui;

class AddOrderCommandTest {
    private static final double DELTA = 1e-9;

    private ProductList products;
    private OrderList orders;
    private Ui ui;
    private Product apple;
    private Product carrot;

    /**
     * Stocks the catalogue with 10 apples at $1.50 and 5 carrots at $1.88.
     */
    @BeforeEach
    public void setUp() throws SystemException {
        products = new ProductList();
        orders = new OrderList();
        ui = new Ui(new ByteArrayInputStream(new byte[0]),
                new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8));

        apple = new Product("Apple", 1.50);
        apple.setStock(10);
        carrot = new Product("Carrot", 1.88);
        carrot.setStock(5);
        products.add(apple);
        products.add(carrot);
    }

    @Test
    public void execute_validOrder_deductsStockAndRecordsOrder() throws SystemException {
        orderOf(item("Apple", 3), item("carrot", 1)).execute(products, orders, ui);

        assertEquals(7, apple.getStock());
        assertEquals(4, carrot.getStock());
        Order order = orders.findById(1);
        assertEquals("Jonas Low", order.getCustomer());
        assertEquals(6.38, order.getTotal(), DELTA);
    }

    @Test
    public void execute_quantityEqualToStock_allowed() throws SystemException {
        orderOf(item("Carrot", 5)).execute(products, orders, ui);
        assertEquals(0, carrot.getStock());
    }

    @Test
    public void execute_oneLineExceedsStock_changesNothing() {
        AddOrderCommand command = orderOf(item("Apple", 3), item("Carrot", 6));

        assertThrows(SystemException.class, () -> command.execute(products, orders, ui));
        assertEquals(10, apple.getStock());
        assertEquals(5, carrot.getStock());
        assertEquals(0, orders.size());
    }

    @Test
    public void execute_unknownProduct_changesNothing() {
        AddOrderCommand command = orderOf(item("Apple", 3), item("Durian", 1));

        assertThrows(SystemException.class, () -> command.execute(products, orders, ui));
        assertEquals(10, apple.getStock());
        assertEquals(0, orders.size());
    }

    @Test
    public void execute_twoOrders_getConsecutiveNumbers() throws SystemException {
        orderOf(item("Apple", 1)).execute(products, orders, ui);
        orderOf(item("Apple", 1)).execute(products, orders, ui);

        assertEquals(2, orders.size());
        assertEquals(2, orders.findById(2).getId());
    }

    @Test
    public void execute_priceEditedAfterOrder_orderKeepsPricePaid() throws SystemException {
        orderOf(item("Apple", 2)).execute(products, orders, ui);
        assertEquals(1.50, orders.findById(1).getItems().get(0).getUnitPrice(), DELTA);
    }

    private static AddOrderCommand orderOf(AddOrderCommand.RequestedItem... items) {
        return new AddOrderCommand("Jonas Low", List.of(items));
    }

    private static AddOrderCommand.RequestedItem item(String name, int quantity) {
        return new AddOrderCommand.RequestedItem(name, quantity);
    }
}
