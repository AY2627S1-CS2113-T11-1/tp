package seedu.duke.command;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.duke.Order;
import seedu.duke.OrderItem;
import seedu.duke.OrderList;
import seedu.duke.Product;
import seedu.duke.ProductList;
import seedu.duke.SystemException;
import seedu.duke.Ui;

class EditOrderCommandTest {
    private static final double DELTA = 1e-9;

    private ProductList products;
    private OrderList orders;
    private Ui ui;
    private Product apple;
    private Product carrot;

    /**
     * Creates order 1 for 4 carrots at $1.88 for Jonas Low, leaving 6 carrots and 10 apples ($1.50) in stock.
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
        carrot.setStock(10);
        products.add(apple);
        products.add(carrot);

        new AddOrderCommand("Jonas Low", List.of(new AddOrderCommand.RequestedItem("Carrot", 4)))
                .execute(products, orders, ui);
    }

    @Test
    public void execute_customerAndIncreasedQuantity_updatesOrderAndDeductsExtraStock() throws SystemException {
        new EditOrderCommand(1, "Mei Tan", null, 5).execute(products, orders, ui);

        Order order = orders.findById(1);
        assertEquals(1, order.getId());
        assertEquals("Mei Tan", order.getCustomer());
        assertEquals(5, order.getTotalUnits());
        assertEquals(BigDecimal.valueOf(9.40).setScale(2), order.getTotal());
        assertEquals(5, carrot.getStock());
    }

    @Test
    public void execute_decreasedQuantity_returnsDifferenceToStock() throws SystemException {
        new EditOrderCommand(1, null, null, 1).execute(products, orders, ui);

        assertEquals(1, orders.findById(1).getTotalUnits());
        assertEquals(9, carrot.getStock());
        assertEquals("Jonas Low", orders.findById(1).getCustomer());
    }

    @Test
    public void execute_increaseUsingAllRemainingStock_allowed() throws SystemException {
        new EditOrderCommand(1, null, null, 10).execute(products, orders, ui);

        assertEquals(0, carrot.getStock());
        assertEquals(10, orders.findById(1).getTotalUnits());
    }

    @Test
    public void execute_increaseBeyondStock_changesNothing() {
        EditOrderCommand command = new EditOrderCommand(1, "Mei Tan", null, 11);

        SystemException e = assertThrows(SystemException.class, () -> command.execute(products, orders, ui));
        assertEquals("Unable to edit order: insufficient stock.\n" + EditOrderCommand.NO_CHANGES_MESSAGE,
                e.getMessage());
        assertOrderUnchanged();
        assertEquals(10, apple.getStock());
    }

    @Test
    public void execute_replacementProduct_restoresOldStockAndUsesNewCurrentPrice() throws SystemException {
        new EditOrderCommand(1, null, "apple", 3).execute(products, orders, ui);

        OrderItem line = orders.findById(1).getItems().get(0);
        assertSame(apple, line.getProduct());
        assertEquals(BigDecimal.valueOf(1.50).setScale(2), line.getUnitPrice());
        assertEquals(BigDecimal.valueOf(4.50).setScale(2), orders.findById(1).getTotal());
        assertEquals(10, carrot.getStock());
        assertEquals(7, apple.getStock());
    }

    @Test
    public void execute_replacementProductWithoutQuantity_keepsOriginalQuantity() throws SystemException {
        new EditOrderCommand(1, null, "Apple", null).execute(products, orders, ui);

        assertEquals(4, orders.findById(1).getTotalUnits());
        assertEquals(6, apple.getStock());
        assertEquals(10, carrot.getStock());
    }

    @Test
    public void execute_replacementProductShortOfStock_changesNothing() {
        apple.setStock(2);
        EditOrderCommand command = new EditOrderCommand(1, "Mei Tan", "Apple", null);

        assertThrows(SystemException.class, () -> command.execute(products, orders, ui));
        assertOrderUnchanged();
        assertEquals(2, apple.getStock());
    }

    @Test
    public void execute_replacementNeedsUnitsTheOldProductFrees_countsOnlyReplacementStock() {
        // Restoring 4 carrots must not count towards the apples the new line needs.
        apple.setStock(0);
        EditOrderCommand command = new EditOrderCommand(1, null, "Apple", 1);

        assertThrows(SystemException.class, () -> command.execute(products, orders, ui));
        assertOrderUnchanged();
        assertEquals(0, apple.getStock());
    }

    @Test
    public void execute_sameProductNamedInOtherCase_keepsStoredUnitPrice() throws SystemException {
        // Simulates an order placed when carrots cost $1.50, before the catalogue price became $1.88.
        orders.create("Mei Tan", List.of(new OrderItem(carrot, 2, 1.50)));

        new EditOrderCommand(2, null, "CARROT", 3).execute(products, orders, ui);

        OrderItem line = orders.findById(2).getItems().get(0);
        assertEquals(BigDecimal.valueOf(1.50).setScale(2), line.getUnitPrice());
        assertEquals(BigDecimal.valueOf(4.50).setScale(2), orders.findById(2).getTotal());
        assertEquals(5, carrot.getStock());
    }

    @Test
    public void execute_unknownProduct_changesNothing() {
        EditOrderCommand command = new EditOrderCommand(1, "Mei Tan", "Durian", 1);

        SystemException e = assertThrows(SystemException.class, () -> command.execute(products, orders, ui));
        assertTrue(e.getMessage().contains("Durian"));
        assertOrderUnchanged();
        assertEquals(10, apple.getStock());
    }

    @Test
    public void execute_cancelledOrder_rejectedWithoutTouchingStock() throws SystemException {
        new CancelOrderCommand(1).execute(products, orders, ui);
        EditOrderCommand command = new EditOrderCommand(1, "Mei Tan", null, 2);

        SystemException e = assertThrows(SystemException.class, () -> command.execute(products, orders, ui));
        assertTrue(e.getMessage().contains("cancelled"));
        assertEquals(10, carrot.getStock());
        assertEquals("Jonas Low", orders.findById(1).getCustomer());
    }

    @Test
    public void execute_unknownOrder_throwsSystemException() {
        EditOrderCommand command = new EditOrderCommand(99, "Mei Tan", null, null);
        assertThrows(SystemException.class, () -> command.execute(products, orders, ui));
    }

    @Test
    public void execute_multiProductOrderQuantityChange_rejected() throws SystemException {
        new AddOrderCommand("Mei Tan", List.of(
                new AddOrderCommand.RequestedItem("Apple", 1),
                new AddOrderCommand.RequestedItem("Carrot", 1))).execute(products, orders, ui);
        EditOrderCommand command = new EditOrderCommand(2, null, null, 3);

        assertThrows(SystemException.class, () -> command.execute(products, orders, ui));
        assertEquals(9, apple.getStock());
        assertEquals(5, carrot.getStock());
    }

    @Test
    public void execute_multiProductOrderCustomerOnly_allowed() throws SystemException {
        new AddOrderCommand("Mei Tan", List.of(
                new AddOrderCommand.RequestedItem("Apple", 1),
                new AddOrderCommand.RequestedItem("Carrot", 1))).execute(products, orders, ui);

        new EditOrderCommand(2, "Raj s/o Kumar", null, null).execute(products, orders, ui);

        assertEquals("Raj s/o Kumar", orders.findById(2).getCustomer());
        assertEquals(2, orders.findById(2).getItems().size());
    }

    /**
     * Asserts that order 1 and carrot stock are exactly as {@link #setUp()} left them.
     * Apple stock is checked by each test, since some tests change it on purpose.
     */
    private void assertOrderUnchanged() {
        Order order = orders.findById(1);
        assertEquals("Jonas Low", order.getCustomer());
        assertSame(carrot, order.getItems().get(0).getProduct());
        assertEquals(4, order.getTotalUnits());
        assertEquals(BigDecimal.valueOf(1.88).setScale(2), order.getItems().get(0).getUnitPrice());
        assertEquals(6, carrot.getStock());
    }
}
