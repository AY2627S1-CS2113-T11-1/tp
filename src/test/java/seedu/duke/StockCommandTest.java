package seedu.duke;

import org.junit.jupiter.api.Test;
import seedu.duke.command.AddStockCommand;
import seedu.duke.command.RemoveStockCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;

class StockCommandTest {

    @Test
    public void execute_addNewStock_success() throws SystemException {
        StockList stocks = new StockList();
        ProductList products = new ProductList();
        AddStockCommand command = new AddStockCommand("Apple", 1.50, 10);

        command.execute(products, new OrderList(), new ExpenseList(), stocks, new Ui());

        assertEquals(1, stocks.getStocks().size());
        assertEquals("Apple", stocks.getStocks().get(0).product);
        assertEquals(10, stocks.getStocks().get(0).quantity);
        assertEquals(1.50, stocks.getStocks().get(0).price);
    }
    
    @Test
    public void execute_addExistingStock_success() throws SystemException {
        StockList stocks = new StockList();
        ProductList products = new ProductList();
        stocks.add(new Stock("Banana", 2.0, 5));
        
        AddStockCommand command = new AddStockCommand("Banana", 2.50, 10);
        command.execute(products, new OrderList(), new ExpenseList(), stocks, new Ui());

        assertEquals(1, stocks.getStocks().size());
        assertEquals("Banana", stocks.getStocks().get(0).product);
        assertEquals(15, stocks.getStocks().get(0).quantity); // 5 + 10
        assertEquals(2.50, stocks.getStocks().get(0).price); // updated price
    }
    
    @Test
    public void execute_removeStock_success() throws SystemException {
        StockList stocks = new StockList();
        ProductList products = new ProductList();
        stocks.add(new Stock("Orange", 3.0, 15));
        
        RemoveStockCommand command = new RemoveStockCommand("Orange", 5, false);
        command.execute(products, new OrderList(), new ExpenseList(), stocks, new Ui());

        assertEquals(1, stocks.getStocks().size());
        assertEquals("Orange", stocks.getStocks().get(0).product);
        assertEquals(10, stocks.getStocks().get(0).quantity); // 15 - 5
    }
}
