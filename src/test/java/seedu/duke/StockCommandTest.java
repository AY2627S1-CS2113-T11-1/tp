package seedu.duke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import seedu.duke.command.AddOrderCommand;
import seedu.duke.command.CancelOrderCommand;
import seedu.duke.command.EditOrderCommand;

import org.junit.jupiter.api.Test;

import seedu.duke.command.AddStockCommand;
import seedu.duke.command.ListStockCommand;
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
        assertEquals(2.00, stocks.getStocks().get(0).price);
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
        assertEquals(3.00, stocks.getStocks().get(0).price);
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
    public void execute_orderAddDeductsStockListQuantity() throws SystemException {
        ProductList products = new ProductList();
        Product apple = new Product("Apple", 1.50);
        products.add(apple);

        StockList stocks = new StockList();
        AddStockCommand restock = new AddStockCommand("Apple", 10);
        restock.execute(products, new OrderList(), new ExpenseList(), stocks, new Ui());

        assertEquals(10, stocks.findByProduct("Apple").getQuantity());

        AddOrderCommand orderCmd = new AddOrderCommand("Alice",
                List.of(new AddOrderCommand.RequestedItem("Apple", 4)));
        OrderList orders = new OrderList();
        orderCmd.execute(products, orders, new ExpenseList(), stocks, new Ui());

        assertEquals(6, apple.getStock());
        assertEquals(6, stocks.findByProduct("Apple").getQuantity());
    }

    @Test
    public void execute_orderCancelRestoresStockListQuantity() throws SystemException {
        ProductList products = new ProductList();
        Product apple = new Product("Apple", 1.50);
        products.add(apple);

        StockList stocks = new StockList();
        AddStockCommand restock = new AddStockCommand("Apple", 10);
        restock.execute(products, new OrderList(), new ExpenseList(), stocks, new Ui());

        OrderList orders = new OrderList();
        AddOrderCommand orderCmd = new AddOrderCommand("Alice",
                List.of(new AddOrderCommand.RequestedItem("Apple", 4)));
        orderCmd.execute(products, orders, new ExpenseList(), stocks, new Ui());

        assertEquals(6, stocks.findByProduct("Apple").getQuantity());

        CancelOrderCommand cancelCmd = new CancelOrderCommand(1);
        cancelCmd.execute(products, orders, new ExpenseList(), stocks, new Ui());

        assertEquals(10, apple.getStock());
        assertEquals(10, stocks.findByProduct("Apple").getQuantity());
    }

    @Test
    public void execute_orderEditUpdatesStockListQuantity() throws SystemException {
        ProductList products = new ProductList();
        Product apple = new Product("Apple", 1.50);
        products.add(apple);

        StockList stocks = new StockList();
        AddStockCommand restock = new AddStockCommand("Apple", 10);
        restock.execute(products, new OrderList(), new ExpenseList(), stocks, new Ui());

        OrderList orders = new OrderList();
        AddOrderCommand orderCmd = new AddOrderCommand("Alice",
                List.of(new AddOrderCommand.RequestedItem("Apple", 2)));
        orderCmd.execute(products, orders, new ExpenseList(), stocks, new Ui());

        assertEquals(8, stocks.findByProduct("Apple").getQuantity());

        // Edit quantity from 2 to 5 (deducting 3 more)
        EditOrderCommand editCmd = new EditOrderCommand(1, null, null, 5);
        editCmd.execute(products, orders, new ExpenseList(), stocks, new Ui());

        assertEquals(5, apple.getStock());
        assertEquals(5, stocks.findByProduct("Apple").getQuantity());
    }

    @Test
    public void stock_constructorAndGetters_enforceInvariants() {
        Product product = new Product("Apple", 2.50);
        Stock stockWithProduct = new Stock(product, 15);
        assertEquals("Apple", stockWithProduct.getProduct());
        assertEquals(2.50, stockWithProduct.getPrice());
        assertEquals(15, stockWithProduct.getQuantity());

        stockWithProduct.setQuantity(20);
        assertEquals(20, stockWithProduct.getQuantity());
        assertEquals(20, product.getStock());

        stockWithProduct.setPrice(3.00);
        assertEquals(3.00, stockWithProduct.getPrice());

        Stock rawStock = new Stock("Banana", 1.20, 8);
        assertEquals("Banana", rawStock.getProduct());
        assertEquals(1.20, rawStock.getPrice());
        assertEquals(8, rawStock.getQuantity());

        assertThrows(AssertionError.class, () -> new Stock((Product) null, 5));
        assertThrows(AssertionError.class, () -> new Stock(product, -1));
        assertThrows(AssertionError.class, () -> new Stock((String) null, 1.0, 5));
        assertThrows(AssertionError.class, () -> new Stock("   ", 1.0, 5));
        assertThrows(AssertionError.class, () -> new Stock("Apple", -0.5, 5));
        assertThrows(AssertionError.class, () -> new Stock("Apple", 1.0, -2));
    }

    @Test
    public void stockList_encapsulationAndInvariants() {
        StockList stockList = new StockList();
        assertEquals(0, stockList.size());
        assertThrows(AssertionError.class, () -> stockList.add(null));
        assertThrows(AssertionError.class, () -> stockList.findByProduct(null));

        Stock stock = new Stock("Apple", 2.0, 5);
        stockList.add(stock);
        assertEquals(1, stockList.size());
        assertNotNull(stockList.findByProduct("apple"));
        assertNull(stockList.findByProduct("banana"));

        // Verify unmodifiable view protects internal state
        assertThrows(UnsupportedOperationException.class, () ->
                stockList.getStocks().add(new Stock("Orange", 3.0, 1)));
    }
}

