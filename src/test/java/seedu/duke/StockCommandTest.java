package seedu.duke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.duke.command.AddStockCommand;
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
}
