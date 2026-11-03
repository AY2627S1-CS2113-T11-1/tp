package seedu.duke;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import seedu.duke.command.RemoveStockCommand;

/**
 * Verifies that stock-removal messages report the actual quantity deducted.
 */
class RemoveStockReportingTest {
    @Test
    public void execute_requestExceedsStock_reportsAvailableQuantity() throws SystemException {
        checkRemoval(3, 10, false, "Removes 3 units of Bread", 0);
    }

    @Test
    public void execute_defaultRemovalFromEmptyStock_reportsZero() throws SystemException {
        checkRemoval(0, 1, true, "Removes 0 units of Bread", 0);
    }

    @Test
    public void execute_defaultRemovalWithStock_preservesDefaultMessage() throws SystemException {
        checkRemoval(3, 1, true, "Removes 1 unit (default behaviour) of Bread", 2);
    }

    /** Runs a removal and checks its message and both inventory representations. */
    private void checkRemoval(int initial, int requested, boolean isDefault, String message, int expected)
            throws SystemException {
        ProductList products = new ProductList();
        Product bread = new Product("Bread", 2);
        bread.setStock(initial);
        products.add(bread);
        StockList stocks = new StockList();
        stocks.add(new Stock(bread, initial));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(new ByteArrayInputStream(new byte[0]),
                new PrintStream(output, true, StandardCharsets.UTF_8));

        new RemoveStockCommand("Bread", requested, isDefault)
                .execute(products, new OrderList(), new ExpenseList(), stocks, ui);

        assertEquals("    " + message + System.lineSeparator(), output.toString(StandardCharsets.UTF_8));
        assertEquals(expected, bread.getStock());
        assertEquals(expected, stocks.findByProduct("Bread").getQuantity());
    }
}
