package seedu.duke.command;

import java.math.BigDecimal;

import seedu.duke.StockList;
import seedu.duke.Expense;
import seedu.duke.ExpenseList;
import seedu.duke.OrderList;
import seedu.duke.ProductList;
import seedu.duke.Ui;

/**
 * Adds an operating expense to the expense list.
 *
 * Corresponds to {@code expense add p/PRODUCT a/AMOUNT}.
 */
public class AddExpenseCommand extends Command {
    private final String productName;
    private final BigDecimal amount;

    /**
     * Creates an add-expense command.
     *
     * @param productName the product associated with the expense
     * @param amount the expense amount in dollars
     */
    public AddExpenseCommand(String productName, BigDecimal amount) {
        this.productName = productName;
        this.amount = amount;
    }

    /** Convenience boundary overload for callers that still provide a decimal literal. */
    public AddExpenseCommand(String productName, double amount) {
        this(productName, BigDecimal.valueOf(amount));
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public void execute(ProductList products, OrderList orders, ExpenseList expenses, StockList stocks, Ui ui) {
        assert expenses != null : "Expense list must be available when adding an expense";
        expenses.add(new Expense(productName, amount));
    }
}
