package seedu.duke;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import seedu.duke.command.AddStockCommand;
import seedu.duke.command.ListStockCommand;
import seedu.duke.command.LowStockCommand;
import seedu.duke.command.RemoveStockCommand;

class StockCommandTest {

    @Test
    public void execute_addNewStock_success() throws SystemException {
        StockList stocks = new StockList();
        ProductList products = new ProductList();
        products.add(new Product("Apple", 2.00));
        AddStockCommand command = new AddStockCommand("Apple", 10);

        command.execute(products, new OrderList(), new ExpenseList(), stocks, new Ui());

        assertEquals(1, stocks.getStocks().size());
        assertEquals("Apple", stocks.getStocks().get(0).product);
        assertEquals(10, stocks.getStocks().get(0).quantity);
        assertEquals(BigDecimal.valueOf(2.00).setScale(2), stocks.getStocks().get(0).price);
        assertEquals(10, products.findByName("Apple").getStock());
    }

    @Test
    public void execute_addExistingStock_success() throws SystemException {
        StockList stocks = new StockList();
        ProductList products = new ProductList();
        Product banana = new Product("Banana", 3.00);
        banana.setStock(5);
        products.add(banana);
        stocks.add(new Stock("Banana", 3.00, 5));

        AddStockCommand command = new AddStockCommand("Banana", 10);
        command.execute(products, new OrderList(), new ExpenseList(), stocks, new Ui());

        assertEquals(1, stocks.getStocks().size());
        assertEquals("Banana", stocks.getStocks().get(0).product);
        assertEquals(15, stocks.getStocks().get(0).quantity); // 5 + 10
        assertEquals(BigDecimal.valueOf(3.00).setScale(2), stocks.getStocks().get(0).price);
        assertEquals(15, products.findByName("Banana").getStock());
    }

    @Test
    public void execute_removeStock_success() throws SystemException {
        StockList stocks = new StockList();
        ProductList products = new ProductList();
        Product orange = new Product("Orange", 4.00);
        orange.setStock(15);
        products.add(orange);
        stocks.add(new Stock("Orange", 3.0, 15));

        RemoveStockCommand command = new RemoveStockCommand("Orange", 5, false);
        command.execute(products, new OrderList(), new ExpenseList(), stocks, new Ui());

        assertEquals(1, stocks.getStocks().size());
        assertEquals("Orange", stocks.getStocks().get(0).product);
        assertEquals(10, stocks.getStocks().get(0).quantity); // 15 - 5
        assertEquals(10, products.findByName("Orange").getStock());
    }

    @Test
    public void execute_missingProduct_throwsSystemException() {
        StockList stocks = new StockList();
        ProductList products = new ProductList();
        AddStockCommand command = new AddStockCommand("Apple", 10);

        assertThrows(SystemException.class, () ->
                command.execute(products, new OrderList(), new ExpenseList(), stocks, new Ui()));
    }

    @Test
    public void addStock_invalidConstructorArguments_failAssertions() {
        assertThrows(AssertionError.class, () -> new AddStockCommand(null, 10));
        assertThrows(AssertionError.class, () -> new AddStockCommand("  ", 10));
        assertThrows(AssertionError.class, () -> new AddStockCommand("Apple", 0));
        assertThrows(AssertionError.class, () -> new AddStockCommand("Apple", -5));
    }

    @Test
    public void removeStock_invalidConstructorArguments_failAssertions() {
        assertThrows(AssertionError.class, () -> new RemoveStockCommand(null, 5, false));
        assertThrows(AssertionError.class, () -> new RemoveStockCommand("  ", 5, false));
        assertThrows(AssertionError.class, () -> new RemoveStockCommand("Apple", 0, false));
        assertThrows(AssertionError.class, () -> new RemoveStockCommand("Apple", -2, false));
    }

    @Test
    public void listStock_nullArguments_failAssertions() {
        ListStockCommand listCommand = new ListStockCommand();
        assertThrows(AssertionError.class, () ->
                listCommand.execute(new ProductList(), new OrderList(), new ExpenseList(), null, new Ui()));
        assertThrows(AssertionError.class, () ->
                listCommand.execute(new ProductList(), new OrderList(), new ExpenseList(), new StockList(), null));
    }

