package seedu.duke.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.duke.ExpenseList;
import seedu.duke.Order;
import seedu.duke.OrderItem;
import seedu.duke.OrderList;
import seedu.duke.Product;
import seedu.duke.ProductList;
import seedu.duke.Stock;
import seedu.duke.StockList;
import seedu.duke.SystemException;
import seedu.duke.Ui;

class DeleteProductCommandTest {
    private ProductList products;
    private OrderList orders;
    private ExpenseList expenses;
    private StockList stocks;
    private Ui ui;
    private Product oatMilk;

    @BeforeEach
    public void setUp() throws SystemException {
        products = new ProductList();
        orders = new OrderList();
        expenses = new ExpenseList();
        stocks = new StockList();
        ui = new Ui(new ByteArrayInputStream(new byte[0]),
                new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8));
        oatMilk = new Product("Oat Milk", 3.50);
        products.add(oatMilk);
    }

    @Test
    public void execute_productWithNoOrders_removesProduct() throws SystemException {
        new DeleteProductCommand("Oat Milk").execute(products, orders, expenses, stocks, ui);

        assertNull(products.findByName("Oat Milk"));
        assertEquals(0, products.size());
    }

    @Test
    public void execute_differentCase_removesProduct() throws SystemException {
        new DeleteProductCommand("OAT MILK").execute(products, orders, expenses, stocks, ui);

        assertNull(products.findByName("Oat Milk"));
    }

    @Test
    public void execute_productWithStock_removesStockEntry() throws SystemException {
        stocks.add(new Stock(oatMilk, 20));

        new DeleteProductCommand("Oat Milk").execute(products, orders, expenses, stocks, ui);

        assertNull(stocks.findByProduct("Oat Milk"));
        assertTrue(stocks.isEmpty());
    }

    @Test
    public void execute_unknownProduct_throwsSystemExceptionAndKeepsCatalogue() {
        SystemException exception = assertThrows(SystemException.class,
                () -> new DeleteProductCommand("Coffee Beans").execute(products, orders, expenses, stocks, ui));

        assertEquals("There is no product named \"Coffee Beans\" in the catalogue.", exception.getMessage());
        assertEquals(1, products.size());
    }

    @Test
    public void execute_productInActiveOrder_throwsSystemExceptionAndKeepsProductAndStock() {
        stocks.add(new Stock(oatMilk, 5));
        Order order = orders.create("Mei Tan", List.of(new OrderItem(oatMilk, 2)));

        SystemException exception = assertThrows(SystemException.class,
                () -> new DeleteProductCommand("Oat Milk").execute(products, orders, expenses, stocks, ui));

        assertEquals("Can't delete Oat Milk, it's still in active order " + order.getId()
                + ". Cancel the order first.", exception.getMessage());
        assertNotNull(products.findByName("Oat Milk"));
        assertNotNull(stocks.findByProduct("Oat Milk"));
    }

    @Test
    public void execute_productOnlyInCancelledOrder_removesProductAndKeepsOrderHistory() throws SystemException {
        Order order = orders.create("Mei Tan", List.of(new OrderItem(oatMilk, 2)));
        order.cancel();

        new DeleteProductCommand("Oat Milk").execute(products, orders, expenses, stocks, ui);

        assertNull(products.findByName("Oat Milk"));
        assertEquals("Oat Milk", order.getItems().get(0).getProduct().getName());
    }

    @Test
    public void isExit_deleteCommand_returnsFalse() {
        assertEquals(false, new DeleteProductCommand("Oat Milk").isExit());
    }
}
