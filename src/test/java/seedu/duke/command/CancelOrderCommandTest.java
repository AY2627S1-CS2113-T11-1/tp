package seedu.duke.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.duke.OrderList;
import seedu.duke.Product;
import seedu.duke.ProductList;
import seedu.duke.SystemException;
import seedu.duke.Ui;

class CancelOrderCommandTest {
    private ProductList products;
    private OrderList orders;
    private Ui ui;
    private Product apple;
    private Product carrot;

    /**
     * Creates order 1 for 3 apples and 1 carrot, leaving 7 apples and 4 carrots in stock.
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

        new AddOrderCommand("Jonas Low", List.of(
                new AddOrderCommand.RequestedItem("Apple", 3),
                new AddOrderCommand.RequestedItem("Carrot", 1))).execute(products, orders, ui);
    }

    @Test
    public void execute_activeOrder_restoresStockAndMarksCancelled() throws SystemException {
        new CancelOrderCommand(1).execute(products, orders, ui);

        assertEquals(10, apple.getStock());
        assertEquals(5, carrot.getStock());
        assertTrue(orders.findById(1).isCancelled());
    }

    @Test
    public void execute_alreadyCancelled_throwsWithoutRestoringTwice() throws SystemException {
        new CancelOrderCommand(1).execute(products, orders, ui);

        CancelOrderCommand again = new CancelOrderCommand(1);
        assertThrows(SystemException.class, () -> again.execute(products, orders, ui));
        assertEquals(10, apple.getStock());
    }

    @Test
    public void execute_unknownOrder_throwsSystemException() {
        CancelOrderCommand command = new CancelOrderCommand(99);
        assertThrows(SystemException.class, () -> command.execute(products, orders, ui));
    }

    @Test
    public void execute_cancelledOrder_keepsItsNumber() throws SystemException {
        new CancelOrderCommand(1).execute(products, orders, ui);
        new AddOrderCommand("Jonas Low", List.of(
                new AddOrderCommand.RequestedItem("Apple", 1))).execute(products, orders, ui);

        assertEquals(2, orders.findById(2).getId());
        assertEquals(2, orders.size());
    }
}