    @Test
    public void execute_lowStockDefaultThreshold_filtersCorrectly() throws SystemException {
        StockList stocks = new StockList();
        ProductList products = new ProductList();

        Product apple = new Product("Apple", 2.00);
        apple.setStock(3);
        products.add(apple);
        stocks.add(new Stock("Apple", 2.00, 3));

        Product banana = new Product("Banana", 1.50);
        banana.setStock(10);
        products.add(banana);
        stocks.add(new Stock("Banana", 1.50, 10));

        Product orange = new Product("Orange", 3.00);
        orange.setStock(5);
        products.add(orange);
        stocks.add(new Stock("Orange", 3.00, 5));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Ui testUi = new Ui(System.in, new PrintStream(out, true, StandardCharsets.UTF_8));

        LowStockCommand command = new LowStockCommand();
        assertEquals(5, command.getThreshold());
        command.execute(products, new OrderList(), new ExpenseList(), stocks, testUi);

        String output = out.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Low stock items (quantity <= 5):"));
        assertTrue(output.contains("Apple"));
        assertTrue(output.contains("Orange"));
        assertTrue(!output.contains("Banana"));
    }

    @Test
    public void execute_lowStockCustomThreshold_filtersCorrectly() throws SystemException {
        StockList stocks = new StockList();
        ProductList products = new ProductList();

        Product apple = new Product("Apple", 2.00);
        apple.setStock(2);
        products.add(apple);
        stocks.add(new Stock("Apple", 2.00, 2));

        Product banana = new Product("Banana", 1.50);
        banana.setStock(4);
        products.add(banana);
        stocks.add(new Stock("Banana", 1.50, 4));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Ui testUi = new Ui(System.in, new PrintStream(out, true, StandardCharsets.UTF_8));

        LowStockCommand command = new LowStockCommand(2);
        assertEquals(2, command.getThreshold());
        command.execute(products, new OrderList(), new ExpenseList(), stocks, testUi);

        String output = out.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Low stock items (quantity <= 2):"));
        assertTrue(output.contains("Apple"));
        assertTrue(!output.contains("Banana"));
    }

    @Test
    public void execute_lowStockEmptyList_showsNoStockMessage() throws SystemException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Ui testUi = new Ui(System.in, new PrintStream(out, true, StandardCharsets.UTF_8));

        LowStockCommand command = new LowStockCommand(5);
        command.execute(new ProductList(), new OrderList(), new ExpenseList(), new StockList(), testUi);

        String output = out.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("There are currently no stocks in the list."));
    }

    @Test
    public void execute_lowStockNoLowItems_showsSufficientStockMessage() throws SystemException {
        StockList stocks = new StockList();
        ProductList products = new ProductList();

        Product banana = new Product("Banana", 1.50);
        banana.setStock(10);
        products.add(banana);
        stocks.add(new Stock("Banana", 1.50, 10));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Ui testUi = new Ui(System.in, new PrintStream(out, true, StandardCharsets.UTF_8));

        LowStockCommand command = new LowStockCommand(5);
        command.execute(products, new OrderList(), new ExpenseList(), stocks, testUi);

        String output = out.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("All products have sufficient stock (more than 5)."));
    }

    @Test
    public void execute_lowStockSyncsWithProductStock() throws SystemException {
        StockList stocks = new StockList();
        ProductList products = new ProductList();

        Product apple = new Product("Apple", 2.00);
        apple.setStock(2);
        products.add(apple);
        Stock appleStock = new Stock("Apple", 2.00, 10);
        stocks.add(appleStock);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Ui testUi = new Ui(System.in, new PrintStream(out, true, StandardCharsets.UTF_8));

        LowStockCommand command = new LowStockCommand(5);
        command.execute(products, new OrderList(), new ExpenseList(), stocks, testUi);

        assertEquals(2, appleStock.quantity);
        String output = out.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Low stock items (quantity <= 5):"));
        assertTrue(output.contains("Apple"));
    }

    @Test
    public void lowStock_invalidConstructorArguments_failAssertions() {
        assertThrows(AssertionError.class, () -> new LowStockCommand(-1));
        assertThrows(AssertionError.class, () -> new LowStockCommand(-10));
    }

    @Test
    public void lowStock_nullArguments_failAssertions() {
        LowStockCommand command = new LowStockCommand();
        assertThrows(AssertionError.class, () ->
                command.execute(null, new OrderList(), new ExpenseList(), new StockList(), new Ui()));
        assertThrows(AssertionError.class, () ->
                command.execute(new ProductList(), new OrderList(), new ExpenseList(), null, new Ui()));
        assertThrows(AssertionError.class, () ->
                command.execute(new ProductList(), new OrderList(), new ExpenseList(), new StockList(), null));
    }
}

