package seedu.duke.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
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

class DiscountOrderCommandTest {
    private static final double DELTA = 1e-9;

    private ProductList products;
    private OrderList orders;
    private Ui ui;
    private Product carrot;

    /**
     * Creates order 1 for 5 carrots at $1.88 (subtotal $9.40), leaving 15 carrots in stock.
     */
    @BeforeEach
    public void setUp() throws SystemException {
        products = new ProductList();
        orders = new OrderList();
        ui = new Ui(new ByteArrayInputStream(new byte[0]),
                new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8));

        carrot = new Product("Carrot", 1.88);
        carrot.setStock(20);
        products.add(carrot);

        new AddOrderCommand("Mei Tan", List.of(new AddOrderCommand
                .RequestedItem("Carrot", 5)))
                .execute(products, orders, ui);
    }

    @Test
    public void execute_tenPercent_reducesTotalOnly() throws SystemException {
        discount(1, "10");

        Order order = orders.findById(1);
        assertEquals(BigDecimal.valueOf(9.40).setScale(2), order.getSubtotal());
        assertEquals(BigDecimal.valueOf(0.94).setScale(2), order.getDiscountAmount());
        assertEquals(BigDecimal.valueOf(8.46).setScale(2), order.getTotal());
        assertEquals(15, carrot.getStock());
        assertEquals(BigDecimal.valueOf(1.88).setScale(2), order.getItems().get(0).getUnitPrice());
    }

    @Test
    public void execute_zeroPercent_removesDiscount() throws SystemException {
        discount(1, "10");
        discount(1, "0");

        Order order = orders.findById(1);
        assertFalse(order.hasDiscount());
        assertEquals(BigDecimal.ZERO.setScale(2), order.getDiscountAmount());
        assertEquals(BigDecimal.valueOf(9.40).setScale(2), order.getTotal());
    }

    @Test
    public void execute_secondDiscount_replacesRatherThanAdds() throws SystemException {
        discount(1, "10");
        discount(1, "5");

        assertEquals(BigDecimal.valueOf(0.47).setScale(2), orders.findById(1).getDiscountAmount());
        assertEquals(BigDecimal.valueOf(8.93).setScale(2), orders.findById(1).getTotal());
    }

    @Test
    public void execute_halfCentDiscount_roundsUp() throws SystemException {
        // 12.5% of $9.40 is exactly $1.175, which must round up to $1.18. With doubles it would round down.
        discount(1, "12.5");

        assertEquals(BigDecimal.valueOf(1.18).setScale(2), orders.findById(1).getDiscountAmount());
        assertEquals(BigDecimal.valueOf(8.22).setScale(2), orders.findById(1).getTotal());
    }

    @Test
    public void execute_belowHalfCent_roundsDown() throws SystemException {
        // 0.01% of $9.40 is $0.00094, which rounds down to $0.00.
        discount(1, "0.01");

        assertEquals(BigDecimal.ZERO.setScale(2), orders.findById(1).getDiscountAmount());
        assertEquals(BigDecimal.valueOf(9.40).setScale(2), orders.findById(1).getTotal());
    }

    @Test
    public void execute_hundredPercent_makesTotalZero() throws SystemException {
        discount(1, "100");
        assertEquals(BigDecimal.ZERO.setScale(2), orders.findById(1).getTotal());
    }

    @Test
    public void execute_orderEditedAfterDiscount_samePercentageAppliesToNewSubtotal()
            throws SystemException {
        discount(1, "10");
        new EditOrderCommand(1, null, null, 10)
                .execute(products, orders, ui);

        Order order = orders.findById(1);
        assertEquals(new BigDecimal("10"), order.getDiscountPercent());
        assertEquals(BigDecimal.valueOf(18.80).setScale(2), order.getSubtotal());
        assertEquals(BigDecimal.valueOf(1.88).setScale(2), order.getDiscountAmount());
        assertEquals(BigDecimal.valueOf(16.92).setScale(2), order.getTotal());
    }

    @Test
    public void execute_cancelledOrder_rejectedAndUnchanged() throws SystemException {
        new CancelOrderCommand(1).execute(products, orders, ui);
        DiscountOrderCommand command = new DiscountOrderCommand(1, new BigDecimal("10"));

        SystemException e = assertThrows(SystemException.class, ()
                -> command.execute(products, orders, ui));
        assertTrue(e.getMessage().endsWith(DiscountOrderCommand.NO_CHANGES_MESSAGE));
        assertFalse(orders.findById(1).hasDiscount());
    }

    @Test
    public void execute_unknownOrder_throwsSystemException() {
        DiscountOrderCommand command = new DiscountOrderCommand(9, new BigDecimal("10"));
        assertThrows(SystemException.class, () -> command.execute(products, orders, ui));
    }

    @Test
    public void execute_discountedOrderCancelled_excludesDiscountedTotalFromSales()
            throws SystemException {
        discount(1, "10");
        new CancelOrderCommand(1).execute(products, orders, ui);

        assertEquals(20, carrot.getStock());
        assertTrue(orders.getConfirmedSales().isEmpty());
    }

    private void discount(int orderId, String percent) throws SystemException {
        new DiscountOrderCommand(orderId, new BigDecimal(percent)).execute(products, orders, ui);
    }
}
